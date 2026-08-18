package com.example.studyapp.user_profile.presentation.screens.profile_screen

sealed class ProfileIntent {
    object OnEditNameClick : ProfileIntent()
    data class OnTempNameChange(val name: String) : ProfileIntent()
    object OnSaveNameClick : ProfileIntent()
    object OnDismissEditDialog : ProfileIntent()
    object OnEditMusicClick : ProfileIntent()
    data class OnTempSongChange(val song: String) : ProfileIntent()
    data class OnTempArtistChange(val artist: String) : ProfileIntent()
    object OnSaveMusicClick : ProfileIntent()
    object OnDismissMusicDialog : ProfileIntent()
}