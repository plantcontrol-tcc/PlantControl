package com.plantcontrol.app.domain.algorithms

object AgronomicCalculations {

    /**
     * Previsão de irrigação: calcula estimativa de tempo (em horas) até o solo secar
     */
    fun estimarHorasAteSecar(umidadeAtual: Int, umidadeMin: Int, temp: Float, luzLux: Int): Float {
        if (umidadeAtual <= umidadeMin) return 0f
        val taxaEvaporacaoBase = 1.2f
        val fatorTemp = if (temp > 28) 1.5f else 1.0f
        val fatorLuz = if (luzLux > 1000) 1.3f else 0.9f
        val perdaPorHora = taxaEvaporacaoBase * fatorTemp * fatorLuz
        return (umidadeAtual - umidadeMin) / perdaPorHora
    }

    /**
     * Compensação de limites de umidade por temperatura e luminosidade
     */
    fun compensarLimites(limiteMinBase: Int, limiteMaxBase: Int, temp: Float, luzLux: Int): Pair<Int, Int> {
        var acrescimo = 0
        if (temp > 32f) acrescimo += 5
        if (luzLux > 1200) acrescimo += 3
        return Pair(limiteMinBase + acrescimo, limiteMaxBase + acrescimo)
    }

    /**
     * Cálculo de consumo de água (L = vazão L/s * tempo seg)
     */
    fun calcularConsumoLitros(tempoAcionamentoSegundos: Long, vazaoMlPorSegundo: Float = 25f): Float {
        return (tempoAcionamentoSegundos * vazaoMlPorSegundo) / 1000f
    }
}