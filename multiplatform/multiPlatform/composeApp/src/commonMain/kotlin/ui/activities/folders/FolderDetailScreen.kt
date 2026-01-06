package ui.activities.folders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import model.Folder
import model.Post
import repository.MockRepository
import ui.activities.appBar_searchBar.AppBar



@Composable
fun FolderDetailScreen(folderId: String,
                       onBack: () -> Unit,
                       onEditFolder: (String) -> Unit,
                       onOpenPost: (String) -> Unit,
                       vm: FolderListViewModel = viewModel()) {

    val uiState by vm.uiState.collectAsState()
    val repo = MockRepository

    // Load the folder by ID
    val currentFolder by produceState<Folder?>(initialValue = null,
                                               key1 = folderId) {
        value = try {
            repo.getFolderById(folderId)
        } catch (_: Exception) {
            null
        }
    }

    // Load posts that belong to this folder
    val postsInFolder by produceState(initialValue = emptyList<Post>(),
                                      key1 = folderId) {
        value = try {
            repo.getPosts().filter {
                it.folderId == folderId
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        vm.loadFolders()
    }

    Scaffold(
        topBar = {
            AppBar(title = "Folder Details",
                   onNavigateBack = onBack,
                   onContinue = { },
                   onRefresh = {
                       vm.refresh()
                   })
        }) { padding ->

        when (uiState) {
            FolderListUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize()
                                       .padding(padding),
                    contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is FolderListUiState.Error -> {
                Column(modifier = Modifier.fillMaxSize()
                                          .padding(padding)
                                          .padding(16.dp),
                       verticalArrangement = Arrangement.Center,
                       horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = (uiState as FolderListUiState.Error).message,
                         color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = {
                        vm.refresh()
                    }) {
                        Text("Retry")
                    }
                }
            }

            is FolderListUiState.Success -> {
                FolderDetailContent(modifier = Modifier.padding(padding),
                                    folder = currentFolder,
                                    posts = postsInFolder,
                                    onBack = onBack,
                                    onEdit = { currentFolderId ->
                                        onEditFolder(currentFolderId)
                                    },
                                    onDelete = {
                                        currentFolder?.let { folder ->
                                        showDeleteDialog = true
                                        }
                                    },
                                    onOpenPost = onOpenPost)
            }
        }

    }

    // Delete confirmation dialog
    if (showDeleteDialog && currentFolder != null) {
        AlertDialog(onDismissRequest = {
                        showDeleteDialog = false
                    },
                    title = {
                        Text("Delete Folder?")
                    },
                    text = {
                        Text(
                           "Are you sure you want to delete \"${currentFolder!!.title}\"? This action cannot be undone.")
                    },
                    confirmButton = {
                        TextButton(onClick = {
                                vm.deleteFolder(currentFolder!!)
                                showDeleteDialog = false
                                onBack()
                                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                }) {
                    Text("Cancel")
                }
            })
    }

}


@Composable
private fun FolderDetailContent(modifier: Modifier,
                                folder: Folder?,
                                posts: List<Post>,
                                onBack: () -> Unit,
                                onEdit: (String) -> Unit,
                                onDelete: () -> Unit,
                                onOpenPost: (String) -> Unit) {
    if (folder == null) {
        Box(
            modifier = modifier.fillMaxSize()
                               .padding(16.dp),
            contentAlignment = Alignment.Center) {
            Text("Folder not found.")
        }
        return
    }

    Column(modifier = modifier.fillMaxSize()
                              .padding(16.dp),
           verticalArrangement = Arrangement.spacedBy(16.dp)) {

        Card(modifier = Modifier.fillMaxWidth(),
             shape = RoundedCornerShape(12.dp),
             colors = CardDefaults.cardColors(
                 containerColor = MaterialTheme.colorScheme.surface),
             elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
            Column(modifier = Modifier.padding(16.dp),
                   verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(folder.title, style = MaterialTheme.typography.headlineSmall)

                folder.description?.takeIf {
                    it.isNotBlank()
                }?.let {
                    Text(text = it,
                         style = MaterialTheme.typography.bodyMedium)
                }

                val created = folder.createdAt.takeIf {
                    it.isNotBlank()
                }

                if (created != null) {
                    Text(text = "Created: ${created.split('T').first()}",
                         style = MaterialTheme.typography.labelSmall)
                }

                folder.visibility.let { visibility ->
                    Text(text = "Visibility: $visibility",
                         style = MaterialTheme.typography.labelSmall)
                }
            }

        }

        // Edit and Delete
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { onEdit(folder.id) },
                   modifier = Modifier.weight(1f)) {
                Text("Edit")
            }

            Button(onClick = onDelete,
                   modifier = Modifier.weight(1f),
                   colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                Text("Delete")
            }
        }

        // Posts in this folder
        Text(text = "Posts in this folder",
             style = MaterialTheme.typography.titleMedium)

        if (posts.isEmpty()) {
            Text(text = "No posts in this folder yet.",
                 style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize(),
                       contentPadding = PaddingValues(vertical = 8.dp),
                       verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(posts, key = { it.id }) { post ->
                    PostRow(post = post,
                            onClick = {
                                onOpenPost(post.id)
                            })
                }
            }
        }
    }

}



@Composable
private fun PostRow(post: Post, onClick: () -> Unit) {
    Card(onClick = onClick,
         modifier = Modifier.fillMaxWidth(),
         shape = RoundedCornerShape(10.dp),
         colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(12.dp),
               verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(post.title, style = MaterialTheme.typography.titleMedium)
            post.notes?.takeIf {
                it.isNotBlank()
            }?.let {
                Text(text = it,
                     style = MaterialTheme.typography.bodySmall,
                     maxLines = 2)
            }
        }
    }

}
