package ui.activities.posts

import ui.activities.posts.PostUiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import model.Post
import repository.MockRepository
import kotlin.random.Random



// When we integrate the backend, this is where we will swap the data source.
class PostViewModel(private val repo: MockRepository = MockRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<PostUiState>(PostUiState.Loading)
    val uiState: StateFlow<PostUiState> = _uiState.asStateFlow()

    // So refresh can keep current filter, like all posts vs folder specific
    private var activeFolderFilter: String? = null


    // Loading functions
    fun loadPosts() {

        activeFolderFilter = null

        viewModelScope.launch {

            try {
                val posts = repo.getPosts().sortedByDescending {
                    it.createdAt
                }
                _uiState.value = PostUiState.Success(posts)
            } catch (e: Exception) {
                _uiState.value = PostUiState.Error(e.message ?: " Failed to load posts.")
            }
        }

    }

    fun loadPostsByFolder(folderId: String) {

        activeFolderFilter = folderId
        viewModelScope.launch {

            try {
                val posts = repo.getPosts().filter {
                                                it.folderId == folderId
                                            }
                                 .sortedByDescending {
                                     it.createdAt
                                 }
                _uiState.value = PostUiState.Success(posts)
            } catch (e: Exception) {
                _uiState.value = PostUiState.Error(e.message ?: "Failed to load posts for folder.")
            }
        }
    }


    // Create, edit, and delete functions
    fun createPost(title: String,
                   url: String,
                   notes: String,
                   tags: List<String>,
                   folderId: String) {

        viewModelScope.launch {
            try {
                val newPost = Post(id = generateLocalId(),
                                   title = title.trim(),
                                   notes = notes.trim().takeIf { it.isNotEmpty() },
                                   tags = tags,
                                   folderId = folderId,
                                   url = url.trim(),
                                   // When we are not using the mock repo this will be filled on the back end too.
                                   createdAt = "")
                repo.addPost(newPost)
                refreshPosts()
            } catch (e: Exception) {
                _uiState.value = PostUiState.Error(e.message ?: "Failed to create post")
            }
        }

    }

    fun editPost(updatedPost: Post) {

        viewModelScope.launch {
            try {
                repo.updatePost(updatedPost)
                refreshPosts()
            } catch (e: Exception) {
                _uiState.value = PostUiState.Error(e.message ?: "Failed to update post.")
            }
        }

    }

    fun deletePost(post: Post) {

        viewModelScope.launch {
            try {
                repo.deletePost(post)
                refreshPosts()
            } catch (e: Exception) {
                _uiState.value = PostUiState.Error(e.message ?: "Failed to delete post.")
            }
        }

    }


    // The error simulation we had
    fun simulateError() {
        _uiState.value = PostUiState.Error("Simulated error!")
    }



    // Helpers
    private fun refreshPosts() {
        val folderId = activeFolderFilter
        if (folderId == null) {
            loadPosts()
        } else {
            loadPostsByFolder(folderId)
        }
    }

    private fun generateLocalId(): String {
        // Will come from backend in the future.
        return "local-${Random.nextLong()}-${Random.nextLong()}"
    }



}