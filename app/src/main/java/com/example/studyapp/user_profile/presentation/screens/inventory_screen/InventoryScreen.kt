package com.example.studyapp.user_profile.presentation.screens.inventory_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studyapp.R
import com.example.studyapp.core.presentation.ui.theme.*
import com.example.studyapp.user_profile.domain.models.Icon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel = viewModel(),
    onBackClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var selectedItem by remember { mutableStateOf<Icon?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Inventario",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(state.icons) { item ->
                    InventoryCard(
                        item = item,
                        onClick = { 
                            selectedItem = if (selectedItem?.id == item.id) null else item 
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Detalle del item seleccionado
            if (selectedItem != null) {
                ItemDetailPanel(
                    item = selectedItem!!,
                    isEquipped = state.equippedIconId == selectedItem!!.id,
                    onEquipClick = { viewModel.onEquipIcon(selectedItem!!) },
                    onCloseClick = { selectedItem = null }
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(StudyTheme.cardBg)
                        .border(1.dp, StudyTheme.cardBorder, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Selecciona un objeto para equipar",
                        color = StudyTheme.textSub,
                        fontSize = 14.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun InventoryCard(
    item: Icon,
    onClick: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val resId = remember(item.imageUrl) {
        val id = context.resources.getIdentifier(item.imageUrl, "drawable", context.packageName)
        if (id != 0) id else R.drawable.img
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(StudyTheme.cardBg)
            .border(1.dp, StudyTheme.cardBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (item.isUnlocked) {
            Image(
                painter = painterResource(id = resId),
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().padding(12.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Bloqueado",
                tint = StudyTheme.textSub.copy(alpha = 0.4f),
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun ItemDetailPanel(
    item: Icon,
    isEquipped: Boolean,
    onEquipClick: () -> Unit,
    onCloseClick: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val resId = remember(item.imageUrl) {
        val id = context.resources.getIdentifier(item.imageUrl, "drawable", context.packageName)
        if (id != 0) id else R.drawable.img
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = StudyTheme.cardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, StudyTheme.cardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Botón de cerrar en la esquina superior derecha
            IconButton(
                onClick = onCloseClick,
                modifier = Modifier.align(Alignment.End).size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = StudyTheme.textSub,
                    modifier = Modifier.size(16.dp)
                )
            }

            Row(
                modifier = Modifier.padding(bottom = 4.dp, start = 4.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Imagen sin fondo circular
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (item.isUnlocked) {
                        Image(
                            painter = painterResource(id = resId),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = StudyTheme.textSub.copy(alpha = 0.4f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = StudyTheme.textMain
                    )
                    Text(
                        text = item.description,
                        fontSize = 13.sp,
                        color = StudyTheme.textSub
                    )
                }

                if (item.isUnlocked) {
                    Button(
                        onClick = onEquipClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isEquipped) ErrorRed else PrimaryBlue,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = if (isEquipped) "DESEQUIPAR" else "EQUIPAR",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
