package com.mandadhi.voicecalc

import org.mariuszgromada.math.mxparser.Expression

class ExpressionEvaluator {
    fun evaluate(expr: String): String {
        if (expr.isBlank()) return ""
        return try {
            if (!expr.matches(Regex("^[0-9+\\-*/(). %]+$"))) {
                return "Unsupported characters"
            }
            val expression = Expression(expr)
            val value = expression.calculate()
            if (value.isNaN()) "Error" else value.toString()
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}
