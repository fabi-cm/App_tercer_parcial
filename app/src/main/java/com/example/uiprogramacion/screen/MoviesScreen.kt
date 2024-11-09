package com.example.uiprogramacion.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.domain.Movie
import com.example.uiprogramacion.ui.theme.DarkBlue
import com.example.uiprogramacion.viewmodel.InternetViewModel
import com.example.uiprogramacion.viewmodel.MovieViewModel

@Composable
fun MoviesScreen(onClick: (String) -> Unit, movieViewModel: MovieViewModel){
    Scaffold(
        content = {
            paddingValues -> MoviesScreenContent(
            modifier = Modifier.
            padding(paddingValues),
                onClick = onClick,
                movieViewModel = movieViewModel
            )
        }
    )
}

@Composable
fun MoviesScreenContent(modifier: Modifier, onClick: (String) -> Unit, movieViewModel: MovieViewModel) {
    Log.d("MoviesScreenContent", "MoviesScreenContent UI")
    var listOfMovies by remember { mutableStateOf(listOf<Movie>()) }
    val context = LocalContext.current

    val localLifecycleOwner = LocalLifecycleOwner.current
    val internetViewModel = InternetViewModel()
    var isThereInternet by remember { mutableStateOf(false) }

    fun updateUI(internetUIState: InternetViewModel.InternetUIState) {
        when ( internetUIState) {
            is InternetViewModel.InternetUIState.Loading -> {
                Toast.makeText(context, "Cargando", Toast.LENGTH_LONG).show()
            }
            is InternetViewModel.InternetUIState.Connection -> {
                if(internetUIState.status){
                    Toast.makeText(context, "Tiene acceso a internet", Toast.LENGTH_LONG).show()
                    movieViewModel.updateInternetStatus(true)
                }else{
                    Toast.makeText(context, "No tiene acceso a internet", Toast.LENGTH_LONG).show()
                    movieViewModel.updateInternetStatus(false)
                }
            }
            is InternetViewModel.InternetUIState.ErrorConnection -> {
                Toast.makeText(context, internetUIState.message, Toast.LENGTH_LONG).show()
            }
        }
    }
    internetViewModel.statusLiveData.observe(
        localLifecycleOwner,
        Observer(::updateUI)
    )

    internetViewModel.verify(context)
    val movieState by movieViewModel.state.collectAsStateWithLifecycle()

    when(movieState) {
        is MovieViewModel.MovieState.Loading -> {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }
        is MovieViewModel.MovieState.Error -> {
            Toast.makeText(context, "Error ${(movieState as MovieViewModel.MovieState.Error).errorMessage}", Toast.LENGTH_SHORT).show()
        }
        is MovieViewModel.MovieState.Successful -> {
            listOfMovies = (movieState as MovieViewModel.MovieState.Successful).list
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBlue)  // Fondo azul oscuro
            .padding(20.dp)
    ) {
        Text(
            text = "Peliculas Populares",
            color = Color.DarkGray, // Texto blanco para contraste
            style = MaterialTheme.typography.bodyLarge
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier
        ) {
            items(listOfMovies.size) {
                ElevatedCard(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                    onClick = {
                        onClick(listOfMovies[it].id.toString())
                    },
                ) {
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w185/${listOfMovies[it].posterPath}",
                        contentDescription = "MoviePoster",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .height(180.dp)
                            .width(160.dp)
                    )
                    Text(
                        text = "${listOfMovies[it].title}",
                        modifier = Modifier
                            .padding(16.dp),
                        color = Color.White,  // Texto en blanco para mayor contraste
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
