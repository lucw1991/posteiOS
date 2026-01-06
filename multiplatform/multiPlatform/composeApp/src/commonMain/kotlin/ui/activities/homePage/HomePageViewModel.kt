package ui.activities.homePage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import model.Folder
import model.Post
import repository.MockRepository

enum class SearchTarget { POSTS, FOLDERS }

data class SearchState(
    val query: String = "",
    val target: SearchTarget = SearchTarget.POSTS,
    val loading: Boolean = false,
    val posts: List<Post> = emptyList(),
    val folders: List<Folder> = emptyList(),
    val error: String? = null
)

class HomePageViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<HomePageState>(HomePageState.Loading)
    val uiState: StateFlow<HomePageState> = _uiState.asStateFlow()

    private val _searchState = MutableStateFlow(SearchState())
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    init {
        refresh()
    }

    fun setSearchTarget(target: SearchTarget) {
        _searchState.value = _searchState.value.copy(target = target)
        performSearch(_searchState.value.query, target)
    }

    fun updateSearchQuery(query: String) {
        _searchState.value = _searchState.value.copy(query = query)
        performSearch(query, _searchState.value.target)
    }

    private fun performSearch(query: String, target: SearchTarget) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _searchState.value = _searchState.value.copy(
                    loading = false,
                    posts = emptyList(),
                    folders = emptyList(),
                    error = null
                )
                return@launch
            }

            _searchState.value = _searchState.value.copy(loading = true, error = null)

            try {
                val q = query.trim().lowercase()

                when (target) {
                    SearchTarget.POSTS -> {
                        val posts = MockRepository.getPosts().filter { p ->
                            p.title.lowercase().contains(q) ||
                                    (p.notes?.lowercase()?.contains(q) == true)
                        }

                        _searchState.value = _searchState.value.copy(
                            loading = false,
                            posts = posts,
                            folders = emptyList(),
                            error = null
                        )
                    }

                    SearchTarget.FOLDERS -> {
                        val folders = MockRepository.getFolders().filter { f ->
                            f.title.lowercase().contains(q) ||
                                    (f.description?.lowercase()?.contains(q) == true)
                        }

                        _searchState.value = _searchState.value.copy(
                            loading = false,
                            posts = emptyList(),
                            folders = folders,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _searchState.value = _searchState.value.copy(
                    loading = false,
                    error = e.message ?: "Search failed"
                )
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = HomePageState.Loading
            try {
                delay(2000)
                _uiState.value = HomePageState.Success()
            } catch (e: Exception) {
                _uiState.value = HomePageState.Error(e.message ?: "Error occurred.")
            }
        }
    }

    fun simulateError() {
        _uiState.value = HomePageState.Error("Something went wrong")
    }
}
