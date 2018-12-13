package com.flowkode.mailvalidator

class Email(rawEmail: String) {
    companion object {
        val regex = "@".toRegex()
    }

    val fullAddress: String

    val domain: String

    val localPart: String

    init {
        val chunks = rawEmail.split(regex)
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()

        if (chunks.size != 2) {
            throw InvalidEmailException()
        }
        fullAddress = rawEmail
        localPart = chunks[0]
        domain = chunks[1]
    }
}
