package com.abeerapps.kmovieapp.data

import com.abeerapps.kmovieapp.data.remote.ApiKeyInterceptor
import com.abeerapps.kmovieapp.data.remote.TmdbApi
import com.abeerapps.kmovieapp.data.remote.dto.FavoriteRequestDto
import com.abeerapps.kmovieapp.data.repository.MovieRepositoryImpl
import com.abeerapps.kmovieapp.di.NetworkModule
import com.abeerapps.kmovieapp.domain.model.DataError
import com.abeerapps.kmovieapp.domain.model.Outcome
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create

/**
 * Runs the real Retrofit + OkHttp + kotlinx.serialization stack against a local server,
 * with the same JSON configuration and API-key interceptor the app uses.
 */
class TmdbApiTest {

    private val server = MockWebServer()
    private lateinit var api: TmdbApi

    @Before
    fun setUp() {
        server.start()
        val client = OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor("test-key"))
            .build()
        api = Retrofit.Builder()
            .baseUrl(server.url("/3/"))
            .client(client)
            .addConverterFactory(
                NetworkModule.provideJson().asConverterFactory("application/json".toMediaType()),
            )
            .build()
            .create()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `search parses a real TMDB response, tolerating nulls, blanks and unknown fields`() =
        runTest {
            server.enqueue(MockResponse().setBody(SEARCH_RESPONSE))

            val page = api.searchMovies(query = "fight club", page = 1)

            assertEquals(2, page.totalPages)
            assertEquals(listOf(550L, 973534L), page.results.map { it.id })
            val unreleased = page.results.last()
            assertNull(unreleased.posterPath)
            assertEquals("", unreleased.releaseDate)
        }

    @Test
    fun `every request carries the API key and its own query parameters`() = runTest {
        server.enqueue(MockResponse().setBody(SEARCH_RESPONSE))

        api.searchMovies(query = "fight club", page = 2)

        val url = server.takeRequest().requestUrl!!
        assertEquals("/3/search/movie", url.encodedPath)
        assertEquals("fight club", url.queryParameter("query"))
        assertEquals("2", url.queryParameter("page"))
        assertEquals("test-key", url.queryParameter("api_key"))
    }

    @Test
    fun `favorite request body includes every field TMDB requires`() = runTest {
        server.enqueue(MockResponse().setBody("""{"status_code":1,"status_message":"Success."}"""))

        api.setFavorite(
            accountId = 7L,
            sessionId = "session",
            body = FavoriteRequestDto(mediaType = "movie", mediaId = 550L, favorite = true),
        )

        val body = server.takeRequest().body.readUtf8()
        assertTrue(body, body.contains(""""media_type":"movie""""))
        assertTrue(body, body.contains(""""media_id":550"""))
        assertTrue(body, body.contains(""""favorite":true"""))
    }

    @Test
    fun `HTTP and network failures map to domain errors`() = runTest {
        val repository = MovieRepositoryImpl(api)

        server.enqueue(MockResponse().setResponseCode(401))
        assertEquals(Outcome.Failure(DataError.UNAUTHORIZED), repository.getGenres())

        server.enqueue(MockResponse().setResponseCode(503))
        assertEquals(Outcome.Failure(DataError.SERVER), repository.getGenres())

        server.enqueue(MockResponse().setBody("""{"genres": "not a list"}"""))
        assertEquals(Outcome.Failure(DataError.UNKNOWN), repository.getGenres())
    }

    @Test
    fun `unreachable server maps to a network error`() = runTest {
        // A dropped connection is retried by OkHttp,
        // so the realistic case is a server that is gone.
        server.shutdown()

        assertEquals(Outcome.Failure(DataError.NETWORK), MovieRepositoryImpl(api).getGenres())
    }

    private companion object {
        /** Trimmed from a real /search/movie response, including its nulls and empty dates. */
        val SEARCH_RESPONSE = """
            {
              "page": 1,
              "results": [
                {
                  "adult": false,
                  "backdrop_path": "/hZkgoQYus5vegHoetLkCJzb17zJ.jpg",
                  "genre_ids": [18, 53, 35],
                  "id": 550,
                  "original_language": "en",
                  "original_title": "Fight Club",
                  "overview": "A ticking-time-bomb insomniac and a slippery soap salesman...",
                  "popularity": 73.433,
                  "poster_path": "/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg",
                  "release_date": "1999-10-15",
                  "title": "Fight Club",
                  "video": false,
                  "vote_average": 8.433,
                  "vote_count": 26279
                },
                {
                  "adult": false,
                  "backdrop_path": null,
                  "genre_ids": [],
                  "id": 973534,
                  "original_language": "ru",
                  "original_title": "Бойцовский клуб (русская версия)",
                  "overview": "The Russian version of the movie \"Fight Club\"...",
                  "popularity": 0.874,
                  "poster_path": null,
                  "release_date": "",
                  "title": "Fight Club (Russian version)",
                  "video": false,
                  "vote_average": 7.4,
                  "vote_count": 6
                }
              ],
              "total_pages": 2,
              "total_results": 39
            }
        """.trimIndent()
    }
}
