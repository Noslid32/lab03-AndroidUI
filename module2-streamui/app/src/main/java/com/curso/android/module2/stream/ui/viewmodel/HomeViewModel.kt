package com.curso.android.module2.stream.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.curso.android.module2.stream.data.model.Category
import com.curso.android.module2.stream.data.model.Song
import com.curso.android.module2.stream.data.repository.MusicRepository
import kotlinx.coroutines.flow.*

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val categories: List<Category>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel(
    private val repository: MusicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Este es el flujo que consume la pantalla de Highlights
    val favoriteSongs: StateFlow<List<Song>> = uiState.map { state ->
        if (state is HomeUiState.Success) {
            state.categories.flatMap { it.songs }.filter { it.isFavorite }.distinctBy { it.id }
        } else emptyList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadCategories()
    }

    private fun loadCategories() {
        _uiState.value = HomeUiState.Loading
        val categories = repository.getCategories()
        _uiState.value = HomeUiState.Success(categories)
    }

    fun toggleFavorite(songId: String) {
        val current = _uiState.value
        if (current is HomeUiState.Success) {
            val updatedCategories = current.categories.map { category ->
                category.copy(
                    songs = category.songs.map { song ->
                        if (song.id == songId) song.copy(isFavorite = !song.isFavorite)
                        else song
                    }
                )
            }
            _uiState.value = HomeUiState.Success(updatedCategories)
        }
    }
}