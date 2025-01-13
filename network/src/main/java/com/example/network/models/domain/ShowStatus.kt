package com.example.network.models.domain

import androidx.compose.ui.graphics.Color

sealed class ShowStatus(val displayStatus: String, val color: Color){
    object Ended: ShowStatus("Ended", Color.Red)
    object Running: ShowStatus("Running", Color.Green)
    object Unknown: ShowStatus("Unknown", Color.Yellow)
}