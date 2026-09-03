package com.example.studyapp.core.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studyapp.R
import com.example.studyapp.core.presentation.ui.theme.*
import com.example.studyapp.core.util.NumberFormatter

@Composable
fun UserProfileCard(
    modifier: Modifier = Modifier,
    name: String,
    expCurrent: Int,
    expMax: Int,
    level: Int,
    iconUrl: String? = null,
    currentStreak: Int = 0,
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
            // Avatar dinámico
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(StudyTheme.cardBg)
                    .border(1.dp, StudyTheme.cardBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (iconUrl != null) {
                    val context = LocalContext.current
                    val resId = remember(iconUrl) {
                        val id = context.resources.getIdentifier(iconUrl, "drawable", context.packageName)
                        if (id != 0) id else R.drawable.img
                    }
                    Image(
                        painter = painterResource(id = resId),
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar",
                        tint = StudyTheme.textSub,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            // Datos del Usuario
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Nombre: $name",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = StudyTheme.textMain,
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (currentStreak > 0) {
                        Surface(
                            color = PrimaryBlue.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Whatshot,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = currentStreak.toString(),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    color = PrimaryBlue
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Barra 1: EXP
                Text(
                    text = "EXP: ${NumberFormatter.formatWithCommas(expCurrent)} / ${NumberFormatter.formatWithCommas(expMax)}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ExpColor
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
                    progress = { level.toFloat() / 999f },
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
