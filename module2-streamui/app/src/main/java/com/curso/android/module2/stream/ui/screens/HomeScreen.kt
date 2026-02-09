package com.curso.android.module2.stream.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.curso.android.module2.stream.data.model.Category
import com.curso.android.module2.stream.data.model.Song
import com.curso.android.module2.stream.ui.components.SongCoverMock
import com.curso.android.module2.stream.ui.viewmodel.HomeUiState
import com.curso.android.module2.stream.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onSongClick: (Song) -> Unit,
    viewModel: HomeViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is HomeUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is HomeUiState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error al cargar música")
            }
        }
        is HomeUiState.Success -> {
            val categories = (uiState as HomeUiState.Success).categories
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(categories, key = { it.name }) { category ->
                    CategorySection(
                        category = category,
                        onSongClick = onSongClick,
                        onFavoriteClick = { songId -> viewModel.toggleFavorite(songId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategorySection(
    category: Category,
    onSongClick: (Song) -> Unit,
    onFavoriteClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = category.name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(8.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(category.songs, key = { it.id }) { song ->
                SongCard(
                    song = song,
                    onClick = { onSongClick(song) },
                    onFavoriteClick = { onFavoriteClick(song.id) }
                )
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

// IMPORTANTE: SIN LA PALABRA 'PRIVATE'
@Composable
fun SongCard(
    song: Song,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(120.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            SongCoverMock(colorSeed = song.colorSeed, size = 120.dp)
            Text(
                text = if (song.isFavorite) "❤️" else "🤍",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clickable { onFavoriteClick() }
                    .padding(6.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = song.title, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Bold)
        Text(text = song.artist, style = MaterialTheme.typography.bodySmall, maxLines = 1)
    }
}