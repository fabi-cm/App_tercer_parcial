package com.example.uiprogramacion.navigation

sealed class Screens(val route: String) {
    object MoviesScreen: Screens("movies")
    object MovieDetailScreen: Screens("movieDetail")
}