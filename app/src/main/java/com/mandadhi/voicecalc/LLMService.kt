package com.mandadhi.voicecalc

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class LLMService {
    private val client = OkHttpClient()

    /**
     * Ask Gemini via REST generateContent endpoint.
     * Uses BuildConfig.GEMINI_API_KEY set in app/build.gradle.kts for testing.
     * Replace with server-side proxy for production.
     */
    fun ask(prompt: String, useOpenAI: Boolean = false): String {
        if (!useOpenAI) {
            val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }
            if (apiKey.isBlank()) return JSONObject(mapOf("error" to "Gemini API key not configured")).toString()

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-pro:generateContent"
            val payload = JSONObject()
            val contents = org.json.JSONArray()
            val userObj = JSONObject()
            userObj.put("role", "user")
            val parts = org.json.JSONArray()
            parts.put(JSONObject().put("text", prompt))
            userObj.put("parts", parts)
            contents.put(userObj)
            payload.put("contents", contents)

            val body = payload.toString().toRequestBody("application/json".toMediaTypeOrNull())
            val req = Request.Builder()
                .url(url)
                .addHeader("x-goog-api-key", apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build()

            client.newCall(req).execute().use { resp ->
                val bodyStr = resp.body?.string() ?: ""
                // Try to pull plaintext out of 'candidates' -> content -> parts -> text
                try {
                    val j = JSONObject(bodyStr)
                    val candidates = j.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val c0 = candidates.getJSONObject(0)
                        val content = c0.optJSONObject("content")
                        if (content != null) {
                            val parts = content.optJSONArray("parts")
                            if (parts != null && parts.length() > 0) {
                                val text = parts.getJSONObject(0).optString("text", "")
                                return text
                            }
                        }
                    }
                } catch (e: Exception) {
                    // ignore and return full body
                }
                return bodyStr
            }
        } else {
            return JSONObject(mapOf("error" to "OpenAI path not implemented")).toString()
        }
    }

    companion object {
        /**
         * Parse response: prefer {result, explanation} JSON; fallback to extracting first number + explanation.
         */
        fun parseLLMResponse(response: String): Pair<String, String> {
            try {
                val obj = JSONObject(response)
                val result = if (obj.has("result")) obj.optString("result") else ""
                val explanation = obj.optString("explanation", "")
                if (result.isNotBlank()) return Pair(result, explanation)
            } catch (_: Exception) {}
            // fallback: extract first numeric value as result
            val num = Regex("(-?[0-9]+(?:\\.[0-9]+)?)").find(response)?.value ?: ""
            return Pair(if (num.isBlank()) "?" else num, response.replace("\\s+".toRegex()," ").trim())
        }
    }
}
