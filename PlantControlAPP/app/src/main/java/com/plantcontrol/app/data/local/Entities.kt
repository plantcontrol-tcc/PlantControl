package com.plantcontrol.app.data.local

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plants")
data class PlantEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val scientificName: String,
    @DrawableRes val imageRes: Int, // Referência oficial do Android (R.drawable.nome_do_arquivo)
    val minHumidity: Int,
    val maxHumidity: Int,
    val lightRequirement: String = "Luz difusa / Meia-sombra",
    val irrigationType: String = "Irrigação moderada",
    val soilType: String = "Substrato drenante com matéria orgânica",
    val fullDescription: String = "",
    val curiosities: String = "",
    val idealTempMin: Float = 18f,
    val idealTempMax: Float = 30f,
    val waterConsumptionLitersDay: Float = 0.5f
)

@Entity(tableName = "event_history")
data class EventLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: String,
    val description: String,
    val isEmergency: Boolean = false
)