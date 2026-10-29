package com.abeerapps.kmovieapp.data

import androidx.paging.PagingSource
import androidx.paging.testing.TestPager
import com.abeerapps.kmovieapp.data.paging.TmdbPagingSource
import com.abeerapps.kmovieapp.data.remote.dto.MovieDto
import com.abeerapps.kmovieapp.data.remote.dto.MoviesPageDto
import com.abeerapps.kmovieapp.domain.model.DataError
import com.abeerapps.kmovieapp.domain.model.DataErrorException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.IOException

class TmdbPagingSourceTest {

    private fun page(page: Int, totalPages: Int, vararg ids: Long) =
        MoviesPageDto(page = page, results = ids.map { MovieDto(id = it) }, totalPages = totalPages)

    @Test
    fun `first page has next key and no previous key`() = runTest {
        val pager = TestPager(TmdbPagingSource.pagingConfig, TmdbPagingSource { page(it, 3, 1, 2) })

        val result = pager.refresh() as PagingSource.LoadResult.Page

        assertNull(result.prevKey)
        assertEquals(2, result.nextKey)
        assertEquals(listOf(1L, 2L), result.data.map { it.id })
    }

    @Test
    fun `last page has no next key`() = runTest {
        val pager = TestPager(TmdbPagingSource.pagingConfig, TmdbPagingSource { page(it, 1, 1) })

        val result = pager.refresh() as PagingSource.LoadResult.Page

        assertNull(result.nextKey)
    }

    @Test
    fun `stops at TMDB's 500 page limit`() = runTest {
        val pager = TestPager(TmdbPagingSource.pagingConfig, TmdbPagingSource { page(it, 1_000, it.toLong()) })

        val result = pager.refresh(initialKey = 500) as PagingSource.LoadResult.Page

        assertNull(result.nextKey)
    }

    @Test
    fun `removes movies repeated within a page`() = runTest {
        val pager = TestPager(TmdbPagingSource.pagingConfig, TmdbPagingSource { page(it, 1, 7, 7, 8) })

        val result = pager.refresh() as PagingSource.LoadResult.Page

        assertEquals(listOf(7L, 8L), result.data.map { it.id })
    }

    @Test
    fun `network failure becomes a NETWORK error`() = runTest {
        val pager = TestPager(TmdbPagingSource.pagingConfig, TmdbPagingSource { throw IOException() })

        val result = pager.refresh() as PagingSource.LoadResult.Error

        assertEquals(DataError.NETWORK, (result.throwable as DataErrorException).error)
    }
}
