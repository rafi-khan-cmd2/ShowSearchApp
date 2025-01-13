package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.screens.HomeScreen
import com.example.myapplication.screens.SearchScreen
import com.example.myapplication.screens.ShowActorDetails
import com.example.myapplication.screens.ShowDetailsScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.network.KtorClient
import com.example.network.models.domain.Actor
import com.example.network.models.domain.Show

class MainActivity : ComponentActivity() {

    private val ktorClient = KtorClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            //var actor by remember {
            //    mutableStateOf<Actor?>(null)
            //}

            //LaunchedEffect(key1 = Unit, block = {
            //    actor = ktorClient.getActor(1)
            //})


            MyApplicationTheme {
                /*
                Box(modifier = Modifier.padding(50.dp)
                )  {
                    Column {
                        Text(text = actor?.name ?: "No actor")
                        actor?.shows?.get(2)?.let { Text(text = it.name) }
                    }
                }*/
                //ShowDetailsScreen(ktorClient = ktorClient, showId = 22)
                //ShowActorDetails(ktorClient, 1)
                //HomeScreen(ktorClient = ktorClient)
                AppNavGraph(ktorClient)
                //SearchScreen()
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        //Greeting("Android")
    }
}