package com.example.network.models.domain

data class ShowWithoutEmbed(
    val id: Int,
    val name: String,
    val language: String,
    val genres: List<String>
)