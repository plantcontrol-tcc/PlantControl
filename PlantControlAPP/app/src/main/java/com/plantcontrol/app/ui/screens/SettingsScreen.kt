package com.plantcontrol.app.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plantcontrol.app.ui.MainViewModel
import com.plantcontrol.app.ui.theme.*

@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val state by viewModel.esp32State.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isHighContrast by viewModel.isHighContrast.collectAsState()
    val fontScale by viewModel.fontScale.collectAsState()
    val talkBackHelp by viewModel.talkBackHelpEnabled.collectAsState()
    val esp32Ip by viewModel.esp32Ip.collectAsState()
    val connectionMode by viewModel.connectionMode.collectAsState()

    var inputMin by remember(state.limiteMin) { mutableStateOf(state.limiteMin.toString()) }
    var inputMax by remember(state.limiteMax) { mutableStateOf(state.limiteMax.toString()) }
    var inputIp by remember(esp32Ip) { mutableStateOf(esp32Ip) }
    var configFeedback by remember { mutableStateOf<String?>(null) }
    var isErrorFeedback by remember { mutableStateOf(false) }

    val screenBackground = when {
        isHighContrast -> Color.Black
        isDarkMode -> Color(0xFF0F172A)
        else -> BlueBackground
    }

    val cardBackground = when {
        isHighContrast -> Color.Black
        isDarkMode -> Color(0xFF1E293B)
        else -> CardBackground
    }

    val primaryTextColor = when {
        isHighContrast -> Color.Yellow
        isDarkMode -> Color(0xFFF8FAFC)
        else -> TextDark
    }

    val secondaryTextColor = when {
        isHighContrast -> Color.White
        isDarkMode -> Color(0xFF94A3B8)
        else -> TextMuted
    }

    val borderStroke = if (isHighContrast) BorderStroke(2.dp, Color.Yellow) else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Configurações do Sistema",
            fontSize = (24 * fontScale).sp,
            color = primaryTextColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.semantics { heading() }
        )

        // SEÇÃO 1: COMUNICAÇÃO ESP32
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
            border = borderStroke,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "📡 Conectividade ESP32",
                    fontSize = (16 * fontScale).sp,
                    color = if (isHighContrast) Color.Yellow else BluePrimary,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Canal de Comunicação Ativo:",
                    fontSize = (13 * fontScale).sp,
                    color = secondaryTextColor
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val isWifi = connectionMode.contains("Wi-Fi")
                    OutlinedButton(
                        onClick = { viewModel.setConnectionMode("Wi-Fi (Access Point)") },
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isWifi) BluePrimary.copy(alpha = 0.15f) else Color.Transparent
                        ),
                        border = BorderStroke(if (isWifi) 2.dp else 1.dp, if (isWifi) BluePrimary else secondaryTextColor)
                    ) {
                        Text("Wi-Fi", fontSize = (14 * fontScale).sp, color = if (isWifi) BluePrimary else primaryTextColor)
                    }

                    OutlinedButton(
                        onClick = { viewModel.setConnectionMode("Bluetooth (BLE/SPP)") },
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (!isWifi) BluePrimary.copy(alpha = 0.15f) else Color.Transparent
                        ),
                        border = BorderStroke(if (!isWifi) 2.dp else 1.dp, if (!isWifi) BluePrimary else secondaryTextColor)
                    ) {
                        Text("Bluetooth", fontSize = (14 * fontScale).sp, color = if (!isWifi) BluePrimary else primaryTextColor)
                    }
                }

                OutlinedTextField(
                    value = inputIp,
                    onValueChange = { inputIp = it },
                    label = { Text("Endereço IP / Host do ESP32") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().semantics {
                        contentDescription = "Campo de texto para endereço IP do ESP32"
                    }
                )

                Button(
                    onClick = {
                        viewModel.setEsp32Ip(inputIp)
                        configFeedback = "Conexão atualizada para: " + inputIp + " via " + connectionMode
                        isErrorFeedback = false
                    },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isHighContrast) Color.Yellow else BluePrimary)
                ) {
                    Text(
                        text = "Testar & Sincronizar Conexão",
                        color = if (isHighContrast) Color.Black else Color.White,
                        fontSize = (14 * fontScale).sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // SEÇÃO 2: LIMITES DE HISTERESE
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
            border = borderStroke,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "⚙ Limites de Umidade & Histerese",
                    fontSize = (16 * fontScale).sp,
                    color = if (isHighContrast) Color.Yellow else BluePrimary,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Altere os pontos de acionamento (RN01) e corte (RN02) gravados no ESP32:",
                    fontSize = (13 * fontScale).sp,
                    color = secondaryTextColor
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = inputMin,
                        onValueChange = { inputMin = it },
                        label = { Text("Mínimo (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).semantics {
                            contentDescription = "Limite inferior para acionar a irrigação"
                        }
                    )

                    OutlinedTextField(
                        value = inputMax,
                        onValueChange = { inputMax = it },
                        label = { Text("Máximo (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).semantics {
                            contentDescription = "Limite superior para desligar a irrigação"
                        }
                    )
                }

                Button(
                    onClick = {
                        val minVal = inputMin.toIntOrNull()
                        val maxVal = inputMax.toIntOrNull()
                        if (minVal != null && maxVal != null) {
                            val success = viewModel.updateCustomThresholds(minVal, maxVal)
                            if (success) {
                                configFeedback = "Sucesso: Histerese atualizada (" + minVal + "% a " + maxVal + "%) [RN05]."
                                isErrorFeedback = false
                            } else {
                                configFeedback = "Erro: O limite mínimo deve ser menor que o máximo (Faixa: 5% a 95%)."
                                isErrorFeedback = true
                            }
                        } else {
                            configFeedback = "Erro: Digite apenas números válidos."
                            isErrorFeedback = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isHighContrast) Color.Yellow else BluePrimary)
                ) {
                    Text(
                        text = "Salvar Parâmetros no ESP32",
                        color = if (isHighContrast) Color.Black else Color.White,
                        fontSize = (14 * fontScale).sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                configFeedback?.let {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isErrorFeedback) Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                    ) {
                        Text(
                            text = it,
                            fontSize = (12 * fontScale).sp,
                            color = if (isErrorFeedback) StatusRed else StatusGreen,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }

        // SEÇÃO 3: ACESSIBILIDADE VISUAL
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
            border = borderStroke,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "👁 Acessibilidade Visual",
                    fontSize = (16 * fontScale).sp,
                    color = if (isHighContrast) Color.Yellow else BluePrimary,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .clickable(role = Role.Switch) { viewModel.setHighContrast(!isHighContrast) }
                        .semantics {
                            contentDescription = "Ativar ou desativar modo de alto contraste visual para baixa visão"
                            stateDescription = if (isHighContrast) "Ativado" else "Desativado"
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Alto Contraste Visual", fontSize = (14 * fontScale).sp, color = primaryTextColor, fontWeight = FontWeight.SemiBold)
                        Text("Cores com relação superior a 7:1 (Fundo preto e destaques amarelos)", fontSize = (11 * fontScale).sp, color = secondaryTextColor)
                    }
                    Switch(checked = isHighContrast, onCheckedChange = { viewModel.setHighContrast(it) })
                }

                HorizontalDivider(color = TrackBackground.copy(alpha = 0.5f))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .clickable(role = Role.Switch) { viewModel.setDarkMode(!isDarkMode) }
                        .semantics {
                            contentDescription = "Alternar entre modo escuro e modo claro"
                            stateDescription = if (isDarkMode) "Modo escuro ativado" else "Modo claro ativado"
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Tema Escuro (Dark Mode)", fontSize = (14 * fontScale).sp, color = primaryTextColor, fontWeight = FontWeight.SemiBold)
                        Text("Reduz a fadiga visual e o consumo de tela", fontSize = (11 * fontScale).sp, color = secondaryTextColor)
                    }
                    Switch(checked = isDarkMode, onCheckedChange = { viewModel.setDarkMode(it) })
                }

                HorizontalDivider(color = TrackBackground.copy(alpha = 0.5f))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Tamanho da Tipografia (Escala de Fonte)", fontSize = (14 * fontScale).sp, color = primaryTextColor, fontWeight = FontWeight.SemiBold)
                    Text("Amplie o texto sem quebrar a estrutura da interface:", fontSize = (11 * fontScale).sp, color = secondaryTextColor)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val options = listOf("100%" to 1.0f, "115%" to 1.15f, "130%" to 1.30f)
                        for ((label, scale) in options) {
                            val isSelected = fontScale == scale
                            OutlinedButton(
                                onClick = { viewModel.setFontScale(scale) },
                                modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isSelected) BluePrimary.copy(alpha = 0.15f) else Color.Transparent
                                ),
                                border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) BluePrimary else secondaryTextColor)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 13.sp,
                                    color = if (isSelected) BluePrimary else primaryTextColor,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }

        // SEÇÃO 4: ACESSIBILIDADE MOTORA E COGNITIVA
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
            border = borderStroke,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "♿ Operabilidade & TalkBack",
                    fontSize = (16 * fontScale).sp,
                    color = if (isHighContrast) Color.Yellow else BluePrimary,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .clickable(role = Role.Switch) { viewModel.setTalkBackHelp(!talkBackHelp) }
                        .semantics {
                            contentDescription = "Otimizar rótulos sonoros para o leitor de telas TalkBack"
                            stateDescription = if (talkBackHelp) "Rótulos expandidos ativados" else "Rótulos normais"
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Suporte Estendido a Leitores de Tela", fontSize = (14 * fontScale).sp, color = primaryTextColor, fontWeight = FontWeight.SemiBold)
                        Text("Garante nomes de componentes e feedbacks sonoros detalhados", fontSize = (11 * fontScale).sp, color = secondaryTextColor)
                    }
                    Switch(checked = talkBackHelp, onCheckedChange = { viewModel.setTalkBackHelp(it) })
                }

                HorizontalDivider(color = TrackBackground.copy(alpha = 0.5f))

                OutlinedButton(
                    onClick = {
                        viewModel.restoreFactoryDefaults()
                        inputIp = "192.168.4.1"
                        inputMin = "40"
                        inputMax = "60"
                        configFeedback = "Configurações restauradas com sucesso."
                        isErrorFeedback = false
                    },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                    border = BorderStroke(1.dp, StatusRed)
                ) {
                    Text(
                        text = "Restaurar Padrões de Fábrica (40% - 60%)",
                        color = StatusRed,
                        fontSize = (13 * fontScale).sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}