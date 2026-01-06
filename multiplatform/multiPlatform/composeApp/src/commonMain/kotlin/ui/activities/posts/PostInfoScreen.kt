package ui.activities.posts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import model.Post
import ui.activities.appBar_searchBar.AppBar
import ui.activities.folders.FolderListUiState
import ui.activities.folders.FolderListViewModel


@Composable
fun PostInfoScreen(postId: String,
                   onNavigateBack: () -> Unit,
                   viewModel: PostViewModel = viewModel()) {

    // Makes sure we have data.
    LaunchedEffect(postId) {
        viewModel.loadPosts()
    }

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(topBar = {
        AppBar(title = "Post",
               onNavigateBack = onNavigateBack,
               onContinue = {},  // Needed for app bar but we aren't using it here!
               onRefresh = {
                   viewModel.loadPosts()
               })
    }) { padding ->
        Column(modifier = Modifier.padding(padding)
                                  .padding(16.dp)
                                  .verticalScroll(rememberScrollState()),
               verticalArrangement = Arrangement.spacedBy(12.dp)) {

            when (val state = uiState) {
                is PostUiState.Loading -> {
                    Text("Loading...", style = MaterialTheme.typography.bodyLarge)
                }

                is PostUiState.Error -> {
                    Text(text = state.message,
                         color = MaterialTheme.colorScheme.error,
                         style = MaterialTheme.typography.bodyLarge)
                }

                is PostUiState.Success -> {
                    val post = state.posts.firstOrNull {
                        it.id == postId
                    }
                    if (post == null) {
                        Text(text = "Post not found.",
                             style = MaterialTheme.typography.bodyLarge)
                        return@Column
                    }

                    PostInfoContent(post = post,
                                    onUpdate = { updated ->
                                        viewModel.editPost(updated)
                                    },
                                    onDelete = {
                                        viewModel.deletePost(post)
                                        onNavigateBack()
                                    })
                }

            }
        }
    }

}



