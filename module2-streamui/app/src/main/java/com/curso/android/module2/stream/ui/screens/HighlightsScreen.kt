package com.curso.android.module2.stream.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.curso.android.module2.stream.data.model.Song

@Composable
fun HighlightsScreen(
    favoriteSongs: List<Song>,
    onSongClick: (Song) -> Unit,
    onFavoriteClick: (String) -> Unit
) {
    if (favoriteSongs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "No favorite songs yet", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(favoriteSongs, key = { it.id }) { song ->
                SongCard(
                    song = song,
                    onClick = { onSongClick(song) },
                    onFavoriteClick = { onFavoriteClick(song.id) }
                )
            }
        }
    }
}