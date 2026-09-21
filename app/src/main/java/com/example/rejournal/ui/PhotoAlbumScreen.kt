package com.example.rejournal.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.example.rejournal.data.MediaGalleryHelper
import com.example.rejournal.data.MediaItem
import com.example.rejournal.ui.components.ButterflyCardWrapper
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.layout.ContentScale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoAlbumScreen(
    viewModel: MoodViewModel,
    onPhotoClick: (MediaItem) -> Unit,
    onBack: () -> Unit
) {
    val entries by viewModel.allEntries.collectAsState()
    val photos = remember(entries) { MediaGalleryHelper.allPhotos(entries) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { CenterAlignedTopAppBar(title = { Text("Photo Album") }, navigationIcon = { BackButton(onBack) }) }
    ) { padding: PaddingValues ->
        if (photos.isEmpty()) {
            ButterflyCardWrapper(seed = "PhotoEmpty", indexOffset = 0, modifier = Modifier.fillMaxWidth().padding(padding).padding(16.dp)) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = softCardShape,
                    border = softCardBorder()
                ) {
                    Text(
                        "No photos yet. Add some from a day's entry.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                itemsIndexed(photos) { index, item ->
                    PhotoThumb(item = item, index = index, onClick = { onPhotoClick(item) })
                }
            }
        }
    }
}

@Composable
private fun PhotoThumb(item: MediaItem, index: Int, onClick: () -> Unit) {
    val bitmap = remember(item.path) {
        BitmapFactory.decodeFile(item.path)?.let { full ->
            Bitmap.createScaledBitmap(full, 300, 300, true)
        }
    }
    if (bitmap != null) {
        ButterflyCardWrapper(seed = "Photo_${item.path}", indexOffset = index) {
            Card(
                modifier = Modifier
                    .aspectRatio(1f)
                    .clickable(onClick = onClick),
                shape = softCardShape,
                border = softCardBorder()
            ) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Photo from ${item.date}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
