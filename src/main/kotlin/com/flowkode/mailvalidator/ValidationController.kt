package com.flowkode.mailvalidator

import spark.kotlin.get


class ValidationController(validationService: ValidationService, json: JsonResponseTransformer) {
    init {
        get("/validate/:emailAddress}", "*/*", json) {
            validationService.validateEmail(params(":emailAddress"))
        }
    }
}
