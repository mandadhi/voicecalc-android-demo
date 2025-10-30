package com.mandadhi.voicecalc

object MathParser {
    private val numberWords = mapOf(
        "zero" to "0", "one" to "1", "two" to "2", "three" to "3",
        "four" to "4", "five" to "5", "six" to "6", "seven" to "7",
        "eight" to "8", "nine" to "9", "ten" to "10"
    )

    fun normalizeForLocalEval(input: String): String {
        var s = input.lowercase().trim()
        s = s.replace("divided by", "/")
        s = s.replace("over", "/")
        s = s.replace("times", "*")
        s = s.replace("x", "*")
        s = s.replace("plus", "+")
        s = s.replace("minus", "-")
        s = s.replace("percent of".toRegex(), "/100*")
        s = s.replace("percent", "/100")
        for ((w, d) in numberWords) { s = s.replace(Regex("\\b" + Regex.escape(w) + "\\b"), d) }
        s = s.replace(Regex("(\\d+) point (\\d+)")) { m -> "${m.groupValues[1]}.${m.groupValues[2]}" }
        s = s.replace(Regex("what is|calculate|equals|=|please|compute"), "")
        return s.trim()
    }

    fun isSimpleExpression(input: String): Boolean {
        val normalized = normalizeForLocalEval(input)
        return normalized.matches(Regex("^[0-9+\\-*/(). %]+$")) && normalized.length <= 60
    }
}
