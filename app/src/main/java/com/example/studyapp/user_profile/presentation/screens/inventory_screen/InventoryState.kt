package com.example.studyapp.user_profile.presentation.screens.inventory_screen

import com.example.studyapp.user_profile.domain.models.Icon

data class InventoryState(
    val icons: List<Icon> = emptyList(),
    val equippedIconId: Int? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
