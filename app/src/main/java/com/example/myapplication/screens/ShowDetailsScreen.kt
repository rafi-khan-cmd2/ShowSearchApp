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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.R
import com.example.myapplication.ui.theme.Pink40
import com.example.network.KtorClient
import com.example.network.models.domain.Show
import com.example.network.models.domain.ShowStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ShowDetailsScreen(
    ktorClient: KtorClient,
    showId: Int,
    navController: NavController
) {
    var show by remember { mutableStateOf<Show?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    val onShowClick: (Int) -> Unit = { actorId ->
        navController.navigate("showActorDetails/$actorId") // Replace with navigation logic when integrating
    }

    LaunchedEffect(showId) {
        scope.launch(Dispatchers.IO) {
            val result = ktorClient.getShow(showId)
            show = result
            isLoading = false
        }
    }

    if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        )
    } else {
        show?.let { show ->
            ShowScreen(show = show, onShowClick = onShowClick, navController = navController)
        }
    }
}

@Composable
fun ShowScreen(show: Show, onShowClick: (Int) -> Unit, navController: NavController) {
    var isSummaryExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Pink40)
    ) {
        // Scrollable Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                Spacer(modifier = Modifier.height(35.dp))
                Row {
                    AsyncImage(
                        model = show.imageURL?.medium,
                        contentDescription = "${show.name} image not available",
                        modifier = Modifier
                            .size(200.dp)
                            .padding(bottom = 5.dp, top = 5.dp)
                    )

                    Column {
                        Text(
                            text = show.name,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 30.sp
                            ),
                            color = Color(0xFFF4EED8),
                            modifier = Modifier.padding(end = 1.dp, bottom = 3.dp, top = 5.dp)
                        )

                        show.summary?.let { summary ->
                            Column {
                                val summaryText = android.text.Html.fromHtml(summary, android.text.Html.FROM_HTML_MODE_LEGACY).toString()

                                Text(
                                    text = if (isSummaryExpanded) summaryText else summaryText.take(100) + "...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFFF4EED8),
                                    modifier = Modifier.padding(1.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = { isSummaryExpanded = !isSummaryExpanded },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF5C362B),
                                        contentColor = Color(0xFFF4EED8)
                                    )
                                ) {
                                    Text(text = if (isSummaryExpanded) "Show Less" else "Read More")
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(5.dp))
                ShowGenres(show.genres)

                Spacer(modifier = Modifier.height(5.dp))
                show.rating?.average?.let { rating ->
                    Text(
                        text = "Rating: $rating",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFFF4EED8),
                        fontSize = 20.sp,
                        modifier = Modifier
                            .background(Color(0xFF5C362B), shape = RoundedCornerShape(20.dp))
                            .padding(horizontal = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Cast",
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
                    items(show.embedded?.cast ?: emptyList()) { castMember ->
                        CastItem(
                            castMember = castMember,
                            onShowClick = { onShowClick(castMember.person.id) }
                        )
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
fun CastMemberRow(castMember: Show.CastMember) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Cast member image
        castMember.person.image?.medium?.let { imageUrl ->
            AsyncImage(
                model = imageUrl,
                contentDescription = castMember.person.name,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Cast member name and character
        Column {
            Text(
                text = castMember.person.name,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "as ${castMember.character.name}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
    }
}


@Composable
private fun LoadingState() {
    CircularProgressIndicator(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 128.dp)
    )
}
@Composable
fun PreviewShowDetailsScreen() {
    val dummyShow = Show(
        id = 1,
        name = "Breaking Bad",
        language = "English",
        genres = listOf("Crime", "Drama", "Thriller","Comedy","Action","Horror","Sci-Fi"),
        status = ShowStatus.Ended,
        startDate = "2008-01-20",
        endDate = "2013-09-29",
        runtime = 47,
        summary = "A high school chemistry teacher turned methamphetamine producer teams up with a former student to secure his family's future while battling the criminal underworld.",
        imageURL = Show.Image(
            medium = "https://static.tvmaze.com/uploads/images/medium_portrait/0/2400.jpg",
            original = "https://static.tvmaze.com/uploads/images/original_untouched/0/2400.jpg"
        ),
        rating = Show.Rating(average = 9.5),
        embedded = Show.Embedded(
            cast = listOf(
                Show.CastMember(
                    person = Show.Person(
                        id = 1,
                        name = "Bryan Cranston",
                        image = Show.Image(
                            medium = "https://static.tvmaze.com/uploads/images/medium_portrait/0/1815.jpg",
                            original = "https://static.tvmaze.com/uploads/images/original_untouched/0/1815.jpg"
                        )
                    ),
                    character = Show.Character(
                        id = 1,
                        name = "Walter White"
                    )
                ),
                Show.CastMember(
                    person = Show.Person(
                        id = 2,
                        name = "Aaron Paul",
                        image = Show.Image(
                            medium = "https://static.tvmaze.com/uploads/images/medium_portrait/0/1819.jpg",
                            original = "https://static.tvmaze.com/uploads/images/original_untouched/0/1819.jpg"
                        )
                    ),
                    character = Show.Character(
                        id = 2,
                        name = "Jesse Pinkman"
                    )
                )
            )
        )
    )

    //ShowScreen(show = dummyShow)
}

@Composable
fun ShowGenres(genres: List<String>) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color(0xFF5C362B)),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(genres) { genre ->
            GenreChip(genre)
        }
    }
}

@Composable
fun GenreChip(genre: String) {
    Text(
        text = genre,
        modifier = Modifier
            .background(
                color = Color(0xFFF4EED8),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 12.dp),
        style = MaterialTheme.typography.bodySmall.copy(
            fontSize = 15.sp
        ),
        color = MaterialTheme.colorScheme.primary
    )
}


@Preview(showBackground = true)
@Composable
fun ShowDetailsPreview() {
    PreviewShowDetailsScreen()
}


@Composable
fun CastItem(castMember: Show.CastMember, onShowClick: (Int) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(100.dp) // Adjust width for each cast item
            .padding(horizontal = 8.dp)
            .clickable { onShowClick(castMember.person.id) }
    ) {
        // Cast Image
        castMember.person.image?.medium?.let { imageUrl ->
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = castMember.person.name,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Cast Name
        Text(
            text = castMember.person.name,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFFF4EED8),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 15.sp
        )

        // Character Name
        Text(
            text = "as ${castMember.character.name}",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFFF4EED8),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

