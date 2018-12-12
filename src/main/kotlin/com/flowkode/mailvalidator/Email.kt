package com.flowkode.mailvalidator

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class Email @JsonCreator
constructor(@param:JsonProperty("address") var address: String,
            @param:JsonProperty("domain") var domain: String,
            @param:JsonProperty("server") var server: String,
            @param:JsonProperty("valid") var valid: Boolean){


    override fun toString(): String {
        return ("Email{"
                + "address:" + address
                + ", domain:" + domain
                + ", server:" + server
                + ", valid:" + valid
                + '}'.toString())
    }
}
