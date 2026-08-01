package com.example.studyapp.questions.presentation.subject_screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studyapp.core.presentation.components.UserProfileCard
import com.example.studyapp.core.presentation.ui.theme.*
import com.example.studyapp.questions.domain.Subject
import com.example.studyapp.questions.domain.SubjectFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectsScreen(
    viewModel: SubjectsViewModel = viewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToQuizConfig: (subjectId: Int, subjectName: String, flow: String) -> Unit = { _, _, _ -> }
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    // IMPORTANTE: Asegurar que el callback de navegación esté actualizado
    val currentOnNavigateBack by rememberUpdatedState(onNavigateBack)
    val currentOnNavigateToQuizConfig by rememberUpdatedState(onNavigateToQuizConfig)

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SubjectsEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                SubjectsEffect.NavigateBack -> {
                    currentOnNavigateBack()
                }
                is SubjectsEffect.NavigateToQuizConfig -> {
                    currentOnNavigateToQuizConfig(effect.subject.id, effect.subject.name, effect.flow.name)
                }
            }
        }
    }

    SubjectsScreenContent(
        state = state,
        onIntent = viewModel::onIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectsScreenContent(
    state: SubjectsState,
    onIntent: (SubjectsIntent) -> Unit
) {
    // Estado del Pager para poder deslizar entre pestañas
    val pagerState = rememberPagerState(pageCount = { 2 })

    // Sincronizar Pestaña -> Pager
    LaunchedEffect(state.selectedTab) {
        if (pagerState.currentPage != state.selectedTab) {
            pagerState.animateScrollToPage(state.selectedTab)
        }
    }

    // Sincronizar Pager -> Pestaña
    LaunchedEffect(pagerState.currentPage) {
        onIntent(SubjectsIntent.OnTabSelect(pagerState.currentPage))
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    val title = when (state.flowType) {
                        SubjectFlow.BROWSE -> "Asignaturas"
                        SubjectFlow.TEST -> "Iniciar Test"
                        SubjectFlow.ERROR_TEST -> "Repasar Fallos"
                    }
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextMain
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onIntent(SubjectsIntent.OnBackClick) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás",
                            tint = TextMain
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = SurfaceBg
                )
            )
        },
        containerColor = SurfaceBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            UserProfileCard(
                name = state.userName,
                level = state.level,
                expCurrent = state.expCurrent,
                expMax = state.expMax,
                onClick = { onIntent(SubjectsIntent.OnProfileClick) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // SEARCHBAR con HINT más oscuro
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { onIntent(SubjectsIntent.OnSearchQueryChange(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                placeholder = { 
                    Text(
                        text = "Buscar asignatura o código...",
                        color = TextSub.copy(alpha = 0.9f) // Hint con más contraste
                    ) 
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = TextSub
                    )
                },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onIntent(SubjectsIntent.OnSearchQueryChange("")) }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF3B82F6),
                    unfocusedBorderColor = Color(0xFFCBD5E1),
                    focusedTextColor = TextMain,
                    unfocusedTextColor = TextMain
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // TABS
            TabRow(
                selectedTabIndex = state.selectedTab,
                containerColor = Color.Transparent,
                contentColor = Color(0xFF3B82F6),
                divider = {}
            ) {
                Tab(
                    selected = state.selectedTab == 0,
                    onClick = { onIntent(SubjectsIntent.OnTabSelect(0)) },
                    text = {
                        Text(
                            text = "Todas (${state.subjects.size})",
                            fontWeight = if (state.selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = state.selectedTab == 1,
                    onClick = { onIntent(SubjectsIntent.OnTabSelect(1)) },
                    text = {
                        Text(
                            text = "Favoritas (${state.subjects.count { it.isFavorite }})",
                            fontWeight = if (state.selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // PAGER para poder deslizar entre listas
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize().weight(1f),
                verticalAlignment = Alignment.Top
            ) { pageIndex ->
                val listToShow = if (pageIndex == 0) state.allSubjectsFiltered else state.favoriteSubjectsFiltered
                
                if (listToShow.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (pageIndex == 1 && state.searchQuery.isEmpty())
                                "No tienes asignaturas favoritas aún"
                            else
                                "No se encontraron asignaturas",
                            color = TextSub,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(listToShow, key = { it.id }) { subject ->
                            val dismissState = rememberSwipeToDismissBoxState()
                            
                            LaunchedEffect(dismissState.currentValue) {
                                if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
                                    onIntent(SubjectsIntent.OnFavoriteToggle(subject))
                                    dismissState.snapTo(SwipeToDismissBoxValue.Settled)
                                }
                            }

                            SwipeToDismissBox(
                                state = dismissState,
                                backgroundContent = {
                                    val color = if (subject.isFavorite) Color(0xFF94A3B8) else Color(0xFFEAB308)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(color)
                                            .padding(horizontal = 20.dp),
                                        contentAlignment = if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) 
                                            Alignment.CenterStart else Alignment.CenterEnd
                                    ) {
                                        Icon(
                                            imageVector = if (subject.isFavorite) Icons.Outlined.StarBorder else Icons.Default.Star,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }
                                },
                                content = {
                                    SubjectCardItem(
                                        subject = subject,
                                        onFavoriteToggle = { onIntent(SubjectsIntent.OnFavoriteToggle(it)) },
                                        onClick = { onIntent(SubjectsIntent.OnSubjectClick(it)) }
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SubjectCardItem(
    subject: Subject,
    onFavoriteToggle: (Subject) -> Unit,
    onClick: (Subject) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(subject) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE0F2FE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        tint = Color(0xFF0284C7)
                    )
                }
                Column {
                    Text(
                        text = subject.name,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = TextMain
                    )
                    Text(
                        text = subject.code,
                        fontSize = 12.sp,
                        color = TextSub
                    )
                }
            }

            IconButton(onClick = { onFavoriteToggle(subject) }) {
                Icon(
                    imageVector = if (subject.isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                    contentDescription = "Favorito",
                    tint = if (subject.isFavorite) Color(0xFFEAB308) else Color(0xFF94A3B8)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SubjectsScreenPreview() {
    SubjectsScreenContent(
        state = SubjectsState(),
        onIntent = {}
    )
}
