package com.example.network.models.remote


import com.example.network.models.domain.Show
import com.example.network.models.domain.Show.Embedded
import com.example.network.models.domain.Show.Image
import com.example.network.models.domain.ShowStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteShow(
    val id: Int,
    val name: String,
    val language: String,
    val genres: List<String>,
    val status: String,
    val premiered: String? = null,
    val ended: String? = null,
    val rating: Rating? = null,
    val image: Image? = null,
    val runtime: Int? = null,
    val summary: String? = null,
    @SerialName("_embedded")
    val embedded: Embedded
) {
    @Serializable
    data class Rating(
        val average: Double? = null
    )
    @Serializable
    data class Image(
        val medium: String,
        val original: String
    )
    @Serializable
    data class Embedded(
        val cast: List<CastMember>
    )
    @Serializable
    data class CastMember(
        val person: Person,
        val character: Character
    )
    @Serializable
    data class Person(
        val id:Int,
        val name: String,
        val image: Image? = null
    )
    @Serializable
    data class Character(
        val id:Int,
        val name: String,
        val image: Image? = null
    )
}

fun RemoteShow.toDomainShow(): Show {
    val showStatus = when (status.lowercase()) {
        "running" -> ShowStatus.Running
        "ended" -> ShowStatus.Ended
        else -> ShowStatus.Unknown
    }

    return Show(
        id = id,
        name = name,
        language = language,
        genres = genres,
        status = showStatus,
        startDate = premiered,
        endDate = ended,
        runtime = runtime,
        imageURL = image?.let { Show.Image(medium = image.medium, original = image.original) },
        summary = summary,
        rating = Show.Rating(average = rating?.average),
        embedded = Show.Embedded(
            cast = embedded.cast.map { castMember ->
                Show.CastMember(
                    person = Show.Person(
                        id = castMember.person.id,
                        name = castMember.person.name,
                        image = castMember.person.image?.let {
                            Show.Image(
                                medium = it.medium,
                                original = castMember.person.image.original
                            )
                        }
                    ),
                    character = Show.Character(
                        id = castMember.character.id,
                        name = castMember.character.name,
                        image = castMember.character.image?.let {
                            Show.Image(medium = it.medium, original = it.original)
                        }
                    )
                )
            }
        )
    )
}