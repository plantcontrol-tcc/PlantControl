package com.plantcontrol.app.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plantcontrol.app.domain.algorithms.AgronomicCalculations
import com.plantcontrol.app.ui.MainViewModel
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import com.plantcontrol.app.ui.theme.*

@Composable
fun HomeScreen(viewModel: MainViewModel, onNavigateToPlants: () -> Unit) {
    val state by viewModel.esp32State.collectAsState()
    val plant by viewModel.selectedPlant.collectAsState()
    val aiReport by viewModel.aiReport.collectAsState()
    val isLoadingAi by viewModel.isLoadingAi.collectAsState()

    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isHighContrast by viewModel.isHighContrast.collectAsState()
    val fontScale by viewModel.fontScale.collectAsState()

    val horasRestantes = AgronomicCalculations.estimarHorasAteSecar(
        state.umidade, state.limiteMin, state.temperatura, state.luminosidadeLux
    )

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

    val highlightColor = if (isHighContrast) Color.Yellow else BluePrimary
    val borderStroke = if (isHighContrast) BorderStroke(2.dp, Color.Yellow) else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Cabeçalho com proteção contra corte no topo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .semantics(mergeDescendants = true) {
                    contentDescription = "Cabeçalho PlantControl. Sistema em modo automático e conectado ao ESP32."
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text(
                    text = "PlantControl",
                    fontSize = (26 * fontScale).sp,
                    color = highlightColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.semantics { heading() }
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(StatusGreen, CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ESP32 Conectado",
                        fontSize = (13 * fontScale).sp,
                        color = secondaryTextColor
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(if (isHighContrast) 2.dp else 1.dp, StatusGreen),
                color = if (isHighContrast) Color.Black else if (isDarkMode) Color(0xFF1E293B) else Color.White
            ) {
                Text(
                    text = "⚙ AUTOMÁTICO",
                    color = StatusGreen,
                    fontSize = (12 * fontScale).sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        // Card Central de Umidade
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
            border = borderStroke,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("💧", fontSize = (32 * fontScale).sp)
                Text(
                    text = "Umidade do solo (Arraste para testar)",
                    fontSize = (14 * fontScale).sp,
                    color = secondaryTextColor
                )
                Text(
                    text = "${state.umidade}%",
                    fontSize = (48 * fontScale).sp,
                    color = highlightColor,
                    fontWeight = FontWeight.ExtraBold
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (state.umidade < state.limiteMin) {
                        if (isHighContrast) Color(0xFF550000) else Color(0xFFFFEBEE)
                    } else {
                        if (isHighContrast) Color(0xFF003300) else Color(0xFFE8F5E9)
                    },
                    border = if (isHighContrast) BorderStroke(1.dp, if (state.umidade < state.limiteMin) StatusRed else StatusGreen) else null,
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    Text(
                        text = if (state.umidade < state.limiteMin) "Solo Seco" else "✓ Umidade adequada",
                        color = if (state.umidade < state.limiteMin) StatusRed else StatusGreen,
                        fontSize = (13 * fontScale).sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = "Faixa configurada: ${state.limiteMin}%–${state.limiteMax}%",
                    fontSize = (13 * fontScale).sp,
                    color = secondaryTextColor
                )

                Slider(
                    value = state.umidade.toFloat(),
                    onValueChange = { viewModel.onSliderHumidityChange(it.toInt()) },
                    valueRange = 0f..100f,
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .semantics {
                            contentDescription = "Controle deslizante de teste de umidade do solo"
                            stateDescription = "${state.umidade} por cento"
                        }
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("0%", fontSize = (11 * fontScale).sp, color = secondaryTextColor)
                    Text("${state.limiteMin}% (Mín)", fontSize = (11 * fontScale).sp, color = secondaryTextColor)
                    Text("${state.limiteMax}% (Máx)", fontSize = (11 * fontScale).sp, color = secondaryTextColor)
                    Text("100%", fontSize = (11 * fontScale).sp, color = secondaryTextColor)
                }
            }
        }

        // Grid 2 Colunas: Reservatório e Bomba
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackground),
                border = borderStroke,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
                    .clickable(role = Role.Switch) { viewModel.toggleReservoir() }
                    .semantics {
                        contentDescription = "Alternar nível de água do reservatório. Atualmente está ${if (state.temAgua) "Com água" else "Vazio"}"
                    }
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Reservatório", fontSize = (14 * fontScale).sp, color = secondaryTextColor, fontWeight = FontWeight.SemiBold)
                    Text("🛢", fontSize = (32 * fontScale).sp, modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        text = if (state.temAgua) "Com água" else "Vazio",
                        color = if (state.temAgua) StatusGreen else StatusRed,
                        fontSize = (15 * fontScale).sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Clique p/ alternar", fontSize = (11 * fontScale).sp, color = secondaryTextColor)
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackground),
                border = borderStroke,
                modifier = Modifier
                    .weight(1f)
                    .semantics {
                        contentDescription = "Estado da Bomba d'água. Atualmente ${if (state.bombaLigada) "Ligada, irrigando" else "Desligada, aguardando"}"
                    }
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Bomba", fontSize = (14 * fontScale).sp, color = secondaryTextColor, fontWeight = FontWeight.SemiBold)
                    Text("⚙", fontSize = (32 * fontScale).sp, modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        text = if (state.bombaLigada) "Ligada" else "Desligada",
                        color = if (state.bombaLigada) StatusGreen else StatusRed,
                        fontSize = (15 * fontScale).sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(if (state.bombaLigada) "Irrigando..." else "Aguardando", fontSize = (11 * fontScale).sp, color = secondaryTextColor)
                }
            }
        }

        // Planta Selecionada (Layout Corrigido: Botão Alterar não quebra linha)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
            border = borderStroke,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Transparent,
                        modifier = Modifier.size(52.dp)
                    ) {
                        Image(
                            painter = painterResource(id = plant.imageRes),
                            contentDescription = "Foto da planta ${plant.name}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = plant.name,
                            fontSize = (16 * fontScale).sp,
                            color = primaryTextColor,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = plant.scientificName,
                            fontSize = (12 * fontScale).sp,
                            color = secondaryTextColor,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }

                OutlinedButton(
                    onClick = onNavigateToPlants,
                    modifier = Modifier.heightIn(min = 48.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    border = BorderStroke(if (isHighContrast) 2.dp else 1.dp, highlightColor)
                ) {
                    Text(
                        text = "Alterar",
                        color = highlightColor,
                        fontSize = (13 * fontScale).sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }

        // Módulo de Algoritmos Agronômicos
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
            border = borderStroke,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Algoritmos Agronômicos em Execução",
                    fontSize = (14 * fontScale).sp,
                    color = primaryTextColor,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Previsão: solo seca em ~${"%.1f".format(horasRestantes)} horas",
                    fontSize = (13 * fontScale).sp,
                    color = secondaryTextColor
                )
                Text(
                    text = "• Compensação de Clima: ${state.temperatura}°C | ${state.luminosidadeLux} Lux",
                    fontSize = (13 * fontScale).sp,
                    color = secondaryTextColor
                )
                Text(
                    text = "• Consumo Estimado: ~0.45 L por ciclo completo",
                    fontSize = (13 * fontScale).sp,
                    color = secondaryTextColor
                )
            }
        }

        // IA Sob Demanda
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
            border = borderStroke,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Consultoria por Inteligência Artificial",
                        fontSize = (15 * fontScale).sp,
                        color = highlightColor,
                        fontWeight = FontWeight.Bold
                    )
                    if (isLoadingAi) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = highlightColor
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.gerarRelatorioIa() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isHighContrast) Color.Yellow else BluePrimary
                    )
                ) {
                    Text(
                        text = "✨ Gerar Diagnóstico & Recomendações",
                        color = if (isHighContrast) Color.Black else Color.White,
                        fontSize = (14 * fontScale).sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                aiReport?.let {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = if (isHighContrast) Color(0xFF1E1E1E) else if (isDarkMode) Color(0xFF0F172A) else BlueBackground,
                        shape = RoundedCornerShape(8.dp),
                        border = if (isHighContrast) BorderStroke(1.dp, Color.Yellow) else null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = it,
                            fontSize = (13 * fontScale).sp,
                            color = primaryTextColor,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }

        // Controles Manuais & Emergência
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
            border = borderStroke,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Controles Manuais & Emergência",
                    fontSize = (14 * fontScale).sp,
                    color = secondaryTextColor,
                    fontWeight = FontWeight.SemiBold
                )

                Button(
                    onClick = { viewModel.startManualIrrigation() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isHighContrast) Color.Yellow else BluePrimary
                    )
                ) {
                    Text(
                        text = "💧 Iniciar irrigação manual (RF06)",
                        color = if (isHighContrast) Color.Black else Color.White,
                        fontSize = (14 * fontScale).sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = { viewModel.triggerEmergencyStop() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp),
                    border = BorderStroke(if (isHighContrast) 2.dp else 1.dp, StatusRed)
                ) {
                    Text(
                        text = "⚠ Parada de emergência (RF07 / RN07)",
                        color = StatusRed,
                        fontSize = (14 * fontScale).sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🛡 Mecanismos de segurança e histerese ativos",
                        fontSize = (12 * fontScale).sp,
                        color = secondaryTextColor
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}