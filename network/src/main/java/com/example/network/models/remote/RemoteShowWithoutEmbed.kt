package com.example.network.models.remote

import com.example.network.models.remote.RemoteShow.Image
import kotlinx.serialization.Serializable

@Serializable
data class RemoteShowWithoutEmbed(
    val id: Int,
    val name: String,
    val language: String,
    val genres: List<String>,
    val image: Image? = null
) {
    @Serializable
    data class Image(
        val medium: String,
        val original: String
    )
}