package com.delice.crm.api.groqAi.service

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class GroqService {

    @Value("\${groq.api.key}")
    private lateinit var apiKey: String

    private lateinit var webClient: WebClient

    @PostConstruct
    fun init() {
        webClient = WebClient.builder()
            .baseUrl("https://api.groq.com/openai/v1")
            .defaultHeader("Authorization", "Bearer $apiKey")
            .build()
    }

    // cached documentation text (loaded lazily)
    private val documentationText: String by lazy { loadDocumentationText() }

    /**
     * Lê o arquivo de documentação do sistema para ser enviado como contexto para a IA `CRM DELICE.txt`.
     */
    private fun loadDocumentationText(): String {
        val classLoader = this::class.java.classLoader
        val txtStream = classLoader.getResourceAsStream("\\static\\CRM DELICE.txt")
            ?: throw RuntimeException("Arquivo de documentação não encontrado em resources")

        return txtStream.bufferedReader().use { it.readText() }
    }

    private val baseSystemMessages: List<Map<String,String>> by lazy {
        listOf(
            mapOf("role" to "system",
                "content" to "Você é uma IA chatbot de um sistema CRM, responderá dúvidas de como usar o sistema, consultas no sistema entre outras atividades."),
            mapOf("role" to "system",
                "content" to documentationText)
        )
    }

    /**
     * Envia pergunta para IA junto com contexto padrão
     */
    fun askAi(question: String): String {
        // Sempre coloca as instruções padrão para o modelo manter o contexto
        val messages = ArrayList(baseSystemMessages)

        // Insere a pergunta na request para a IA
        messages.add(
            mapOf(
                "role" to "user",
                "content" to question
            )
        )

        // Monta o modelo da IA a ser usada junto com os prompts
        val requestBody = mapOf(
            "model" to "llama-3.3-70b-versatile",
            "messages" to messages
        )

        return try {
            val response = webClient.post()
                .uri("/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map::class.java)
                .block()

            val choices = response!!["choices"] as List<Map<String, Any>>
            val message = choices[0]["message"] as Map<String, String>

            message["content"]!!
        } catch (e: Exception) {
            when (e) {
                is org.springframework.web.reactive.function.client.WebClientResponseException -> {
                    throw RuntimeException("Erro na API Groq: ${e.statusCode} - ${e.responseBodyAsString}", e)
                }
                else -> {
                    throw RuntimeException("Erro interno: ${e.message}", e)
                }
            }
        }
    }
}