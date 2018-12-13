package com.flowkode.mailvalidator

import org.slf4j.LoggerFactory
import org.xbill.DNS.Lookup
import org.xbill.DNS.MXRecord
import org.xbill.DNS.Type
import java.io.*
import java.net.InetSocketAddress
import java.net.Socket


class ValidationService(private val fromAddr: String) {
    private val fromDomain: String = fromAddr.split("@")[1]

    companion object {
        val LOGGER = LoggerFactory.getLogger(ValidationService.javaClass)
    }

    fun validateEmail(email: String): Boolean {
        try {
            val emailToCheck = Email(email)
            if (!isLocalPartValid(emailToCheck.localPart)) {
                return false
            }
            if (!isDomainValid(emailToCheck.domain)) {
                return false
            }
            if (!isFullEmailValid(emailToCheck.fullAddress)) {
                return false
            }
            if (!isSmtpValid(email, lookupMxRecords(emailToCheck.domain))) {
                return false
            }
        }
        catch (e: InvalidEmailException) {
            return false
        }
        catch (e: InvalidArgumentException) {
            return false
        }
        return true
    }

    private fun isFullEmailValid(fullAddress: String): Boolean {
        return !fullAddress.contains("..")
                && !fullAddress.contains(".@")
                && !fullAddress.contains("@.")
                && !fullAddress.contains("._.")
                && !fullAddress.endsWith(".")
                && !fullAddress.startsWith(".")
    }

    private fun isDomainValid(domain: String): Boolean {
        return domain.length >= 3 && domain.contains(".")
    }

    private fun isLocalPartValid(localPart: String) = localPart.isNotEmpty()

    private fun isSmtpValid(email: String, records: List<MXRecord>): Boolean {
        for (record in records) {
            try {
                val socket = Socket()
                socket.connect(InetSocketAddress(record.target.toString(), 25), 5000) //timeout of 5secs
                socket.soTimeout = 5000
                socket.use { sck ->
                    BufferedReader(InputStreamReader(sck.getInputStream())).use { bufferedReader ->
                        BufferedWriter(OutputStreamWriter(sck.getOutputStream())).use { bufferedWriter ->
                            if (read(bufferedReader) != 220) {
                                return false
                            }
                            write(bufferedWriter, "HELO $fromDomain")
                            if (read(bufferedReader) != 250) {
                                return false
                            }
                            write(bufferedWriter, "MAIL FROM:<$fromAddr>")
                            if (read(bufferedReader) != 250) {
                                return false
                            }
                            write(bufferedWriter, "RCPT TO:<$email>")
                            if (read(bufferedReader) != 250) {
                                return false
                            }
                            return true
                        }
                    }
                }
            }
            catch (e: Exception) {
                LOGGER.info(e.message)
                //continue iteration
            }
        }
        return false
    }

    private fun lookupMxRecords(domainPart: String): List<MXRecord> {
        val records: List<MXRecord>? = Lookup(domainPart, Type.MX).run()?.map { r -> r as MXRecord }
        if (records.isNullOrEmpty()) {
            throw InvalidArgumentException(InvalidArgumentException.Messages.NO_MX)
        }
        return records
    }

    @Throws(IOException::class)
    private fun read(reader: BufferedReader): Int {
        for (line in reader.readLines()) {
            LOGGER.debug("SMTP: $line")
            if (line[3] == ' ') {
                try {
                    return Integer.parseInt(line.substring(0, 3))
                }
                catch (ex: NumberFormatException) {
                    LOGGER.info("Could not decode code from: $line")
                }
            }
        }
        return -1
    }

    @Throws(IOException::class)
    private fun write(wr: BufferedWriter, text: String) {
        LOGGER.debug("Sent: $text")
        wr.write(text + "\r\n")
        wr.flush()
    }
}
