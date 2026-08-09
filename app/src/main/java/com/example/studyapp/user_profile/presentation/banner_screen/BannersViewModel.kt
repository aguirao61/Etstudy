package com.example.studyapp.user_profile.presentation.banner_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.user_profile.domain.repositories.BannerRepository
import com.example.studyapp.user_profile.domain.repositories.UserRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BannersViewModel(
    private val bannerRepository: BannerRepository,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(BannersState())
    val state: StateFlow<BannersState> = _state.asStateFlow()

    private val _effect = Channel<BannersEffect>()
    val effect = _effect.receiveAsFlow()

    private val userId: Int = savedStateHandle.get<Int>("userId") ?: 0

    init {
        loadBanners()
    }

    private fun loadBanners() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            // Carga reactiva de banners
            bannerRepository.getUserBanners(userId).collectLatest { banners ->
                val user = userRepository.getUserById(userId)
                _state.update { it.copy(
                    banners = banners,
                    selectedBannerId = user?.equippedBannerId ?: 0, // 0 for "None"
                    isLoading = false
                ) }
            }
        }
    }

    fun onIntent(intent: BannersIntent) {
        when (intent) {
            is BannersIntent.OnBannerSelect -> {
                if (intent.bannerId == 0) {
                    _state.update { it.copy(selectedBannerId = 0) }
                } else {
                    val banner = _state.value.banners.find { it.id == intent.bannerId }
                    if (banner?.isObtained == true) {
                        _state.update { it.copy(selectedBannerId = intent.bannerId) }
                    }
                }
            }
            BannersIntent.OnEquipClick -> {
                equipSelectedBanner()
            }
        }
    }

    private fun equipSelectedBanner() {
        val selectedId = _state.value.selectedBannerId ?: return
        viewModelScope.launch {
            // Logic in repo: if id <= 0 -> set null
            bannerRepository.equipBanner(userId, selectedId)
            _effect.send(BannersEffect.NavigateBack)
        }
    }
}
