package com.flowkode.mailvalidator

class InvalidArgumentException(message: Messages) : Exception() {
    val errorMessage = message.errorMessage

    enum class Messages(val errorMessage: String) {
        NO_MX("do me")
    }
}
