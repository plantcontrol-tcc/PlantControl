package com.plantcontrol.app.ui.screens.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plantcontrol.app.ui.MainViewModel
import com.plantcontrol.app.ui.theme.*

@Composable
fun AnalyticsDashboardScreen(viewModel: MainViewModel) {
    val state by viewModel.esp32State.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isHighContrast by viewModel.isHighContrast.collectAsState()
    val fontScale by viewModel.fontScale.collectAsState()

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
        Text(
            text = "Telemetria Detalhada",
            fontSize = (24 * fontScale).sp,
            color = primaryTextColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.semantics { heading() }
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
            border = borderStroke,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Consumo e Eficiência Hídrica", fontSize = (16 * fontScale).sp, color = highlightColor, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("• Volume total consumido hoje: 2.8 Litros", fontSize = (14 * fontScale).sp, color = primaryTextColor)
                Text("• Ciclos de irrigação executados: 4 ciclos", fontSize = (14 * fontScale).sp, color = primaryTextColor)
                Text("• Eficiência de retenção: 88%", fontSize = (14 * fontScale).sp, color = StatusGreen, fontWeight = FontWeight.Bold)
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
            border = borderStroke,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Parâmetros do Ambiente", fontSize = (16 * fontScale).sp, color = highlightColor, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Temperatura:", fontSize = (14 * fontScale).sp, color = secondaryTextColor)
                    Text("${state.temperatura} °C", fontSize = (14 * fontScale).sp, color = primaryTextColor, fontWeight = FontWeight.Bold)
                }

                HorizontalDivider(color = TrackBackground.copy(alpha = 0.5f))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Luminosidade:", fontSize = (14 * fontScale).sp, color = secondaryTextColor)
                    Text("${state.luminosidadeLux} Lux", fontSize = (14 * fontScale).sp, color = primaryTextColor, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}