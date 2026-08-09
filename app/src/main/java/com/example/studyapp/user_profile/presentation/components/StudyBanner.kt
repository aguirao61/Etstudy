package com.example.studyapp.user_profile.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StudyBanner(
    text: String,
    bannerColour: Color,
    bannerTextColour: Color,
    bannerIcon: String,
    modifier: Modifier = Modifier,
    border: BorderStroke? = null,
    onClick: (() -> Unit)? = null
) {
    val finalBorder = border ?: BorderStroke(1.dp, bannerTextColour.copy())

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bannerColour),
        border = finalBorder,
        onClick = onClick ?: {},
        enabled = onClick != null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = mapBannerIcon(bannerIcon),
                contentDescription = null,
                tint = bannerTextColour,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = bannerTextColour
            )
        }
    }
}

private fun mapBannerIcon(iconName: String): ImageVector {
    return when (iconName) {
        "star" -> Icons.Default.Star
        "school" -> Icons.Default.School
        "premium" -> Icons.Default.WorkspacePremium
        else -> Icons.Default.EmojiEvents
    }
}
