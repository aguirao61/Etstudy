package com.example.studyapp.user_profile.presentation.screens.community_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studyapp.R
import com.example.studyapp.core.presentation.ui.theme.*
import com.example.studyapp.core.util.NumberFormatter
import com.example.studyapp.user_profile.domain.models.User
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    viewModel: CommunityViewModel = viewModel(),
    onBackClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()

    // Synchronize pager with ViewModel state
    LaunchedEffect(state.leaderboardType) {
        val targetPage = when (state.leaderboardType) {
            LeaderboardType.LEVEL -> 0
            LeaderboardType.STUDY_POINTS -> 1
        }
        if (pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    // Synchronize ViewModel sate with pager
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            val targetType = when (page) {
                0 -> LeaderboardType.LEVEL
                else -> LeaderboardType.STUDY_POINTS
            }
            if (state.leaderboardType != targetType) {
                viewModel.onLeaderboardTypeChange(targetType)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Comunidad",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
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
            // Leaderboard selector
            LeaderboardToggle(
                selectedType = state.leaderboardType,
                onTypeChange = { type ->
                    scope.launch {
                        viewModel.onLeaderboardTypeChange(type)
                        val targetPage = when (type) {
                            LeaderboardType.LEVEL -> 0
                            LeaderboardType.STUDY_POINTS -> 1
                        }
                        pagerState.animateScrollToPage(targetPage)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            } else {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.Top
                ) { page ->
                    val type = when (page) {
                        0 -> LeaderboardType.LEVEL
                        else -> LeaderboardType.STUDY_POINTS
                    }
                    
                    // Organize according to selected stat
                    val usersForPage = when (type) {
                        LeaderboardType.LEVEL -> state.users.sortedWith(compareByDescending<User> { it.level }.thenByDescending { it.experience })
                        LeaderboardType.STUDY_POINTS -> state.users.sortedByDescending { it.studyPoints }
                    }

                    LeaderboardList(
                        users = usersForPage,
                        leaderboardType = type,
                        currentUserId = state.currentUserId
                    )
                }
            }
        }
    }
}

@Composable
fun LeaderboardToggle(
    selectedType: LeaderboardType,
    onTypeChange: (LeaderboardType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(16.dp))
            .background(StudyTheme.cardBg)
            .border(1.dp, StudyTheme.cardBorder, RoundedCornerShape(16.dp))
            .padding(4.dp)
    ) {
        LeaderboardOption(
            text = "Nivel",
            isSelected = selectedType == LeaderboardType.LEVEL,
            onClick = { onTypeChange(LeaderboardType.LEVEL) },
            modifier = Modifier.weight(1f).fillMaxHeight()
        )
        LeaderboardOption(
            text = "Puntos",
            isSelected = selectedType == LeaderboardType.STUDY_POINTS,
            onClick = { onTypeChange(LeaderboardType.STUDY_POINTS) },
            modifier = Modifier.weight(1f).fillMaxHeight()
        )
    }
}

@Composable
fun LeaderboardOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp)),
        color = if (isSelected) PrimaryBlue else Color.Transparent,
        contentColor = if (isSelected) Color.White else StudyTheme.textSub
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = text, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
fun LeaderboardList(
    users: List<User>,
    leaderboardType: LeaderboardType,
    currentUserId: Int
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        itemsIndexed(users) { index, user ->
            LeaderboardItem(
                rank = index + 1,
                user = user,
                leaderboardType = leaderboardType,
                isCurrentUser = user.uniqueUserId == currentUserId
            )
        }
    }
}

@Composable
fun LeaderboardItem(
    rank: Int,
    user: User,
    leaderboardType: LeaderboardType,
    isCurrentUser: Boolean
) {
    val isTop3 = rank <= 3
    val rankColor = when (rank) {
        1 -> Color(0xFFFFD700) // Oro
        2 -> Color(0xFFC0C0C0) // Plata
        3 -> Color(0xFFCD7F32) // Bronce
        else -> StudyTheme.textSub.copy(alpha = 0.5f)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentUser) PrimaryBlue.copy(alpha = 0.1f) else StudyTheme.cardBg
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isCurrentUser) 2.dp else if (isTop3) 2.dp else 1.dp,
            color = if (isCurrentUser) PrimaryBlue else if (isTop3) rankColor else StudyTheme.cardBorder
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank
            Box(
                modifier = Modifier.size(32.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isTop3) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = rankColor,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = rank.toString(),
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = if (isCurrentUser) PrimaryBlue else StudyTheme.textSub
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isCurrentUser) PrimaryBlue.copy(alpha = 0.2f) else StudyTheme.surfaceBg),
                contentAlignment = Alignment.Center
            ) {
                if (user.equippedIconUrl != null) {
                    val context = LocalContext.current
                    val resId = remember(user.equippedIconUrl) {
                        val id = context.resources.getIdentifier(user.equippedIconUrl, "drawable", context.packageName)
                        if (id != 0) id else R.drawable.img
                    }
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = resId),
                        contentDescription = null,
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = if (isCurrentUser) PrimaryBlue else StudyTheme.textSub
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Name
            Text(
                text = user.username + (if (isCurrentUser) " (Tú)" else ""),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = if (isCurrentUser) PrimaryBlue else StudyTheme.textMain,
                modifier = Modifier.weight(1f)
            )

            // Stat
            Column(horizontalAlignment = Alignment.End) {
                when (leaderboardType) {
                    LeaderboardType.LEVEL -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "NIVEL ",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCurrentUser) PrimaryBlue else LevelColor
                            )
                            Text(
                                text = user.level.toString(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isCurrentUser) PrimaryBlue else LevelColor
                            )
                        }
                        Text(
                            text = "${NumberFormatter.formatWithCommas(user.experience)} EXP",
                            fontSize = 10.sp,
                            color = if (isCurrentUser) PrimaryBlue.copy(alpha = 0.7f) else StudyTheme.textSub
                        )
                    }
                    LeaderboardType.STUDY_POINTS -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = NumberFormatter.formatWithCommas(user.studyPoints),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = PrimaryBlue
                            )
                        }
                    }
                }
            }
        }
    }
}

