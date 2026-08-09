package com.example.studyapp.user_profile.presentation.inventory_screen

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
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studyapp.R
import com.example.studyapp.core.presentation.ui.theme.*

data class InventoryItem(
    val id: Int,
    val name: String,
    val description: String,
    val imageResId: Int,
    val category: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    onBackClick: () -> Unit = {}
) {
    // Mock Data
    val items = remember {
        listOf(
            InventoryItem(1, "Matemáticas Discretas", "Por responder 100 preguntas correctas", R.drawable.trofeo_tecnologia_materiales_icon_1000, "Trofeos"),
            InventoryItem(2, "Física General", "Por responder 250 preguntas correctas", R.drawable.trofeo_tecnologia_materiales_icon_1000, "Trofeos"),
            InventoryItem(3, "Programación", "Por responder 1000 preguntas correctas", R.drawable.trofeo_tecnologia_materiales_icon_1000, "Trofeos"),
            InventoryItem(4, "Cálculo Integral", "Por responder 2500 preguntas correctas", R.drawable.trofeo_tecnologia_materiales_icon_1000, "Trofeos"),
            InventoryItem(5, "Bases de Datos", "Por responder 100 preguntas correctas", R.drawable.trofeo_tecnologia_materiales_icon_1000, "Trofeos"),
            InventoryItem(6, "Estructuras de Datos", "Por responder 250 preguntas correctas", R.drawable.trofeo_tecnologia_materiales_icon_1000, "Trofeos")
        )
    }

    var selectedItem by remember { mutableStateOf<InventoryItem?>(null) }

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
                items(items) { item ->
                    InventoryCard(
                        item = item,
                        onClick = { selectedItem = item }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Detalle del item seleccionado
            if (selectedItem != null) {
                ItemDetailPanel(item = selectedItem!!)
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(StudyTheme.cardBg)
                        .border(1.dp, StudyTheme.cardBorder, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Selecciona un objeto para ver detalles",
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
    item: InventoryItem,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(StudyTheme.cardBg)
            .border(1.dp, StudyTheme.cardBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = item.imageResId),
            contentDescription = item.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().padding(12.dp)
        )
    }
}

@Composable
fun ItemDetailPanel(item: InventoryItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = StudyTheme.cardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, StudyTheme.cardBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(StudyTheme.surfaceBg),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = item.imageResId),
                    contentDescription = null,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Column {
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
        }
    }
}
