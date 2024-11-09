package com.example.data

import com.example.domain.Movie

class MovieRepository(
    val localDataSource: ILocalDataSource, val remoteDataSource: IRemoteDataSource
) {
    suspend fun obtainMovies(isThereInternet: Boolean): List<Movie> {
        if (isThereInternet){
            val moviesRemote = remoteDataSource.fetchData()

            when (moviesRemote) {
                is NetworkResult.Success -> {
                    //eliminar datos de bd y actualizar
                    localDataSource.deleteAll()
                    localDataSource.insertMovies(moviesRemote.data)
                }
                is NetworkResult.Error -> {
                    //registrar log en Sentry
                }
            }
        }

        val moviesLocal = localDataSource.getList()
        when(moviesLocal) {
            is NetworkResult.Success -> {
                return moviesLocal.data
            }
            is NetworkResult.Error -> {
                return emptyList()
            }
        }
    }

    fun findById(id: String): Movie? {
        val movieLocal = localDataSource.findById(id)
        when(movieLocal) {
            is NetworkResult.Success -> {
                return movieLocal.data
            }
            is NetworkResult.Error -> {
                return null
            }
        }
    }
}