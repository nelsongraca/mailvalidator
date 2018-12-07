package org.knowledgeworks.pt

import com.google.gson.Gson
import spark.ResponseTransformer

class JsonResponseTransformer : ResponseTransformer {

    private val gson = Gson()

    override fun render(model: Any?): String {
        return gson.toJson(model)
    }

}