package com.example.network

import com.example.network.models.domain.Actor
import com.example.network.models.domain.Show
import com.example.network.models.remote.RemoteActor
import com.example.network.models.remote.RemoteSearchShow
import com.example.network.models.remote.RemoteShow
import com.example.network.models.remote.RemoteShowWithoutEmbed
import com.example.network.models.remote.toDomainActor
import com.example.network.models.remote.toDomainShow
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


class KtorClient {
    private val client = HttpClient(OkHttp) {
        defaultRequest{ url.takeFrom("https://api.tvmaze.com/")}

        install(Logging) {
            logger = Logger.SIMPLE
        }

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun getShow(id: Int): Show {
        return client.get("shows/$id?embed=cast").body<RemoteShow>().toDomainShow()
    }

    suspend fun getActor(id:Int): Actor {
        return client.get("people/$id?embed=castcredits").body<RemoteActor>().toDomainActor()
    }

    suspend fun getAllShows(): List<RemoteShowWithoutEmbed>{
        return try {
            client.get("shows").body<List<RemoteShowWithoutEmbed>>()
        } catch (e: Exception) {
            println("Error fetching all shows: ${e.message}")
            emptyList()
        }
    }

    suspend fun getSearchedShows(searchshow: String): List<RemoteSearchShow> {
        return try {
            client.get("search/shows") {
                parameter("q", searchshow)
            }.body()
        } catch (e: Exception) {
            println("Error fetching searched shows: ${e.message}")
            emptyList()
        }
    }
}


