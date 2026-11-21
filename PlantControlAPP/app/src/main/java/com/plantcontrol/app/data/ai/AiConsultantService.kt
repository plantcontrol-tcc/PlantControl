package com.plantcontrol.app.data.ai

import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AiConsultantService(apiKey: String) {
    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey
    )

    suspend fun gerarDiagnostico(
        planta: String,
        umidade: Int,
        limiteMin: Int,
        limiteMax: Int,
        temperatura: Float,
        luminosidade: Int,
        bombaStatus: String,
        reservatorioStatus: String
    ): String = withContext(Dispatchers.IO) {
        val prompt = """
            Você é um agrônomo especialista no sistema IoT PlantControl.
            Analise os dados em tempo real da planta:
            - Cultura: $planta
            - Umidade do solo: $umidade% (Faixa ideal: $limiteMin% a $limiteMax%)
            - Temperatura atual: $temperatura °C
            - Luminosidade: $luminosidade Lux
            - Estado da Bomba: $bombaStatus
            - Reservatório: $reservatorioStatus

            Gere um relatório curto e direto estruturado em:
            1. Análise da Situação (Linguagem simples para o usuário comum).
            2. Padrões Detectados (Relação entre umidade, calor e luz).
            3. Recomendações Práticas de Manejo.
        """.trimIndent()

        try {
            val response = model.generateContent(prompt)
            response.text ?: "Não foi possível gerar a recomendação no momento."
        } catch (e: Exception) {
            "Erro ao comunicar com a IA: ${e.localizedMessage}. Verifique a conexão com a internet."
        }
    }
}