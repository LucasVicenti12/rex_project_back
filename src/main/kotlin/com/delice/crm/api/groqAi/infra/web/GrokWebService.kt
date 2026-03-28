package com.delice.crm.api.groqAi.infra.web

import com.delice.crm.api.groqAi.service.GroqService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/ai")
class AiController(private val groqService: GroqService) {

    @PostMapping
    fun ask(@RequestBody req: Map<String, Any>): ResponseEntity<String> {
        val message = req["message"] as? String

        if (message.isNullOrBlank()) {
            return ResponseEntity.badRequest().body("O termo de busca é obrigatório e não pode estar vazio.")
        }

        return try {
            val response = groqService.askAi(message)
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            if (e.message?.contains("Erro na API Groq") == true) {
                ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Erro ao comunicar com a API Groq: ${e.message}")
            } else {
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor: ${e.message}")
            }
        }
    }
}