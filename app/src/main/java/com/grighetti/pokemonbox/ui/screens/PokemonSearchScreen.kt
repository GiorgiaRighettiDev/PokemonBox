package com.grighetti.pokemonbox.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.grighetti.pokemonbox.ui.*
import com.grighetti.pokemonbox.ui.theme.*
import com.grighetti.pokemonbox.viewmodel.PokemonViewModel

@Composable
fun PokemonSearchScreen(
    navController: NavController,
    viewModel: PokemonViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val pokemonListUiState by viewModel.pokemonListUiState.collectAsState()
    val listState = rememberLazyListState()

    // ✅ Carica la prima pagina di Pokémon
    LaunchedEffect(Unit) { viewModel.loadPokemonList() }

    // ✅ Carica più Pokémon quando l'utente scorre in basso
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                val totalItemsCount = pokemonListUiState.pokemonList.size
                if (lastVisibleItemIndex != null && lastVisibleItemIndex >= totalItemsCount - 5) {
                    viewModel.loadPokemonList()
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(PaddingLarge, PaddingLarge, PaddingLarge, 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PaddingLarge)
    ) {
        // **Title**
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) { append("Pokemon") }
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("Box") }
            },
            style = Typography.headlineMedium
        )

        // **Search Bar**
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onSearch = { navController.navigateToDetail(searchQuery) }
        )

        // **Pokémon List**
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(PaddingMedium)
        ) {
            items(pokemonListUiState.pokemonList, key = { it }) { pokemonName ->
                PokemonListItem(pokemonName, viewModel, navController)
            }

            // **Loading Indicator**
            item {
                if (pokemonListUiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = PaddingLarge),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(LoaderSize))
                    }
                }
            }
        }
    }
}


/**
 * Displays a single Pokémon list item with shimmer effects while loading.
 */
@Composable
fun PokemonListItem(name: String, viewModel: PokemonViewModel, navController: NavController) {
    val detailsCache by viewModel.pokemonDetailsCache.collectAsState()
    val pokemonDetail = detailsCache[name]

    LaunchedEffect(name) {
        if (pokemonDetail == null) {
            viewModel.searchPokemonDetail(name)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = { navController.navigateToDetail(name) }
            )
            .padding(vertical = PaddingMedium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PaddingLarge)
    ) {
        // **Pokémon Image**
        Box(
            modifier = Modifier
                .size(ImageSize)
                .clip(RoundedCornerShape(CornerRadius)),
            contentAlignment = Alignment.CenterStart
        ) {
            LoadingContent(
                isLoading = pokemonDetail == null,
                loading = { ShimmerBox(modifier = Modifier.fillMaxSize()) },
                content = {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(pokemonDetail?.imageUrl)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .build(),
                        contentDescription = "${pokemonDetail?.name} sprite",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(PaddingSmall)
        ) {
            // **Pokémon Name**
            LoadingContent(
                isLoading = pokemonDetail == null,
                loading = { ShimmerBox(modifier = Modifier.width(100.dp).height(20.dp)) },
                content = {
                    Text(
                        text = pokemonDetail?.name ?: "",
                        style = Typography.titleMedium
                    )
                }
            )

            // **Pokémon Types**
            LoadingContent(
                isLoading = pokemonDetail == null,
                loading = {
                    Row(horizontalArrangement = Arrangement.spacedBy(PaddingSmall)) {
                        repeat(2) {
                            ShimmerBox(
                                modifier = Modifier.width(40.dp).height(PaddingLarge)
                            )
                        }
                    }
                },
                content = {
                    Row(horizontalArrangement = Arrangement.spacedBy(PaddingSmall)) {
                        pokemonDetail!!.types.forEach { TypeBadge(it) }
                    }
                }
            )

            // **Pokédex Description**
            LoadingContent(
                isLoading = pokemonDetail == null,
                loading = {
                    Column(verticalArrangement = Arrangement.spacedBy(PaddingSmall)) {
                        repeat(2) {
                            ShimmerBox(modifier = Modifier.fillMaxWidth().height(14.dp))
                        }
                    }
                },
                content = {
                    Text(text = pokemonDetail!!.pokedexEntry, style = Typography.bodyMedium)
                }
            )
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(SearchBarHeight)
            .background(BackgroundColor, shape = RoundedCornerShape(CornerRadius))
            .padding(PaddingMedium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PaddingMedium)
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = HintColor
        )

        Box(modifier = Modifier.weight(1f)) {
            if (query.isEmpty()) {
                Text(
                    text = "Search name or number",
                    color = TextSecondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearch() }),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
