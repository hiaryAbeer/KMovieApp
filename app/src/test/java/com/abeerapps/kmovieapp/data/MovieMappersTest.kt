package com.abeerapps.kmovieapp.data

import com.abeerapps.kmovieapp.data.mapper.toBestTrailer
import com.abeerapps.kmovieapp.data.mapper.toDomain
import com.abeerapps.kmovieapp.data.remote.dto.MovieDetailsDto
import com.abeerapps.kmovieapp.data.remote.dto.VideoDto
import com.abeerapps.kmovieapp.domain.model.Trailer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MovieMappersTest {

    @Test
    fun `best trailer prefers official YouTube trailer`() {
        val videos = listOf(
            VideoDto(key = "teaser", site = "YouTube", type = "Teaser", official = true),
            VideoDto(key = "fan", site = "YouTube", type = "Trailer", official = false),
            VideoDto(key = "vimeo", site = "Vimeo", type = "Trailer", official = true),
            VideoDto(key = "official", site = "YouTube", type = "Trailer", official = true),
        )

        assertEquals(Trailer("official"), videos.toBestTrailer())
    }

    @Test
    fun `best trailer falls back to teaser`() {
        val videos = listOf(
            VideoDto(key = "clip", site = "YouTube", type = "Clip"),
            VideoDto(key = "teaser", site = "YouTube", type = "Teaser"),
        )

        assertEquals(Trailer("teaser"), videos.toBestTrailer())
    }

    @Test
    fun `best trailer is null when there are no YouTube trailers`() {
        val videos = listOf(VideoDto(key = "clip", site = "YouTube", type = "Featurette"))

        assertNull(videos.toBestTrailer())
    }

    @Test
    fun `details mapping normalises blank and zero values to null`() {
        val dto = MovieDetailsDto(id = 1, tagline = "", releaseDate = "", runtime = 0)

        val details = dto.toDomain()

        assertNull(details.tagline)
        assertNull(details.releaseDate)
        assertNull(details.runtimeMinutes)
    }
}
