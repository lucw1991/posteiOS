package ui.activities.posts

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import model.Post
import ui.activities.appBar_searchBar.AppBar



@Composable
fun PostScreen(onBack: () -> Unit,
               onOpenPost: (String) -> Unit,
               folderId: String? = null,
               folderName: String? = null,
               viewModel: PostViewModel = viewModel()) {

    val uiState by viewModel.uiState.collectAsState()

    // Load the correct data set when entering
    LaunchedEffect(folderId) {
        if (folderId.isNullOrBlank()) {
            viewModel.loadPosts()
        } else {
            viewModel.loadPostsByFolder(folderId)
        }
    }

    val screenTitle = if (!folderName.isNullOrBlank()) {
                          "Posts from $folderName"
                      } else {
                          "Posts"
                      }

    Scaffold(topBar = {
                AppBar(title = screenTitle,
                       onNavigateBack = onBack,
                       onContinue = {},  // Needed for AppBar but not used here!
                       onRefresh = null)
             }) { padding ->

        when (val state = uiState) {
            PostUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is PostUiState.Error -> {
                Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                       verticalArrangement = Arrangement.Center,
                       horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = state.message,
                         color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = {
                               if (folderId.isNullOrBlank()) {
                                   viewModel.loadPosts()
                               } else {
                                   viewModel.loadPostsByFolder(folderId)
                               }
                           }) {
                        Text("Retry")
                    }
                }
            }

            is PostUiState.Success -> {
                PostListContent(modifier = Modifier.padding(padding),
                                posts = state.posts,
                                folderIdForCreate = folderId,
                                onOpenPost = onOpenPost,
                                onCreatePost = {
                                    title, url, notes, tags, selectedFolderId ->
                                        viewModel.createPost(
                                            title = title,
                                            url = url,
                                            notes = notes,
                                            tags = tags,
                                            folderId = selectedFolderId)
                                })
            }
        }
    }

}


@Composable
private fun PostListContent(modifier: Modifier,
                            posts: List<Post>,
                            folderIdForCreate: String?,
                            onOpenPost: (String) -> Unit,
                            onCreatePost: (String, String, String, List<String>, String) -> Unit) {

    var showCreateDialog by remember {
        mutableStateOf(false)
    }

    Column(modifier = modifier.fillMaxSize()) {

        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text("All Posts", style = MaterialTheme.typography.headlineSmall)

            Button(onClick = {
                showCreateDialog = true
            }) {
                Text("New Post")
            }
        }

        if (posts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp),
                contentAlignment = Alignment.Center) {
                Text("No posts yet.")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize(),
                       contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                       verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(posts, key = {
                                         it.id
                                     }) { post ->
                                            PostCard(post = post,
                                                     onClick = {
                                                         onOpenPost(post.id)
                                                     })
                                        }
            }
        }
    }

    if (showCreateDialog) {
        CreatePostDialog(defaultFolderId = folderIdForCreate,
                         onDismiss = {
                             showCreateDialog = false
                         },
                         onConfirm = {
                             title, url, notes, tags, folderId ->
                                onCreatePost(title, url, notes, tags, folderId)
                                showCreateDialog = false
                         })
    }

}


@Composable
private fun PostCard(post: Post,
                     onClick: () -> Unit) {

    Card(onClick = onClick,
         modifier = Modifier.fillMaxWidth(),
         shape = RoundedCornerShape(12.dp),
         colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
         elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
         border = CardDefaults.outlinedCardBorder()) {

        Column(modifier = Modifier.padding(16.dp)) {
            Text(post.title, style = MaterialTheme.typography.titleLarge)

            post.notes?.takeIf {
                it.isNotBlank()
            } ?.let {
                Spacer(Modifier.height(6.dp))
                Text(it, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
            }

            Spacer(Modifier.height(8.dp))

            val created = post.createdAt?.takeIf {
                it.isNotBlank()
            }

            if (created != null) {
                Text(text = "Created: ${created.split("T").first()}",
                     style = MaterialTheme.typography.labelSmall)
            }

            if (post.tags.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(text ="Tags: ${post.tags.joinToString(", ")}",
                     style = MaterialTheme.typography.labelSmall)
            }
        }
    }

}


@Composable
private fun CreatePostDialog(defaultFolderId: String?,
                             onDismiss: () -> Unit,
                             onConfirm: (String, String, String, List<String>, String) -> Unit) {

    var title by remember {
        mutableStateOf("")
    }
    var url by remember {
        mutableStateOf("")
    }
    var notes by remember {
        mutableStateOf("")
    }
    var tagsText by remember {
        mutableStateOf("")
    }
    var folderId by remember {
        mutableStateOf(defaultFolderId ?: "")
    }



    AlertDialog(onDismissRequest = onDismiss,
                title = {
                    Text("New Post")
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                           verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                                          modifier = Modifier.fillMaxWidth())

                        OutlinedTextField(value = tagsText,
                                          onValueChange = {
                                              tagsText = it
                                          },
                                          label = {
                                              Text("Tags (comma-separated)")
                                          },
                                          modifier = Modifier.fillMaxWidth())

                        OutlinedTextField(value = folderId,
                                          onValueChange = {
                                              folderId = it
                                          },
                                          label = {
                                              Text("Folder ID")
                                          },
                                          modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(enabled = title.isNotBlank() &&
                                        url.isNotBlank() &&
                                        notes.isNotBlank() &&
                                        folderId.isNotBlank(),
                       onClick = {
                           val tags = tagsText.split(",")
                                              .map {
                                                  it.trim()
                                              }
                                              .filter {
                                                  it.isNotEmpty()
                                              }

                       onConfirm(title.trim(),
                                 url.trim(),
                                 notes.trim(),
                                 tags,
                                 folderId.trim())
                      }) {
                            Text("Create")
                         }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        })

}