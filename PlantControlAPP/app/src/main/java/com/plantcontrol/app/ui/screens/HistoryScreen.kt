package com.plantcontrol.app.ui.screens.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun HistoryScreen(viewModel: MainViewModel) {
    val logs by viewModel.eventLogs.collectAsState()
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

    val borderStroke = if (isHighContrast) BorderStroke(2.dp, Color.Yellow) else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "Histórico de Eventos",
            fontSize = (24 * fontScale).sp,
            color = primaryTextColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.semantics { heading() }
        )
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
            border = borderStroke,
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(logs) { item ->
                    Column {
                        Row {
                            Text(
                                text = item.timestamp + " - ",
                                fontSize = (14 * fontScale).sp,
                                color = secondaryTextColor,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = item.description,
                                fontSize = (14 * fontScale).sp,
                                color = if (item.isEmergency) StatusRed else primaryTextColor,
                                fontWeight = if (item.isEmergency) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(top = 8.dp),
                            color = TrackBackground.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}