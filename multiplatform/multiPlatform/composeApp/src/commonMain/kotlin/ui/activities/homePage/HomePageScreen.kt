package ui.activities.homePage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.resources.painterResource

import ui.activities.appBar_searchBar.AppBar
import ui.activities.appBar_searchBar.SearchControls
import ui.activities.homePage.HomePreviewSection
import ui.animations.AnimatedGradientBackground


import multiplatform.composeapp.generated.resources.Res
import multiplatform.composeapp.generated.resources.eposte

@Composable
fun HomePageRoute(
    viewModel: HomePageViewModel = viewModel(),
    onContinue: () -> Unit,
    onNavigateBack: (() -> Unit)? = null,
    onNavigateFolders: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val searchState by viewModel.searchState.collectAsState()

    HomePageScreen(
        state = state,
        searchState = searchState,
        onSearchQueryChange = viewModel::updateSearchQuery,
        onTargetChange = viewModel::setSearchTarget,
        onRefresh = { viewModel.refresh() },
        onNavigateBack = onNavigateBack,
        onContinue = onContinue,
        onNavigateFolders = onNavigateFolders
    )
}

@Composable
fun HomePageScreen(
    state: HomePageState,
    searchState: SearchState,
    onSearchQueryChange: (String) -> Unit,
    onTargetChange: (SearchTarget) -> Unit,
    onRefresh: () -> Unit,
    onContinue: () -> Unit,
    onNavigateBack: (() -> Unit)? = null,
    onNavigateFolders: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            AppBar(
                title = "HomePage",
                onNavigateBack = onNavigateBack,
                onContinue = onContinue,
                onRefresh = onRefresh
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedGradientBackground()

            when (state) {
                is HomePageState.Loading -> CircularProgressIndicator()

                is HomePageState.Error -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = onRefresh) { Text("Retry") }
                }

                is HomePageState.Success -> Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Spacer(Modifier.height(8.dp))

                    Image(
                        painter = painterResource(Res.drawable.eposte),
                        contentDescription = "ePoste Logo",
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .align(Alignment.CenterHorizontally)
                            .alpha(0.6f),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(Modifier.weight(1f))

                    SearchControls(
                        searchState = searchState,
                        onQueryChange = onSearchQueryChange,
                        onTargetChange = onTargetChange
                    )

                    Spacer(Modifier.weight(1f))

                    HomePreviewSection(
                        onOpenPosts = onContinue,
                        onOpenFolders = onNavigateFolders
                    )

                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}
