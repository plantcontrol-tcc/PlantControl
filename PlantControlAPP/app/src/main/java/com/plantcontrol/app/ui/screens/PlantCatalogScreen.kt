package com.plantcontrol.app.ui.screens.catalog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.plantcontrol.app.data.local.PlantEntity
import com.plantcontrol.app.ui.MainViewModel
import com.plantcontrol.app.ui.theme.*



@Composable
fun PlantCatalogScreen(
    viewModel: MainViewModel,
    onNavigateToHome: () -> Unit
) {
    val plants by viewModel.plantList.collectAsState()
    val selectedPlant by viewModel.selectedPlant.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isHighContrast by viewModel.isHighContrast.collectAsState()
    val fontScale by viewModel.fontScale.collectAsState()

    var plantDetailDialog by remember { mutableStateOf<PlantEntity?>(null) }

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

    // Cores específicas da Caixa de Curiosidade para evitar texto branco em fundo claro
    val curiosityCardBg = when {
        isHighContrast -> Color(0xFF1C1917)
        isDarkMode -> Color(0xFF0F172A)
        else -> Color(0xFFEFF6FF)
    }

    val curiosityCardBorder = when {
        isHighContrast -> BorderStroke(1.dp, Color.Yellow)
        isDarkMode -> BorderStroke(1.dp, Color(0xFF334155))
        else -> BorderStroke(1.dp, Color(0xFFBFDBFE))
    }

    val curiosityTextColor = when {
        isHighContrast -> Color.Yellow
        isDarkMode -> Color(0xFFE2E8F0)
        else -> Color(0xFF1E3A8A)
    }

    val borderStroke = if (isHighContrast) BorderStroke(2.dp, Color.Yellow) else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "Catálogo de Cultivos",
            fontSize = (24 * fontScale).sp,
            color = primaryTextColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.semantics { heading() }
        )
        Text(
            text = "Toque no card para abrir o guia de cultivo ou em Selecionar para aplicar a planta:",
            fontSize = (13 * fontScale).sp,
            color = secondaryTextColor,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(plants) { plant ->
                val isCurrentlySelected = selectedPlant.id == plant.id
                val activeBorder = if (isCurrentlySelected) {
                    BorderStroke(2.dp, if (isHighContrast) Color.Yellow else StatusGreen)
                } else borderStroke

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBackground),
                    border = activeBorder,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(role = Role.Button) { plantDetailDialog = plant }
                        .semantics(mergeDescendants = true) {
                            contentDescription = "Planta ${plant.name}. Toque para abrir informações completas."
                        }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Ícone Vetorial
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

                            // Conteúdo Textual com largura flexível (Weight 1f)
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                // Badge "CULTURA ATIVA" em linha própria para nunca quebrar ou espremer
                                if (isCurrentlySelected) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(bottom = 2.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(if (isHighContrast) Color.Yellow else StatusGreen, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "CULTURA ATIVA",
                                            color = if (isHighContrast) Color.Yellow else StatusGreen,
                                            fontSize = (10 * fontScale).sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                }

                                Text(
                                    text = plant.name,
                                    fontSize = (17 * fontScale).sp,
                                    color = primaryTextColor,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = plant.scientificName,
                                    fontSize = (12 * fontScale).sp,
                                    color = secondaryTextColor,
                                    fontStyle = FontStyle.Italic
                                )

                                Text(
                                    text = "Faixa Ideal: " + plant.minHumidity + "% - " + plant.maxHumidity + "%",
                                    fontSize = (13 * fontScale).sp,
                                    color = if (isHighContrast) Color.Yellow else BluePrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Botão com largura mínima e sem quebra de linha de texto
                            Button(
                                onClick = {
                                    viewModel.selectPlant(plant)
                                    onNavigateToHome()
                                },
                                modifier = Modifier.heightIn(min = 48.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isHighContrast) Color.Yellow else if (isCurrentlySelected) StatusGreen else BluePrimary
                                )
                            ) {
                                Text(
                                    text = if (isCurrentlySelected) "Ativa" else "Selecionar",
                                    color = if (isHighContrast) Color.Black else Color.White,
                                    fontSize = (13 * fontScale).sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "☀️ " + plant.lightRequirement.split("(")[0].trim(),
                                fontSize = (11 * fontScale).sp,
                                color = secondaryTextColor
                            )
                            Text(
                                text = "💧 " + plant.irrigationType.split("(")[0].trim(),
                                fontSize = (11 * fontScale).sp,
                                color = secondaryTextColor
                            )
                        }
                    }
                }
            }
        }
    }

    // DIÁLOGO INFORMATIVO BOTÂNICO E DE CULTIVO
    plantDetailDialog?.let { plant ->
        Dialog(onDismissRequest = { plantDetailDialog = null }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = cardBackground,
                border = BorderStroke(2.dp, if (isHighContrast) Color.Yellow else BluePrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = plant.name,
                                fontSize = (19 * fontScale).sp,
                                color = primaryTextColor,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = plant.scientificName,
                                fontSize = (13 * fontScale).sp,
                                color = secondaryTextColor,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }

                    HorizontalDivider(color = TrackBackground.copy(alpha = 0.5f))

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "💧 Manejo Hídrico & Histerese",
                            fontSize = (15 * fontScale).sp,
                            color = if (isHighContrast) Color.Yellow else BluePrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "• Faixa de Umidade Ideal: " + plant.minHumidity + "% (Liga) a " + plant.maxHumidity + "% (Desliga)",
                            fontSize = (13 * fontScale).sp,
                            color = primaryTextColor,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "• Modo de Irrigação: " + plant.irrigationType,
                            fontSize = (13 * fontScale).sp,
                            color = secondaryTextColor
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "☀️ Exposição Solar & Luz",
                            fontSize = (15 * fontScale).sp,
                            color = if (isHighContrast) Color.Yellow else BluePrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "• " + plant.lightRequirement,
                            fontSize = (13 * fontScale).sp,
                            color = secondaryTextColor
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "🌱 Substrato e Drenagem",
                            fontSize = (15 * fontScale).sp,
                            color = if (isHighContrast) Color.Yellow else BluePrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "• " + plant.soilType,
                            fontSize = (13 * fontScale).sp,
                            color = secondaryTextColor
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "📖 Guia Completo de Cultivo",
                            fontSize = (15 * fontScale).sp,
                            color = if (isHighContrast) Color.Yellow else BluePrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = plant.fullDescription,
                            fontSize = (13 * fontScale).sp,
                            color = primaryTextColor,
                            lineHeight = (18 * fontScale).sp
                        )
                    }

                    // Caixa de Curiosidade corrigida (Fundo e borda adaptáveis ao modo escuro)
                    if (plant.curiosities.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = curiosityCardBg,
                            border = curiosityCardBorder,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "💡 Curiosidade: " + plant.curiosities,
                                fontSize = (12 * fontScale).sp,
                                color = curiosityTextColor,
                                lineHeight = (17 * fontScale).sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { plantDetailDialog = null },
                            modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                        ) {
                            Text(
                                text = "Fechar",
                                color = primaryTextColor,
                                fontSize = (14 * fontScale).sp
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.selectPlant(plant)
                                plantDetailDialog = null
                                onNavigateToHome()
                            },
                            modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isHighContrast) Color.Yellow else BluePrimary
                            )
                        ) {
                            Text(
                                text = "Selecionar",
                                color = if (isHighContrast) Color.Black else Color.White,
                                fontSize = (14 * fontScale).sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}