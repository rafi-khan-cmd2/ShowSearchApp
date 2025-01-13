package com.example.myapplication.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.navigation.NavController
import com.example.myapplication.ui.theme.Pink40
import com.example.network.KtorClient
import com.example.network.models.domain.ShowWithoutEmbed
import com.example.network.models.remote.RemoteSearchShow
import com.example.network.models.remote.RemoteShowWithoutEmbed
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.graphics.SolidColor


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun SearchScreen(navController: NavController, ktorClient: KtorClient) {
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<RemoteSearchShow>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val onShowClick: (Int) -> Unit = { showId ->
        navController.navigate("showDetails/$showId") // Replace with navigation logic when integrating
    }

    Column {
        // Custom top section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // This ensures that the Box takes up remaining space
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(results) { show ->
                    TVShowItem(show, onShowClick = { onShowClick(show.show.id) })
                }
            }
        }

        // Spacer to push the search bar to the bottom by 35.dp
        Spacer(modifier = Modifier.height(35.dp))

        // Search bar section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(color = Color(0xFF5C362B))
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { navController.popBackStack() }
                )
                Spacer(modifier = Modifier.width(8.dp))

                TextField(
                    value = query,
                    onValueChange = { newQuery -> query = newQuery },
                    placeholder = {
                        Text(
                            text = "Search TV Shows...",
                            color = Color.Black,
                            fontSize = 16.sp// Placeholder color
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        //.height(100.dp)
                        //.padding(top = 0.dp, bottom = 0.dp)
                        .background(Color.Transparent), // Transparent background
                    textStyle = TextStyle(
                        color = Color.Black, // Text color for entered text
                        fontSize = 16.sp
                    )
                )
            }
        }

        // Trigger search with debounce
        LaunchedEffect(query) {
            if (query.isNotEmpty()) {
                isLoading = true
                errorMessage = null // Reset error message
                kotlinx.coroutines.delay(500) // Debounce delay
                try {
                    results = ktorClient.getSearchedShows(query)
                } catch (e: Exception) {
                    errorMessage = "Failed to fetch search results. Please try again."
                    results = emptyList()
                } finally {
                    isLoading = false
                }
            } else {
                results = emptyList()
            }
        }

        // Display results or errors
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize().background(color = Pink40), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFF4EED8))
                }
            }
            errorMessage != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }
            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize().background(color = Pink40)) {
                    items(results) { show ->
                        TVShowItem(show, onShowClick = { onShowClick(show.show.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun TVShowItem(show: RemoteSearchShow, onShowClick: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(color = Pink40)
            .clickable { onShowClick(show.show.id) }
    ) {
        Text(text = show.show.name, style = MaterialTheme.typography.bodySmall, color = Color(0xFFF4EED8), fontSize = 20.sp)
    }
}




