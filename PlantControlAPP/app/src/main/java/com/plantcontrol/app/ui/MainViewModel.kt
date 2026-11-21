package com.plantcontrol.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.plantcontrol.app.data.ai.AiConsultantService
import com.plantcontrol.app.data.local.EventLogEntity
import com.plantcontrol.app.data.local.PlantDatabase
import com.plantcontrol.app.data.local.PlantEntity
import com.plantcontrol.app.data.local.prefs.SettingsRepository
import com.plantcontrol.app.data.remote.Esp32State
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepo = SettingsRepository(application)

    // --- CARREGA O CATÁLOGO DE FORMA ESCALÁVEL DO BANCO DE PLANTAS ---
    private val _plantList = MutableStateFlow(PlantDatabase.initialCatalog)
    val plantList: StateFlow<List<PlantEntity>> = _plantList.asStateFlow()

    private val _eventLogs = MutableStateFlow(
        listOf(EventLogEntity(1, "10:42", "Sistema inicializado e pronto.", false))
    )
    val eventLogs: StateFlow<List<EventLogEntity>> = _eventLogs.asStateFlow()

    private val _esp32State = MutableStateFlow(Esp32State())
    val esp32State: StateFlow<Esp32State> = _esp32State.asStateFlow()

    private val _selectedPlant = MutableStateFlow(_plantList.value[0])
    val selectedPlant: StateFlow<PlantEntity> = _selectedPlant.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _isHighContrast = MutableStateFlow(false)
    val isHighContrast: StateFlow<Boolean> = _isHighContrast.asStateFlow()

    private val _fontScale = MutableStateFlow(1.0f)
    val fontScale: StateFlow<Float> = _fontScale.asStateFlow()

    private val _talkBackHelpEnabled = MutableStateFlow(true)
    val talkBackHelpEnabled: StateFlow<Boolean> = _talkBackHelpEnabled.asStateFlow()

    private val _esp32Ip = MutableStateFlow("192.168.4.1")
    val esp32Ip: StateFlow<String> = _esp32Ip.asStateFlow()

    private val _connectionMode = MutableStateFlow("Wi-Fi (Access Point)")
    val connectionMode: StateFlow<String> = _connectionMode.asStateFlow()

    private val _aiReport = MutableStateFlow<String?>(null)
    val aiReport: StateFlow<String?> = _aiReport.asStateFlow()

    private val _isLoadingAi = MutableStateFlow(false)
    val isLoadingAi: StateFlow<Boolean> = _isLoadingAi.asStateFlow()

    private val aiService = AiConsultantService(apiKey = "TEST_SIMULATION_KEY")

    // O restante das funções do ViewModel (logEvent, onSliderHumidityChange, selectPlant, etc.)
    // continua EXATAMENTE IGUAL ao seu código anterior. Não apague as funções abaixo desta linha.

    init {
        // Carrega configurações persistidas e restaura a planta selecionada
        viewModelScope.launch {
            settingsRepo.preferencesFlow.collect { prefs ->
                _isDarkMode.value = prefs.isDarkMode
                _isHighContrast.value = prefs.isHighContrast
                _fontScale.value = prefs.fontScale
                _talkBackHelpEnabled.value = prefs.talkBackHelpEnabled
                _esp32Ip.value = prefs.esp32Ip
                _connectionMode.value = prefs.connectionMode

                // Restaura a planta salva pelo ID
                val savedPlant = _plantList.value.find { it.id == prefs.selectedPlantId } ?: _plantList.value[0]
                _selectedPlant.value = savedPlant

                _esp32State.value = _esp32State.value.copy(
                    limiteMin = prefs.limiteMin,
                    limiteMax = prefs.limiteMax
                )
            }
        }
    }

    fun logEvent(desc: String, isEmergency: Boolean = false) {
        val horaAtual = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val novoLog = EventLogEntity(
            id = _eventLogs.value.size + 1,
            timestamp = horaAtual,
            description = desc,
            isEmergency = isEmergency
        )
        _eventLogs.value = listOf(novoLog) + _eventLogs.value
    }

    fun onSliderHumidityChange(novoValor: Int) {
        val st = _esp32State.value
        _esp32State.value = st.copy(umidade = novoValor)

        if (novoValor < st.limiteMin && !st.bombaLigada) {
            if (st.temAgua) {
                _esp32State.value = _esp32State.value.copy(bombaLigada = true)
                logEvent("Umidade (" + novoValor + "%) abaixo do mínimo (" + st.limiteMin + "%). Irrigação ativada.")
            } else {
                logEvent("Umidade baixa, mas reservatório vazio! Bomba bloqueada (RN06).", isEmergency = true)
            }
        } else if (novoValor >= st.limiteMax && st.bombaLigada) {
            _esp32State.value = _esp32State.value.copy(bombaLigada = false)
            logEvent("Umidade (" + novoValor + "%) atingiu o limite máximo (" + st.limiteMax + "%). Irrigação desligada.")
        }
    }

    fun toggleReservoir() {
        val novoEstado = !_esp32State.value.temAgua
        _esp32State.value = _esp32State.value.copy(temAgua = novoEstado)
        if (!novoEstado && _esp32State.value.bombaLigada) {
            _esp32State.value = _esp32State.value.copy(bombaLigada = false)
            logEvent("ALERTA: Reservatório esvaziou! Bomba desligada (RN06).", isEmergency = true)
        } else {
            logEvent(if (novoEstado) "Reservatório abastecido com água." else "Reservatório vazio.")
        }
    }

    fun selectPlant(plant: PlantEntity) {
        _selectedPlant.value = plant
        _esp32State.value = _esp32State.value.copy(limiteMin = plant.minHumidity, limiteMax = plant.maxHumidity)

        // Grava no disco a planta e os limites associados
        viewModelScope.launch {
            settingsRepo.updateSelectedPlantId(plant.id)
            settingsRepo.updateThresholds(plant.minHumidity, plant.maxHumidity)
        }

        logEvent("Cultura alterada para " + plant.name + ". Faixa: " + plant.minHumidity + "% a " + plant.maxHumidity + "%.")
    }

    fun startManualIrrigation() {
        if (!_esp32State.value.temAgua) {
            logEvent("Tentativa de irrigação manual rejeitada: reservatório vazio (RN06 / RF05).", isEmergency = true)
            return
        }
        _esp32State.value = _esp32State.value.copy(bombaLigada = true)
        logEvent("Irrigação manual iniciada pelo usuário (RF06).")
    }

    fun triggerEmergencyStop() {
        _esp32State.value = _esp32State.value.copy(bombaLigada = false)
        logEvent("PARADA DE EMERGÊNCIA ACIONADA! (RF07 / RN07)", isEmergency = true)
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.updateDarkMode(enabled) }
        logEvent("Aparência alterada para modo " + (if (enabled) "Escuro" else "Claro") + ".")
    }

    fun setHighContrast(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.updateHighContrast(enabled) }
        logEvent("Modo de Alto Contraste " + (if (enabled) "ativado" else "desativado") + ".")
    }

    fun setFontScale(scale: Float) {
        viewModelScope.launch { settingsRepo.updateFontScale(scale) }
        val label = when (scale) {
            1.15f -> "Grande (115%)"
            1.30f -> "Extra Grande (130%)"
            else -> "Padrão (100%)"
        }
        logEvent("Escala de tipografia definida para $label.")
    }

    fun setTalkBackHelp(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.updateTalkBackHelp(enabled) }
    }

    fun setEsp32Ip(ip: String) {
        viewModelScope.launch { settingsRepo.updateEsp32Ip(ip) }
    }

    fun setConnectionMode(mode: String) {
        viewModelScope.launch { settingsRepo.updateConnectionMode(mode) }
        logEvent("Interface de comunicação alterada para $mode.")
    }

    fun updateCustomThresholds(min: Int, max: Int): Boolean {
        if (min >= max || min < 5 || max > 95) return false
        val current = _esp32State.value
        _esp32State.value = current.copy(limiteMin = min, limiteMax = max)
        viewModelScope.launch { settingsRepo.updateThresholds(min, max) }
        logEvent("Limites salvos: Min " + min + "% e Max " + max + "%.")
        return true
    }

    fun restoreFactoryDefaults() {
        val current = _esp32State.value
        _esp32State.value = current.copy(limiteMin = 40, limiteMax = 60)
        _selectedPlant.value = _plantList.value[0]
        viewModelScope.launch { settingsRepo.restoreDefaults() }
        logEvent("Configurações restauradas para o padrão de fábrica.")
    }

    fun gerarRelatorioIa() {
        viewModelScope.launch {
            _isLoadingAi.value = true
            val st = _esp32State.value
            val planta = _selectedPlant.value
            _aiReport.value = aiService.gerarDiagnostico(
                planta = planta.name,
                umidade = st.umidade,
                limiteMin = st.limiteMin,
                limiteMax = st.limiteMax,
                temperatura = st.temperatura,
                luminosidade = st.luminosidadeLux,
                bombaStatus = if (st.bombaLigada) "Ligada" else "Desligada",
                reservatorioStatus = if (st.temAgua) "Com Água" else "Vazio"
            )
            _isLoadingAi.value = false
        }
    }
}