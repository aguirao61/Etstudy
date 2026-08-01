package com.example.studyapp.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studyapp.core.presentation.ui.theme.*

@Composable
fun UserProfileCard(
    name: String,
    expCurrent: Int,
    expMax: Int,
    level: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .border(2.dp, StudyTheme.profileBorder, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = StudyTheme.profileBg)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Circle Avatar Placeholder
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(StudyTheme.cardBg)
                    .border(1.dp, StudyTheme.cardBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar",
                    tint = StudyTheme.textSub,
                    modifier = Modifier.size(36.dp)
                )
            }

            // Datos del Usuario
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Nombre: $name",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = StudyTheme.textMain
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Barra 1: EXP
                Text(
                    text = "EXP: $expCurrent / $expMax",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StudyTheme.textSub
                )
                LinearProgressIndicator(
                    progress = { expCurrent.toFloat() / expMax.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = ExpColor,
                    trackColor = StudyTheme.surfaceBg
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Barra 2: Nivel
                Text(
                    text = "Nivel: $level",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LevelColor
                )
                LinearProgressIndicator(
                    progress = { 0.65f }, // Ejemplo de progreso de nivel
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = LevelColor,
                    trackColor = StudyTheme.surfaceBg
                )
            }
        }
    }
}
