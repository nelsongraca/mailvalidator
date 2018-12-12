package com.flowkode.mailvalidator

import spark.kotlin.get


fun main(args: Array<String>) {
    Main().run()
}

class Main {
    fun run() {
        get("/isValid/:email") {
            val emailUtils = EmailUtils()
            emailUtils.validateEmail(params(":email"))
        }
    }
}
