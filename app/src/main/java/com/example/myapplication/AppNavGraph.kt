package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.screens.HomeScreen
import com.example.myapplication.screens.SearchScreen
import com.example.myapplication.screens.ShowActorDetails
import com.example.myapplication.screens.ShowDetailsScreen
import com.example.network.KtorClient

@Composable
fun AppNavGraph(ktorClient: KtorClient) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home" ) {
        composable("home") {
            HomeScreen(
                ktorClient = ktorClient,
                navController = navController
            )
        }

        composable(
            route = "showDetails/{showId}",
            arguments = listOf(navArgument("showId") { type = NavType.IntType })
        ) {
            BackStackEntry ->
            val showId = BackStackEntry.arguments?.getInt("showId") ?: return@composable
            ShowDetailsScreen(ktorClient = ktorClient, showId = showId, navController)
        }
        composable(
            route = "showActorDetails/{actorId}",
            arguments = listOf(navArgument("actorId") { type = NavType.IntType })
        ) {
                BackStackEntry ->
            val actorId = BackStackEntry.arguments?.getInt("actorId") ?: return@composable
            ShowActorDetails(ktorClient = ktorClient, actorId = actorId, navController)
        }
        composable("search") {
            SearchScreen(navController, ktorClient)
        }
    }


}