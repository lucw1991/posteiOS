package ui.activities.folders

import model.Folder



sealed interface FolderListUiState {

    // Loading indicator while folders are being fetched
    data object Loading : FolderListUiState

    // Load success
    data class Success(val folders: List<Folder>) : FolderListUiState

    // Error state
    data class Error(val message: String) : FolderListUiState

}