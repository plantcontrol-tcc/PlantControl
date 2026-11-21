package com.plantcontrol.app.data.local.prefs

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore by preferencesDataStore(name = "plantcontrol_settings")

class SettingsRepository(context: Context) {
    private val dataStore = context.dataStore

    private object PreferencesKeys {
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val IS_HIGH_CONTRAST = booleanPreferencesKey("is_high_contrast")
        val FONT_SCALE = floatPreferencesKey("font_scale")
        val TALK_BACK_HELP = booleanPreferencesKey("talk_back_help")
        val ESP32_IP = stringPreferencesKey("esp32_ip")
        val CONNECTION_MODE = stringPreferencesKey("connection_mode")
        val LIMITE_MIN = intPreferencesKey("limite_min")
        val LIMITE_MAX = intPreferencesKey("limite_max")
        val SELECTED_PLANT_ID = intPreferencesKey("selected_plant_id")
    }

    val preferencesFlow: Flow<PlantSettings> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            PlantSettings(
                isDarkMode = preferences[PreferencesKeys.IS_DARK_MODE] ?: false,
                isHighContrast = preferences[PreferencesKeys.IS_HIGH_CONTRAST] ?: false,
                fontScale = preferences[PreferencesKeys.FONT_SCALE] ?: 1.0f,
                talkBackHelpEnabled = preferences[PreferencesKeys.TALK_BACK_HELP] ?: true,
                esp32Ip = preferences[PreferencesKeys.ESP32_IP] ?: "192.168.4.1",
                connectionMode = preferences[PreferencesKeys.CONNECTION_MODE] ?: "Wi-Fi (Access Point)",
                limiteMin = preferences[PreferencesKeys.LIMITE_MIN] ?: 40,
                limiteMax = preferences[PreferencesKeys.LIMITE_MAX] ?: 60,
                selectedPlantId = preferences[PreferencesKeys.SELECTED_PLANT_ID] ?: 1
            )
        }

    suspend fun updateDarkMode(enabled: Boolean) {
        dataStore.edit { it[PreferencesKeys.IS_DARK_MODE] = enabled }
    }

    suspend fun updateHighContrast(enabled: Boolean) {
        dataStore.edit { it[PreferencesKeys.IS_HIGH_CONTRAST] = enabled }
    }

    suspend fun updateFontScale(scale: Float) {
        dataStore.edit { it[PreferencesKeys.FONT_SCALE] = scale }
    }

    suspend fun updateTalkBackHelp(enabled: Boolean) {
        dataStore.edit { it[PreferencesKeys.TALK_BACK_HELP] = enabled }
    }

    suspend fun updateEsp32Ip(ip: String) {
        dataStore.edit { it[PreferencesKeys.ESP32_IP] = ip }
    }

    suspend fun updateConnectionMode(mode: String) {
        dataStore.edit { it[PreferencesKeys.CONNECTION_MODE] = mode }
    }

    suspend fun updateThresholds(min: Int, max: Int) {
        dataStore.edit {
            it[PreferencesKeys.LIMITE_MIN] = min
            it[PreferencesKeys.LIMITE_MAX] = max
        }
    }

    suspend fun updateSelectedPlantId(id: Int) {
        dataStore.edit { it[PreferencesKeys.SELECTED_PLANT_ID] = id }
    }

    suspend fun restoreDefaults() {
        dataStore.edit { it.clear() }
    }
}

data class PlantSettings(
    val isDarkMode: Boolean,
    val isHighContrast: Boolean,
    val fontScale: Float,
    val talkBackHelpEnabled: Boolean,
    val esp32Ip: String,
    val connectionMode: String,
    val limiteMin: Int,
    val limiteMax: Int,
    val selectedPlantId: Int
)