@Composable
private fun PostInfoContent(post: Post,
                            onUpdate: (Post) -> Unit,
                            onDelete: () -> Unit) {

    var showEditDialog by remember {
        mutableStateOf(false)
    }
    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    Text(text = post.title ,
         style = MaterialTheme.typography.headlineSmall)

    Text(text = "URL",
         style = MaterialTheme.typography.titleSmall)

    Text(text = post.url,
         style = MaterialTheme.typography.bodyMedium)

    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

    Text(text = "Notes",
         style = MaterialTheme.typography.titleSmall)

    Text(text = post.notes?.takeIf { it.isNotBlank() } ?: "No notes.",
         style = MaterialTheme.typography.bodyMedium)

    Text(text = "Folder Name and ID",
         style = MaterialTheme.typography.titleSmall)

    Text(text = post.title,
         style = MaterialTheme.typography.bodyMedium)

    Text(text = post.folderId,
        style = MaterialTheme.typography.bodySmall)

    Text(text = "Created",
         style = MaterialTheme.typography.titleSmall)

    Text(text = post.createdAt,
         style = MaterialTheme.typography.bodyMedium)

    if (post.tags.isNotEmpty()) {
        Text(text = "Tags",
             style = MaterialTheme.typography.titleSmall)
        TagRow(tags = post.tags)
    }

    Spacer(Modifier.height(8.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

        Button(onClick = {
                   showEditDialog = true
               },
               modifier = Modifier.weight(1f)) {
                   Text("Edit")
               }

        Button(onClick = {
                   showDeleteDialog = true
               },
               modifier = Modifier.weight(1f)) {
                   Text("Delete")
        }

    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },
            title = {
                Text("Delete Post")
            },
            text = {
                Text("Are you sure you want to delete this post? This cannot be undone.")
            },
            confirmButton = {
                TextButton(onClick = {
                               showDeleteDialog = false
                               onDelete()
                           }
                ) {
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

    if (showEditDialog) {
        EditPostDialog(post = post,
                       onDismiss = {
                           showEditDialog = false
                       },
                       onConfirm = { updated ->
                           onUpdate(updated)
                           showEditDialog = false
                       })
    }

}



/*
For folders I made this its own file but I did not think the edit posts needed to be separated.
Whenever we refactor I figure this will get moved if we feel like it makes the file too long!
*/
@Composable
private fun EditPostDialog(post: Post,
                           onDismiss: () -> Unit,
                           onConfirm: (Post) -> Unit) {

    var title by remember {
        mutableStateOf(post.title)
    }
    var url by remember {
        mutableStateOf(post.url)
    }
    var notes by remember {
        mutableStateOf(post.notes ?: "")
    }
    var selectedFolderId by remember {
        mutableStateOf(post.folderId)
    }
    var folderId by remember {
        mutableStateOf(post.folderId)
    }

    // For simple tag editing
    var tagsCsv by remember {
        mutableStateOf(post.tags.joinToString(", "))
    }

    // Load folders from view model
    val foldersViewModel: FolderListViewModel = viewModel()
    val folderState by foldersViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        foldersViewModel.loadFolders()
    }


    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Edit Post")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = title,
                                  onValueChange = {
                                      title = it
                                  },
                                  label = {
                                      Text("Title")
                                  },
                                  modifier = Modifier.fillMaxWidth())

                OutlinedTextField(value = url,
                                  onValueChange = {
                                      url = it
                                  },
                                  label = {
                                      Text("URL")
                                  },
                                  modifier = Modifier.fillMaxWidth())

                OutlinedTextField(value = notes,
                                  onValueChange = {
                                      notes = it
                                  },
                                  label = {
                                      Text("Notes")
                                  },
                                  modifier = Modifier.fillMaxWidth(),
                                  minLines = 3)

                when (folderState) {

                    is FolderListUiState.Success -> {
                        val folders = (folderState as FolderListUiState.Success).folders
                        val selectedFolder = folders.firstOrNull {
                            it.id == selectedFolderId
                        }

                        var menuExpanded by remember {
                            mutableStateOf(false)
                        }

                        Column {
                            OutlinedTextField(value = selectedFolder?.title ?: "Select folder",
                                              onValueChange = {},
                                              readOnly = true,
                                              label = {
                                                  Text("Folder")
                                              },
                                              modifier = Modifier.fillMaxWidth()
                                                                 .clickable {
                                                                     // Open drop down menu
                                                                     menuExpanded = true
                                                                 })

                            DropdownMenu(expanded = menuExpanded,
                                         onDismissRequest = { menuExpanded = false }) {
                                             folders.forEach { folder ->
                                                 DropdownMenuItem(text = {
                                                     Text(folder.title)
                                                 },
                                                 onClick = {
                                                     selectedFolderId = folder.id
                                                     menuExpanded = false
                                                 })
                                             }
                                         }
                        }
                    }

                    else -> Text("Loading folders…")
                }

                OutlinedTextField(value = tagsCsv,
                                  onValueChange = {
                                      tagsCsv = it
                                  },
                                  label = {
                                      Text("Tags (comma-separated)")
                                  },
                                  modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(onClick = {
                           val tags = tagsCsv.split(",")
                                             .map {
                                                 it.trim()
                                             }
                                             .filter {
                                                 it.isNotBlank()
                                             }

                           onConfirm(
                               post.copy(title = title.trim(),
                                         url = url.trim(),
                                         notes = notes,
                                         folderId = selectedFolderId,
                                         tags = tags))
                      },
                      enabled = title.isNotBlank() && url.isNotBlank() && folderId.isNotBlank()) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        })

}



@Composable
private fun TagRow(tags: List<String>) {
    if (tags.isEmpty()) return

    Row(
        modifier = Modifier.fillMaxWidth()
                           .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        tags.forEach { tag ->
            Card(colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Text(text = tag,
                     modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                     style = MaterialTheme.typography.labelMedium,
                     color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
    }

}