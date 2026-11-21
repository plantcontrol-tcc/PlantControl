package com.plantcontrol.app.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class Esp32State(
    val umidade: Int = 52,
    val temAgua: Boolean = true,
    val bombaLigada: Boolean = false,
    val modoAutomatico: Boolean = true,
    val limiteMin: Int = 40,
    val limiteMax: Int = 60,
    val temperatura: Float = 27.5f,
    val luminosidadeLux: Int = 650
)

data class ThresholdConfig(val min: Int, val max: Int)

interface Esp32Api {
    @GET("/status")
    suspend fun getStatus(): Esp32State

    @POST("/irrigar_manual")
    suspend fun irrigarManual(): Esp32State

    @POST("/emergencia")
    suspend fun emergencia(): Esp32State

    @POST("/config")
    suspend fun setConfig(@Body config: ThresholdConfig): Esp32State
}