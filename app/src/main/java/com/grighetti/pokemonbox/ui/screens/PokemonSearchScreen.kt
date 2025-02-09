package com.grighetti.pokemonbox.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.grighetti.pokemonbox.navigateToDetail
import com.grighetti.pokemonbox.ui.TypeBadge
import com.grighetti.pokemonbox.ui.shimmerEffect
import com.grighetti.pokemonbox.ui.theme.Typography
import com.grighetti.pokemonbox.viewmodel.PokemonViewModel

@Composable
fun PokemonSearchScreen(
    navController: NavController,
    viewModel: PokemonViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val pokemonList by viewModel.pokemonList.collectAsState()
    val listState = rememberLazyListState()

    // Load initial Pokémon list
    LaunchedEffect(Unit) {
        viewModel.loadPokemonList()
    }

    // Load more Pokémon when scrolling to the bottom
    LaunchedEffect(listState.firstVisibleItemIndex, listState.layoutInfo.totalItemsCount) {
        if (listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == pokemonList.lastIndex) {
            viewModel.loadPokemonList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, 16.dp, 16.dp, 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // App title
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) { append("Pokemon") }
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("Box") }
            },
            style = Typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search bar
        Row(modifier = Modifier.fillMaxWidth()) {
            BasicTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    navController.navigateToDetail(searchQuery)
                }),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .background(Color(0xFFeeeeee), shape = RoundedCornerShape(10.dp))
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                decorationBox = { innerTextField ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xffc3c3c3)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box {
                            if (searchQuery.isEmpty()) Text(
                                "Search name or number",
                                color = Color(0xffc3c3c3),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pokémon List
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(pokemonList.size) { index ->
                PokemonListItem(pokemonList[index], viewModel, navController)
            }
            // Loading indicator at the end of the list
            item {
                if (viewModel.isLoading) CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun PokemonListItem(name: String, viewModel: PokemonViewModel, navController: NavController) {
    val detailsCache by viewModel.pokemonDetailsCache.collectAsState()
    val pokemonDetail = detailsCache[name]

    LaunchedEffect(name) {
        if (pokemonDetail == null) {
            viewModel.loadPokemonDetail(name)
        }
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigateToDetail(name) }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pokémon image with shimmer effect
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF8F8F8)), contentAlignment = Alignment.CenterStart
            ) {
                if (pokemonDetail == null) Box(modifier = Modifier
                    .size(64.dp)
                    .shimmerEffect())
                else AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(pokemonDetail.imageUrl)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .build(),
                    contentDescription = "${pokemonDetail.name} sprite",
                    modifier = Modifier.size(64.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                // Pokémon Name
                if (pokemonDetail == null) Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(20.dp)
                        .shimmerEffect()
                )
                else Text(
                    text = pokemonDetail.name.replaceFirstChar { it.uppercase() },
                    style = Typography.titleMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Pokémon Types
                if (pokemonDetail == null) Row {
                    repeat(2) {
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(16.dp)
                                .shimmerEffect()
                        ); Spacer(modifier = Modifier.width(4.dp))
                    }
                }
                else Row {
                    pokemonDetail.types.forEach {
                        TypeBadge(it); Spacer(
                        modifier = Modifier.width(
                            4.dp
                        )
                    )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Pokédex Description
                if (pokemonDetail == null) Column {
                    repeat(2) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(14.dp)
                                .shimmerEffect()
                        ); Spacer(modifier = Modifier.height(4.dp))
                    }
                }
                else Text(text = pokemonDetail.pokedexEntry, style = Typography.bodyMedium)
            }
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFFE0E0E0)))
    }
}
