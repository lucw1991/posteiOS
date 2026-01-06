package ui.activities.posts

import model.Post



sealed interface PostUiState {

    // Loading indicator
    data object Loading : PostUiState

    // Posts list
    data class Success(val posts: List<Post>) : PostUiState

    // Error state message
    data class Error(val message: String) : PostUiState

}