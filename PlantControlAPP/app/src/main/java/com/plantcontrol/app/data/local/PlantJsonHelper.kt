package com.plantcontrol.app.data.local

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object PlantJsonHelper {
    private val gson = Gson()

    fun exportToJson(plants: List<PlantEntity>): String = gson.toJson(plants)

    fun importFromJson(jsonString: String): List<PlantEntity>? {
        return try {
            val type = object : TypeToken<List<PlantEntity>>() {}.type
            gson.fromJson<List<PlantEntity>>(jsonString, type)
        } catch (e: Exception) {
            null
        }
    }
}