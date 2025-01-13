package com.example.network.models.remote

import com.example.network.models.domain.Actor
import com.example.network.models.domain.Show
import com.example.network.models.domain.ShowStatus
import com.example.network.models.remote.RemoteShow.Embedded
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteActor(
    @SerialName("id") val actorId: Int,
    @SerialName("name") val name: String,
    @SerialName("country") val country: Country? = null,
    @SerialName("birthday") val birthDay: String? = null,
    @SerialName("deathday") val deathDay: String? = null,
    @SerialName("gender") val gender: String? = null,
    @SerialName("image") val imageURL: ImageURL? = null,
    @SerialName("_embedded") val embedded: Embedded? = null
) {
    @Serializable
    data class ImageURL(
        @SerialName("medium") val medium: String? = null,
        @SerialName("original") val original: String? = null
    )

    @Serializable
    data class Country(
        @SerialName("name") val name: String? = null,
        @SerialName("code") val code: String? = null
    )

    @Serializable
    data class Embedded(
        @SerialName("castcredits") val castCredits: List<Work>
    )

    @Serializable
    data class Work(
        @SerialName("_links") val links: Links
    )

    @Serializable
    data class Links(
        @SerialName("show") val show: ShowLink
    )

    @Serializable
    data class ShowLink(
        @SerialName("href") val href: String,
        @SerialName("name") val name: String
    )
}

fun extractWorkId(href: String): Int? {
    // Extracts the numerical ID from the URL.
    val regex = """\d+$""".toRegex()
    return regex.find(href)?.value?.toInt()
}

fun RemoteActor.toDomainActor(): Actor {
    // Log the raw response data for debugging
    //Log.d("RemoteActor", "actorId: $actorId, name: $name, embedded: ${embedded?.castCredits}")

    val shows = embedded?.castCredits?.mapNotNull { work ->
        // Extract show ID from the href within the _links.show object
        val showId = extractWorkId(work.links.show.href)
        if (showId != null && work.links.show.name.isNotEmpty()) {
            Actor.Show(
                showId = showId,
                name = work.links.show.name
            )
        } else {
            //Log.d("RemoteActor", "Skipping work: Invalid href or name")
            null // Skip invalid or incomplete entries
        }
    } ?: emptyList() // Default to an empty list if no shows exist

    return Actor(
        actorId = actorId,
        name = name,
        country = country?.let {
            Actor.Country(
                name = it.name ?: "",
                code = it.code ?: ""
            )
        },
        birthDay = birthDay,
        deathDay = deathDay,
        gender = gender,
        imageURL = imageURL?.let {
            Actor.ImageURL(
                medium = it.medium ?: "",
                original = it.original ?: ""
            )
        },
        shows = shows
    )
}



/* @Serializable
data class RemoteActor(
    val actorId: Int,
    val name: String,
    val country: Country? = null,
    val birthDay: String?,
    val deathDay: String?,
    val gender: String?,
    val imageURL: ImageURL?,
    val embedded: Embedded? = null
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
        @SerialName("castcredits") val castCredits: List<Work>
    )
    data class Work(
        val showId: Int,
        val name: String
    )

}

fun extractWorkId(href: String): Int? {
    val regex = """\d+$""".toRegex()
    return regex.find(href)?.value?.toInt()
}


fun RemoteActor.toDomainActor(): Actor {
    val shows = embedded?.castCredits?.mapNotNull { work ->
        val workId = extractWorkId(work.workLink)
        workId?.let {
            Show(
                showId = it,
                name = work.name,
                status = Show.Status.UNKNOWN // Placeholder for status; replace with logic if needed
            )
        }
    }

    return Actor(
        actorId = actorId,
        name = name,
        country = country?.let { Actor.Country(name = it.name ?: "", code = it.code ?: "") },
        birthDay = birthDay,
        deathDay = deathDay,
        gender = gender,
        imageURL = imageURL?.let { Actor.ImageURL(medium = it.medium ?: "", original = it.original ?: "") },
        shows = shows
    )
}
 */

/*
@Serializable
data class RemoteActor(
    @SerialName("id") val actorId: Int,
    @SerialName("name") val name: String,
    @SerialName("country") val country: Country? = null,
    @SerialName("birthday") val birthDay: String? = null,
    @SerialName("deathday") val deathDay: String? = null,
    @SerialName("gender") val gender: String? = null,
    @SerialName("image") val imageURL: ImageURL? = null,
    @SerialName("_embedded") val embedded: Embedded? = null
) {
    @Serializable
    data class ImageURL(
        @SerialName("medium") val medium: String? = null,
        @SerialName("original") val original: String? = null
    )

    @Serializable
    data class Country(
        @SerialName("name") val name: String? = null,
        @SerialName("code") val code: String? = null
    )

    @Serializable
    data class Embedded(
        @SerialName("castcredits") val castCredits: List<Work>
    )

    @Serializable
    data class Link(
        @SerialName("_links") val links:
    )

    @Serializable
    data class Work(
        @SerialName("href") val workLink: String, // The API provides this as `href`
        @SerialName("name") val name: String
    )
}

fun extractWorkId(href: String): Int? {
    val regex = """\d+$""".toRegex()
    return regex.find(href)?.value?.toInt()
}

fun RemoteActor.toDomainActor(): Actor {
    val shows = embedded?.castCredits?.mapNotNull { work ->
        val showId = extractWorkId(work.workLink) // Extract the showId from workLink (href)
        showId?.let {
            Actor.Show(
                showId = it,
                name = work.name
            )
        }
    } ?: emptyList() // Default to an empty list if no cast credits exist

    return Actor(
        actorId = actorId,
        name = name,
        country = country?.let {
            Actor.Country(
                name = it.name ?: "", // Default empty string if null
                code = it.code ?: ""  // Default empty string if null
            )
        },
        birthDay = birthDay,
        deathDay = deathDay,
        gender = gender,
        imageURL = imageURL?.let {
            Actor.ImageURL(
                medium = it.medium ?: "", // Default empty string if null
                original = it.original ?: "" // Default empty string if null
            )
        },
        shows = shows
    )
}
 */



