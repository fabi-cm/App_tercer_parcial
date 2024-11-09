package com.example.uiprogramacion.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MovieRepository
import com.example.domain.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel() {
    sealed class MovieState {
        object Loading : MovieState()
        class Error(val errorMessage: String? = null) : MovieState()
        class Successful(val list: List<Movie>) : MovieState()
    }

    val state : StateFlow<MovieState>
        get() = _state
    private val _state = MutableStateFlow<MovieState>(MovieState.Loading)

    private val _isThereInternet = MutableStateFlow(false)
    val isThereInternet: StateFlow<Boolean>
        get() = _isThereInternet

//    init {
//        fetchData()
//    }

    init {
        observeInternetStatus()
    }

    private fun observeInternetStatus() {
        viewModelScope.launch {
            _isThereInternet.collect { isInternet ->
                if (isInternet != null) {
                    fetchData()
                }
            }
        }
    }
    fun fetchData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val movies = movieRepository.obtainMovies(_isThereInternet.value)
                withContext(Dispatchers.Main) {
                    _state.value = MovieState.Successful(list = movies)
                }
            } catch (e: Exception) {
                // Handle error state
                Log.e("MOVIE", "Error fetching movies", e)
                withContext(Dispatchers.Main) {
                    _state.value = MovieState.Error( errorMessage = e.message)
                }
            }
        }
    }

    fun updateInternetStatus(isInternet: Boolean) {
        _isThereInternet.value = isInternet
    }

}