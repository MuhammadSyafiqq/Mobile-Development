package com.example.dicodingstory.utils

import org.json.JSONObject
import org.json.JSONException

object SimplifiedMessage {
    fun get(stringMessage: String): HashMap<String, String> {
        val messages = HashMap<String, String>()

        try {
            val jsonObject = JSONObject(stringMessage)
            if (jsonObject.has("message")) {
                messages["message"] = jsonObject.getString("message")
            } else {
                messages["message"] = "Unexpected error occurred. Please try again."
            }
        } catch (e: JSONException) {
            messages["message"] = "Unexpected error occurred. Please try again."
        }

        return messages
    }
}
