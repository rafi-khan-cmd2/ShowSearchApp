package com.example.network.models.remote

import com.example.network.models.remote.RemoteShowWithoutEmbed.Image
import kotlinx.serialization.Serializable

@Serializable
data class RemoteSearchShow (
    val show: Show
){
    @Serializable
    data class Show(
        val id: Int,
        val name: String,
        val language: String,
        val genres: List<String>,
        val image: Image? = null
    )
}