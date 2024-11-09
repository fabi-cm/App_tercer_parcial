package com.example.data

import com.example.domain.Movie

interface ILocalDataSource {
    fun getList(): NetworkResult<List<Movie>>
    suspend fun deleteAll()
    fun insertMovies(lists: List<Movie>)
    fun findById(id: String): NetworkResult<Movie>
}