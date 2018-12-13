package com.flowkode.mailvalidator

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.SystemExitException
import org.koin.Koin
import org.koin.dsl.module.applicationContext
import org.koin.log.EmptyLogger
import org.koin.spark.controller
import org.koin.spark.runControllers
import org.koin.spark.start
import org.koin.standalone.KoinComponent


fun main(args: Array<String>) {
    Koin.logger = EmptyLogger()
    Main().run(args)
}

class Main : KoinComponent {
    fun run(args: Array<String>) {
        try {
            ArgParser(args).parseInto(::MailValidatorArgs).run {
                val appModule = applicationContext {
                    bean { ValidationService("who@who.com") }
                    bean { JsonResponseTransformer() }
                    controller { ValidationController(get(), get()) }
                }
                when {
//                    generatePassword -> {
//                        startKoin(listOf(helloAppModule))
//                        val authService: AuthService by inject()
//                        println(authService.encryptPassword(askPassword()))
//                    }
                    else -> start(modules = listOf(appModule), port = 9999) {
                        runControllers()
                    }
                }
                return
            }
        }
        catch (ex: InvalidArgumentException) {
            println(ex.errorMessage)
        }
        catch (ex: SystemExitException) {
            ex.printAndExit("mailValidator")
        }
    }
}
