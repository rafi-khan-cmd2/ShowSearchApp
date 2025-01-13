package com.example.myapplication.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myapplication.ui.theme.Pink40
import com.example.network.KtorClient
import com.example.network.models.remote.RemoteShowWithoutEmbed

@Composable
fun HomeScreen(ktorClient: KtorClient, navController: NavController) {
    var shows by remember { mutableStateOf<List<RemoteShowWithoutEmbed>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    val onShowClick: (Int) -> Unit = { showId ->
        navController.navigate("showDetails/$showId") // Replace with navigation logic when integrating
    }

    // Fetch popular shows
    LaunchedEffect(Unit) {
        try {
            shows = ktorClient.getAllShows()
        } catch (e: Exception) {
            errorMessage = "Failed to fetch shows"
        } finally {
            isLoading = false
        }
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(color = Pink40) // Ensures background remains pink
        ) {
            Column {
                // Custom Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF5C362B))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Show Search App",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFFF4EED8)
                    )
                    IconButton(onClick = { navController.navigate("search") }) {
                        Icon(Icons.Default.Search, contentDescription = "Search Shows", tint = Color.White)
                    }
                }

                // Content Area
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                } else if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    // Filter genres to only include those with at least 3 shows
                    val showsByGenre = shows.groupBy { it.genres.firstOrNull() ?: "Uncategorized" }
                        .filter { it.value.size >= 3 }

                    LazyColumn(contentPadding = PaddingValues(16.dp), modifier = Modifier.background(color = Pink40)) {
                        showsByGenre.forEach { (genre, genreShows) ->
                            item {
                                Text(
                                    text = genre,
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(vertical = 8.dp).background(Color(0xFF5C362B)),
                                    color = Color(0xFFF4EED8)
                                )
                            }
                            item {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    items(genreShows) { show ->
                                        ShowCard(show, onShowClick = { onShowClick(show.id) })
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShowCard(show: RemoteShowWithoutEmbed, onShowClick: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .width(120.dp) // Keeps the card width fixed
            .padding(8.dp)
            .clickable { onShowClick(show.id) },
        horizontalAlignment = Alignment.CenterHorizontally // Centers content horizontally
    ) {
        // Show image
        show.image?.medium?.let { imageUrl ->
            AsyncImage(
                model = imageUrl,
                contentDescription = show.name,
                modifier = Modifier
                    .size(105.dp)
            // Size of the image
            // Optional: Rounded corners for the image
            )
        }

        // Show name directly under the image
        Text(
            text = show.name,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            modifier = Modifier
                .padding(top = 4.dp) // Spacing between image and name
                .align(Alignment.CenterHorizontally), // Ensures name is centered under the image
            color = Color(0xFFF4EED8),
            overflow = TextOverflow.Ellipsis // Handles overflow if the name is too long
        )
    }
}






