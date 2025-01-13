package com.example.myapplication.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.ui.theme.Pink40
import com.example.network.KtorClient
import com.example.network.models.domain.Actor
import com.example.network.models.domain.Show
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ShowActorDetails(
    ktorClient: KtorClient,
    actorId: Int,
    navController: NavController
) {
    var actor by remember { mutableStateOf<Actor?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(actorId) {
        scope.launch(Dispatchers.IO) {
            val result = ktorClient.getActor(actorId)
            actor = result
            isLoading = false
        }
    }

    if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center),
            color = Color(0xFFF4EED8)
        )
    } else {
        actor?.let { actor ->
            ShowActorScreen(actor, ktorClient, navController)
        }
    }
}

@Composable
fun ShowActorScreen(actor: Actor, ktorClient: KtorClient, navController: NavController) {
    val showWork = remember {
        derivedStateOf {
            actor.shows ?: emptyList()
        }
    }

    val onShowClick: (Int) -> Unit = { showId ->
        navController.navigate("showDetails/$showId")
    }

    Box(modifier = Modifier.fillMaxSize().background(color = Pink40)) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                // Actor details
                Spacer(modifier = Modifier.height(35.dp))
                Row {
                    AsyncImage(
                        model = actor.imageURL?.medium,
                        contentDescription = "${actor.name} image not available",
                        modifier = Modifier.size(200.dp).padding(bottom = 5.dp)
                    )

                    Column {
                        Text(
                            text = actor.name,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 30.sp
                            ),
                            color = Color(0xFFF4EED8),
                            modifier = Modifier.padding(5.dp)
                        )

                        Text(
                            text = "Born: ${actor.birthDay?.let { formatDateWithMonthName(it) } ?: ""}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFFF4EED8),
                            modifier = Modifier.padding(5.dp)
                        )

                        Text(
                            text = "Died: ${actor.deathDay?.let { formatDateWithMonthName(it) } ?: ""}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFFF4EED8),
                            modifier = Modifier.padding(5.dp)
                        )

                        Text(
                            text = "Country: ${actor.country?.name ?: ""}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFFF4EED8),
                            modifier = Modifier.padding(5.dp)
                        )
                    }
                }

                // Shows list
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Shows",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFFF4EED8),
                    fontSize = 20.sp,
                    modifier = Modifier.padding(16.dp)
                )

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    items(actor.shows ?: emptyList()) { show ->
                        ShowItem(showId = show.showId, ktorClient, onShowClick = { onShowClick(show.showId) })
                    }
                }
            }
        }

        // Fixed Row with Back Button and Home Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color(0xFF5C362B)) // Optional background for better visibility
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

            // Home Button
            Button(
                onClick = { navController.navigate("home") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5C362B),
                    contentColor = Color(0xFFF4EED8)
                ),
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = "Home",
                    fontSize = 15.sp
                )
            }
        }
    }
}



@Composable
fun ShowItem(showId: Int, ktorClient: KtorClient, onShowClick: (Int) -> Unit) {
    val scope = rememberCoroutineScope()
    var show by remember { mutableStateOf<Show?>(null) }

    LaunchedEffect(showId) {
        scope.launch(Dispatchers.IO) {
            val result = ktorClient.getShow(showId)
            show = result
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(100.dp)
            .padding(horizontal = 8.dp)
            .clickable {
                onShowClick(showId)
            }
    ) {
        // Show Image
        show?.imageURL?.medium?.let { imageUrl ->
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = show!!.name,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Show Name
        show?.let {
            Text(
                text = it.name,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = Color(0xFFF4EED8),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 15.sp
            )
        }
    }
}

fun formatDateWithMonthName(date: String, inputFormat: String = "yyyy-MM-dd"): String {
    val formatter = DateTimeFormatter.ofPattern(inputFormat, Locale.getDefault())
    val parsedDate = LocalDate.parse(date, formatter)
    val outputFormatter = DateTimeFormatter.ofPattern("dd MMMM, yyyy", Locale.getDefault())
    return parsedDate.format(outputFormatter)
}


