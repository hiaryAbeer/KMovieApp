package com.abeerapps.kmovieapp.ui

import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abeerapps.kmovieapp.domain.models.BaseData
import com.abeerapps.kmovieapp.domain.models.CastModel
import com.abeerapps.kmovieapp.domain.models.GenresRequestModel
import com.abeerapps.kmovieapp.domain.models.MovieDetailsModel
import com.abeerapps.kmovieapp.domain.models.MovieModel
import com.abeerapps.kmovieapp.domain.usecases.GetBaseDataUseCase
import com.abeerapps.kmovieapp.domain.usecases.GetCastUseCase
import com.abeerapps.kmovieapp.domain.usecases.GetGenresDataUseCase
import com.abeerapps.kmovieapp.domain.usecases.GetMovieDetailUseCase
import com.abeerapps.kmovieapp.domain.usecases.GetMoviesByGenresDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@InternalCoroutinesApi
class MovieViewModel @Inject constructor(
    private val mGetBaseDataUseCase: GetBaseDataUseCase,
    private val mGetGenresDataUseCase: GetGenresDataUseCase,
    private val mGetMoviesByGenresDataUseCase: GetMoviesByGenresDataUseCase,
    private val mCastUseCase: GetCastUseCase,
    private val mGetMovieDetailUseCase: GetMovieDetailUseCase,

    ) : ViewModel() {

    val mBaseData = MutableStateFlow(BaseData())
    val mGenresData = MutableStateFlow(BaseData())
    val mMoviesByGenresData = MutableStateFlow(BaseData())
    private val mSelectedMovie = MutableStateFlow(MovieModel())
    private val mActionSender = Channel<MovieActions> { }
    val mAction = mActionSender.receiveAsFlow()
    val mCastData = MutableSharedFlow<List<CastModel>>()
    val mMovieDetails = MutableSharedFlow<MovieDetailsModel>()
    val mIsMovieHasVideo = ObservableBoolean(true)
    val model = GenresRequestModel()

    init {
        getBaseData()
        getGenresData()
    }

    private fun getBaseData() {
        viewModelScope.launch {
            mGetBaseDataUseCase.getBaseData().collect {
                Log.d("getBaseData", it.list.size.toString())
                mBaseData.value = it
            }

        }
    }

    private fun getGenresData() {
        viewModelScope.launch {
            mGetGenresDataUseCase.getGenresData().collect {
                Log.d("getGenresData", it.genresModelList.size.toString())
                mGenresData.value = it
            }
        }
    }

    fun getMovieByGenresData(genres: Int) {
        viewModelScope.launch {
            model.genres = genres
            mGetMoviesByGenresDataUseCase.getMoviesByGenresData(model).collect {
                mMoviesByGenresData.value = it
            }
        }
    }

    fun selectMovie(model: MovieModel) {
        mSelectedMovie.value = model
        viewModelScope.launch {
            mActionSender.send(MovieActions.NavToMovieDetails)
        }
    }

    fun getCastData() {
        viewModelScope.launch {
            mCastUseCase.getCast(mSelectedMovie.value.id).collect {
                Log.d("getCastData", it.genresModelList.size.toString())
                mCastData.emit(it.castModels)
            }
        }
    }

    fun getMovieDetails() {
        viewModelScope.launch {
            mGetMovieDetailUseCase.getMovieDetail(mSelectedMovie.value.id).collect {
                Log.d("getMovieDetails", it.genresModelList?.size.toString())
                mMovieDetails.emit(it)
            }
        }
    }

    sealed class MovieActions {
        data object NavToMovieDetails : MovieActions()
    }
}