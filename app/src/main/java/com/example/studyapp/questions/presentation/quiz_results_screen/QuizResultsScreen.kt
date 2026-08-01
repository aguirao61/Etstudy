package com.example.studyapp.questions.presentation.quiz_results_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
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
fun QuizResultsScreen(
    score: Int,
    total: Int,
    onBackToConfigClick: () -> Unit = {}
) {
    val percentage = if (total > 0) (score.toFloat() / total.toFloat() * 100).toInt() else 0
    val message = when {
        percentage >= 90 -> "¡Increíble! Eres un experto"
        percentage >= 70 -> "¡Buen trabajo! Vas por buen camino"
        percentage >= 50 -> "¡No está mal! Sigue practicando"
        else -> "¡Sigue intentándolo! Puedes mejorar"
    }

    Scaffold(
        containerColor = StudyTheme.surfaceBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = Color(0xFFEAB308),
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Test Finalizado",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = StudyTheme.textMain
            )

            Text(
                text = message,
                fontSize = 16.sp,
                color = StudyTheme.textSub,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Score Circle
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(StudyTheme.cardBg)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { score.toFloat() / total.toFloat() },
                    modifier = Modifier.fillMaxSize(),
                    color = if (percentage >= 50) SuccessGreen else ErrorRed,
                    strokeWidth = 10.dp,
                    trackColor = StudyTheme.cardBorder
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$score / $total",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = StudyTheme.textMain
                    )
                    Text(
                        text = "aciertos",
                        fontSize = 14.sp,
                        color = StudyTheme.textSub
                    )
                }
            }

            Spacer(modifier = Modifier.height(64.dp))

            Button(
                onClick = onBackToConfigClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Volver a Configurar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
