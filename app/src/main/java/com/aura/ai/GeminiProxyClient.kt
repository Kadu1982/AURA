package com.aura.ai

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiProxyClient(
    private val baseUrl: String,
    private val token: String
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(45, TimeUnit.SECONDS)
        .build()

    /**
     * System prompt que define a personalidade JARVIS
     */
    private fun buildSystemPrompt(): String {
        return """
            Você é JARVIS, o assistente pessoal sofisticado e altamente inteligente.

            PERSONALIDADE:
            - Trate o usuário sempre como "senhor" ou "senhora"
            - Use linguagem formal e elegante, inspirada no mordomo britânico
            - Seja conciso mas eloquente - não seja prolixo
            - Mantenha tom respeitoso, profissional e levemente carismático
            - Demonstre inteligência através de análises precisas e sugestões pertinentes
            - Nunca seja servil demais, mas sempre cortês

            ESTILO DE COMUNICAÇÃO:
            - Inicie respostas com: "Senhor", "Certamente", "Permita-me", "Com todo respeito"
            - Finalize com: "Às suas ordens", "Feito, senhor", "Mais alguma coisa?"
            - Use frases como: "Permita-me sugerir", "Se me permite", "Com a devida vênia"
            - Seja proativo: sugira soluções, antecipe necessidades

            EXEMPLOS DE RESPOSTAS:

            Pergunta: "Que horas são?"
            Resposta: "São 15h30, senhor."

            Pergunta: "Abra o YouTube"
            Resposta: "Abrindo YouTube, senhor."

            Pergunta: "Como está o clima?"
            Resposta: "Permita-me verificar... A previsão indica 25°C com sol, senhor. Um dia agradável."

            Pergunta: "Envie mensagem para João"
            Resposta: "Certamente, senhor. Qual mensagem deseja enviar?"

            Erro: "Não consegui executar"
            Resposta: "Lamento, senhor, mas encontrei dificuldades para executar essa operação."

            REGRAS:
            1. SEMPRE trate como "senhor" ou "senhora"
            2. Seja CONCISO - máximo 2-3 frases por resposta
            3. NUNCA use emojis ou linguagem informal
            4. Se não souber algo, admita elegantemente: "Lamento, senhor, mas não possuo essa informação"
            5. Para confirmações, use: "Confirma a operação, senhor?"
            6. Mantenha o tom Paul Bettany/JARVIS do MCU

            IMPORTANTE: Você está rodando em um dispositivo Android. Suas respostas serão faladas por TTS.
            Portanto, seja breve e direto, mas sempre mantendo a elegância.
        """.trimIndent()
    }

    /**
     * Constrói o prompt final com system instruction e comando do usuário
     */
    private fun buildFinalPrompt(userCommand: String): String {
        val systemPrompt = buildSystemPrompt()

        return """
            $systemPrompt

            ---

            Comando do usuário: $userCommand
        """.trimIndent()
    }

    fun chat(text: String, model: String = "gemini-2.5-flash"): String {
        return try {
            val cleanBase = baseUrl.trim().removeSuffix("/")
            if (!cleanBase.startsWith("http://") && !cleanBase.startsWith("https://")) {
                return "BaseURL inválida (use http://IP:8010)"
            }
            if (token.isBlank()) return "Token vazio (configure auraToken)"
            if (text.isBlank()) return "Prompt vazio"

            val mediaType = "application/json; charset=utf-8".toMediaType()

            // Construir prompt completo com personalidade JARVIS
            val finalPrompt = buildFinalPrompt(text)

            val payload = JSONObject()
                .put("text", finalPrompt)
                .put("model", model)

            val request = Request.Builder()
                .url("$cleanBase/chat")
                .addHeader("X-Aura-Token", token.trim())
                .addHeader("Content-Type", "application/json")
                .post(payload.toString().toRequestBody(mediaType))
                .build()

            client.newCall(request).execute().use { resp ->
                val body = resp.body?.string().orEmpty()

                if (!resp.isSuccessful) {
                    return "Erro HTTP ${resp.code}: ${body.take(250)}"
                }

                // candidates[0].content.parts[0].text
                try {
                    val root = JSONObject(body)
                    val candidates = root.getJSONArray("candidates")
                    val content = candidates.getJSONObject(0).getJSONObject("content")
                    val parts = content.getJSONArray("parts")
                    parts.getJSONObject(0).getString("text").trim()
                } catch (e: Exception) {
                    "Resposta inesperada: ${body.take(250)}"
                }
            }
        } catch (e: Exception) {
            "Falha rede/parse: ${e.javaClass.simpleName}: ${e.message}"
        }
    }
}
