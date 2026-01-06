package ui.activities.folders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import model.Folder
import repository.MockRepository
import kotlin.random.Random



class FolderListViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<FolderListUiState>(FolderListUiState.Loading)
    val uiState: StateFlow<FolderListUiState> = _uiState.asStateFlow()

    // Wire backend repo here.
    private val repo = MockRepository

    // To remember the last selected folder. Could be useful at some point so I included it.
    private var activeFolderId: String? = null

    init {
        loadFolders()
    }


    fun loadFolders() {

        _uiState.value = FolderListUiState.Loading

        viewModelScope.launch {
            try {
                val folders = repo.getFolders()
                _uiState.value = FolderListUiState.Success(folders)
            } catch (e: Exception) {
                _uiState.value = FolderListUiState.Error(e.message ?: "Failed to load folders")
            }
        }

    }


    fun refresh() {
        loadFolders()
    }


    // Create in the Mock repo for now
    fun createFolder(title: String,
                     description: String?) {

        if (title.isBlank()) {
            return
        }

        viewModelScope.launch {

            try {
                val newFolder = Folder(id = generateLocalId(),
                                       title = title.trim(),
                                       description = description?.takeIf {
                                           it.isNotBlank()
                                       },
                                       visibility = "private",
                                       createdAt = "",
                                       theme = null)
                repo.addFolder(newFolder)
                loadFolders()
            } catch (e: Exception) {
                _uiState.value = FolderListUiState.Error(e.message ?: "Failed to create folder")
            }
        }

    }


    fun updateFolder(folder: Folder) {

        viewModelScope.launch {
            try {
                repo.updateFolder(folderId = folder.id,
                                  title = folder.title,
                                  description = folder.description,
                                  visibility = folder.visibility,
                                  theme = folder.theme)
                loadFolders()
            } catch (e: Exception) {
                _uiState.value = FolderListUiState.Error(e.message ?: "Failed to update folder")
            }
        }

    }


    fun deleteFolder(folder: Folder) {

        viewModelScope.launch {
            try {
                repo.deleteFolder(folder.id)
                loadFolders()
            } catch (e: Exception) {
                _uiState.value = FolderListUiState.Error(e.message ?: "Failed to delete folder")
            }
        }

    }


    fun setActiveFolder(folderId: String?) {
        activeFolderId = folderId
    }


    fun getActiveFolderId(): String? = activeFolderId


    // Backend will provide real IDs later, instead of these generated ones
    private fun generateLocalId(): String {
        return "local-folder-${Random.nextLong()}-${Random.nextLong()}"
    }

}