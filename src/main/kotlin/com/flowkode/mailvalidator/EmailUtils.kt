package com.flowkode.mailvalidator

/*
 * Copyright 2018 by KnowledgeWorks. All rights reserved.
 *
 * This software is the proprietary information of KnowledgeWorks
 *
 * Use is subject to license terms.
 *
 * http://www.knowledgeworks.pt
 *
 */

import org.xbill.DNS.Lookup
import org.xbill.DNS.Record
import org.xbill.DNS.TextParseException
import org.xbill.DNS.Type
import java.io.*
import java.net.Socket

class EmailUtils {

    private var emailtest = Email("", "", "", false)
    private var jsonResponseTransformer = JsonResponseTransformer()

    fun validateEmail(email: String): String {

        val chunks = email.split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()


        if (chunks.size != 2) {
            emailtest.address = "Invalid address"
            emailtest.domain = "Invalid domain"
            return jsonCreator()
        }

        emailtest.address = chunks[0]
        emailtest.domain = chunks[1]

        if (chunks[0].isEmpty() || chunks[1].length < 3) {
            return jsonCreator()
        }

        if (!chunks[1].contains(".")) {
            return jsonCreator()
        }

        if (email.contains("..") || email.contains(".@") || email.contains("@.") || email.contains("._.")) {
            return jsonCreator()
        }

        if (email.endsWith(".") || email.startsWith(".")) {
            return jsonCreator()
        }


        var record: Array<Record?> = arrayOfNulls<Record>(0)
        try {
            record = lookupMxRecords(chunks[1])
            if (record.isEmpty()) {
                return jsonCreator()
            }
        } catch (e: TextParseException) {
            println("ERROR --- Invalid email address")
        }

        return validateHosts(email, record)
    }

    private fun validateHosts(email: String, record: Array<Record?>): String {
        for (arecord in record) {
            var valid = false
            var targetServer: List<String> = emptyList()
            try {
                var res: Int
                targetServer = arecord!!.rdataToString().split(" ")
                val socket = Socket(targetServer[1], 25)
                val bufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
                val bufferedWriter = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
                res = hear(bufferedReader)
                if (res != 220) {
                    throw Exception("Invalid header")
                }
                say(bufferedWriter, "HELO here.com")
                res = hear(bufferedReader)
                if (res != 250) {
                    throw Exception("Not ESMTP")
                }
                say(bufferedWriter, "MAIL FROM:<me@here.com>")
                res = hear(bufferedReader)
                if (res != 250) {
                    throw Exception("Sender rejected")
                }
                say(bufferedWriter, "RCPT TO:<$email>")
                res = hear(bufferedReader)
                if (res != 250) {
                    throw Exception("Address is not valid!")
                }
                valid = true
                bufferedReader.close()
                bufferedWriter.close()
                socket.close()
            } catch (e: Exception) {
                //continue iteration
            } finally {
                if (valid) {
                    emailtest.server = targetServer[1]
                    emailtest.valid = valid
                    println("returned true")
                    return jsonCreator()
                }
            }
        }

        return jsonCreator()
    }

    private fun jsonCreator(): String {
        if (emailtest.server == "") {
            emailtest.server = "No server for this address"
        }

        return jsonResponseTransformer.render(emailtest)
    }

    @Throws(TextParseException::class)
    fun lookupMxRecords(domainPart: String): Array<Record?> {
        val dnsLookup = Lookup(domainPart, Type.MX)
        return dnsLookup.run()
    }

    @Throws(IOException::class)
    private fun hear(reader: BufferedReader): Int {
        var line: String? = null
        var res = 0
        while ({ line = reader.readLine(); line }() != null) {
            println(line)
            val pfx = line!!.substring(0, 3)
            try {
                res = Integer.parseInt(pfx)
            } catch (ex: Exception) {
                res = -1
            }

            if (line!![3] != '-') {
                break
            }
        }
        return res
    }

    @Throws(IOException::class)
    private fun say(wr: BufferedWriter, text: String) {
        wr.write(text + "\r\n")
        wr.flush()
        return
    }


}
