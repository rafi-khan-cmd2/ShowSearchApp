package com.example.network.models.domain

import com.example.network.models.domain.Show.Embedded

data class Actor(
    val actorId: Int,
    val name: String,
    val country: Country?,
    val birthDay: String?,
    val deathDay: String?,
    val gender: String?,
    val imageURL: ImageURL?,
    val shows: List<Show>
) {
    data class ImageURL(
        val medium: String,
        val original: String
    )
    data class Country(
        val name: String,
        val code: String
    )
    data class Embedded(
        val castCredits: List<Show>
    )
    data class Show(
        val showId: Int,
        val name: String
    )
}