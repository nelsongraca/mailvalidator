package org.knowledgeworks.pt

import spark.kotlin.get
import spark.kotlin.internalServerError
import spark.kotlin.notFound
import java.util.*


fun main(args: Array<String>) {
    Main().run()
}

class Main {
    fun run() {
        get("/isValid/:email") {
            val emailUtils = EmailUtils()
            //val email = askEmail()

            emailUtils.validateEmail(params(":email"))


            /*//todo: implement this, but better
            internalServerError {
                "{\"message\":\"Custom 500 internal server error\"}"
            }

            notFound {
                "{\"message\":\"Custom 404 not found\"}"
            }
            get("/throwexception") {throw YourCustomException() }
            */
        }

    }
}