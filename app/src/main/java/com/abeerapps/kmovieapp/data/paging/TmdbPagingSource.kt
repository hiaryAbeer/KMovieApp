package com.abeerapps.kmovieapp.data.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.abeerapps.kmovieapp.data.mapper.toDomain
import com.abeerapps.kmovieapp.data.remote.dto.MoviesPageDto
import com.abeerapps.kmovieapp.data.remote.toDataError
import com.abeerapps.kmovieapp.domain.model.DataErrorException
import com.abeerapps.kmovieapp.domain.model.Movie
import kotlinx.coroutines.CancellationException

/** Pages through any TMDB endpoint that returns a page of movies. */
class TmdbPagingSource(private val fetchPage: suspend (page: Int) -> MoviesPageDto) :
    PagingSource<Int, Movie>() {

    // Same boundary as safeApiCall: toDataError() rethrows anything unexpected.
    @Suppress("TooGenericExceptionCaught")
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: FIRST_PAGE
        return try {
            val response = fetchPage(page)
            val lastPage = minOf(response.totalPages, MAX_PAGE)
            LoadResult.Page(
                // TMDB can repeat a movie across pages, which would break DiffUtil.
                data = response.results.map { it.toDomain() }.distinctBy { it.id },
                prevKey = if (page == FIRST_PAGE) null else page - 1,
                nextKey = if (page < lastPage) page + 1 else null,
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            LoadResult.Error(DataErrorException(e.toDataError()))
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? =
        state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.let { it.prevKey?.plus(1) ?: it.nextKey?.minus(1) }
        }

    companion object {
        private const val FIRST_PAGE = 1

        /** TMDB rejects page numbers above 500. */
        private const val MAX_PAGE = 500

        val pagingConfig = PagingConfig(pageSize = 20, enablePlaceholders = false)
    }
}
