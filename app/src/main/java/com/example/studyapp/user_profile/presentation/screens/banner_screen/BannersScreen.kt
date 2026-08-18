package com.example.studyapp.user_profile.presentation.screens.banner_screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studyapp.core.presentation.ui.theme.*
import com.example.studyapp.user_profile.domain.models.Banner
import com.example.studyapp.user_profile.presentation.components.StudyBanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BannersScreen(
    viewModel: BannersViewModel = viewModel(),
    onBackClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                BannersEffect.NavigateBack -> onBackClick()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mis Banners",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = StudyTheme.textMain
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = StudyTheme.textMain
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = StudyTheme.surfaceBg)
            )
        },
        containerColor = StudyTheme.surfaceBg
    ) { innerPadding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Selecciona el banner que quieres equipar",
                    fontSize = 14.sp,
                    color = StudyTheme.textSub,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    item {
                        NoneBannerItem(
                            isSelected = state.selectedBannerId == 0,
                            onClick = {
                                viewModel.onIntent(
                                    BannersIntent.OnBannerSelect(
                                        0
                                    )
                                )
                            }
                        )
                    }
                    items(state.banners) { banner ->
                        SelectableBannerItem(
                            banner = banner,
                            isSelected = banner.id == state.selectedBannerId,
                            onClick = {
                                viewModel.onIntent(
                                    BannersIntent.OnBannerSelect(
                                        banner.id
                                    )
                                )
                            }
                        )
                    }
                }

                Button(
                    onClick = { 
                        viewModel.onIntent(BannersIntent.OnEquipClick)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue,
                        disabledContainerColor = GrayMid
                    ),
                    enabled = state.selectedBannerId != null
                ) {
                    Text("EQUIPAR SELECCIONADO", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun NoneBannerItem(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) PrimaryBlue else StudyTheme.cardBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = StudyTheme.cardBg)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Ningún banner (Predeterminado)",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = StudyTheme.textSub
            )
        }
    }
}

@Composable
fun SelectableBannerItem(
    banner: Banner,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isLocked = !banner.isObtained
    val bgColor = if (isLocked) Color(0xFFF1F5F9) else Color(banner.bannerColour)
    val textColor = if (isLocked) Color(0xFF94A3B8) else Color(banner.bannerTextColour)
    val borderColor = if (isSelected) PrimaryBlue else if (isLocked) Color(0xFFE2E8F0) else textColor.copy(alpha = 0.5f)
    val borderWidth = if (isSelected) 3.dp else 1.dp

    StudyBanner(
        text = banner.bannerContent,
        bannerColour = bgColor,
        bannerTextColour = textColor,
        bannerIcon = banner.bannerIcon,
        border = BorderStroke(borderWidth, borderColor),
        onClick = if (isLocked) null else onClick
    )
}
