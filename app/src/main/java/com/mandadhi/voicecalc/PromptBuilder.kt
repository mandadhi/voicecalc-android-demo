package com.mandadhi.voicecalc

fun buildLLMPromptForCompute(userInput: String): String {
    return """
You are a helpful math assistant. The user asked: "$userInput"

Please solve this and respond with ONLY a JSON object in this exact format:
{"result": "numerical_answer", "explanation": "brief step-by-step explanation"}

Example:
User: "What is 25 percent of 80?"
Response: {"result": "20", "explanation": "25% of 80 = 0.25 × 80 = 20"}

Now solve: "$userInput"
""".trimIndent()
}
