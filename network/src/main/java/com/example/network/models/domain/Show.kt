package com.example.network.models.domain

import androidx.resourceinspection.annotation.Attribute.IntMap
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class Show(
    val id: Int,
    val name: String,
    val language: String,
    val genres: List<String>,
    val status: ShowStatus,
    val startDate: String? = null,
    val endDate: String? = null,
    val runtime: Int? = null,
    val summary: String? = null,
    val imageURL: Image? = null,
    val rating: Rating? = null,
    val embedded: Embedded? = null

) {
    data class Rating(
        val average: Double? = null
    )
    data class Image(
        val medium: String,
        val original: String
    )
    data class Embedded(
        val cast: List<CastMember>
    )
    data class CastMember(
        val person: Person,
        val character: Character
    )
    data class Person(
        val id:Int,
        val name: String,
        val image: Image? = null
    )
    data class Character(
        val id:Int,
        val name: String,
        val image:Image? = null
    )
}

