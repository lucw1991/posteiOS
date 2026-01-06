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
import model.Folder
import ui.activities.appBar_searchBar.AppBar



@Composable
fun FolderListScreen(onBack: () -> Unit,
                     onOpenFolder: (String) -> Unit,
                     vm: FolderListViewModel = viewModel()) {
    val uiState by vm.uiState.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadFolders()
    }

    Scaffold(topBar = {
                AppBar(title = "Folders",
                       onNavigateBack = onBack,
                       onContinue = {},          // Needed for app bar but not used here.
                       onRefresh = {
                           vm.refresh()
                       })
             }) { padding ->

        when (val state = uiState) {
            FolderListUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize()
                                       .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is FolderListUiState.Error -> {
                Column(modifier = Modifier.fillMaxSize()
                                          .padding(padding)
                                          .padding(16.dp),
                       verticalArrangement = Arrangement.Center,
                       horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = state.message,
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
                FolderListContent(modifier = Modifier.padding(padding),
                                  folders = state.folders,
                                  onOpenFolder = onOpenFolder,
                                  onCreateFolder = { title, description ->
                                      vm.createFolder(title, description)
                                  })
            }
        }
    }

}

@Composable
private fun FolderListContent(modifier: Modifier,
                              folders: List<Folder>,
                              onOpenFolder: (String) -> Unit,
                              onCreateFolder: (String, String?) -> Unit) {

    var showCreateDialog by remember {
        mutableStateOf(false)
    }

    Column(modifier = modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth()
                               .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text("Your Folders", style = MaterialTheme.typography.headlineSmall)

            Button(onClick = {
                showCreateDialog = true
            }) {
                Text("New Folder")
            }
        }

        if (folders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize()
                                   .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No folders yet.")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize(),
                       contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                       verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(folders, key = {
                                        it.id
                                       }) { folder ->
                    FolderCard(folder = folder,
                               onClick = {
                                   onOpenFolder(folder.id)
                               })
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateFolderDialog(onDismiss = {
                               showCreateDialog = false
                           },
                           onConfirm = {
                               title, description ->
                                   onCreateFolder(title, description)
                                   showCreateDialog = false
                           })
    }

}

@Composable
private fun FolderCard(folder: Folder,
                       onClick: () -> Unit) {
    Card(onClick = onClick,
         modifier = Modifier.fillMaxWidth(),
         shape = RoundedCornerShape(12.dp),
         colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
         elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
         border = CardDefaults.outlinedCardBorder()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(folder.title, style = MaterialTheme.typography.titleLarge)

            folder.description?.takeIf {
                it.isNotBlank()
            }?.let {
                Spacer(Modifier.height(6.dp))
                Text(text = it,
                     style = MaterialTheme.typography.bodyMedium,
                     maxLines = 2)
            }

            Spacer(Modifier.height(8.dp))

            val created = folder.createdAt.takeIf {
                it.isNotBlank()
            }

            if (created != null) {
                Text(text = "Created: ${created.split("T").first()}",
                     style = MaterialTheme.typography.labelSmall)
            }

            folder.visibility.let { visibility ->
                Spacer(Modifier.height(4.dp))
                Text(text = "Visibility: $visibility",
                     style = MaterialTheme.typography.labelSmall)
            }
        }
    }

}

@Composable
private fun CreateFolderDialog(onDismiss: () -> Unit,
                               onConfirm: (String, String?) -> Unit) {

    var title by remember {
        mutableStateOf("")
    }
    var description by remember {
        mutableStateOf("")
    }

    AlertDialog(onDismissRequest = onDismiss,
                title = { Text("New Folder") },
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

                       OutlinedTextField(value = description,
                                         onValueChange = {
                                             description = it
                                         },
                                         label = {
                                             Text("Description (optional)")
                                         },
                                         modifier = Modifier.fillMaxWidth(),
                                         minLines = 2)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                           onConfirm(title.trim(),
                                     description.trim().takeIf {
                                         it.isNotBlank()
                                     })
                },
                enabled = title.isNotBlank()) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        })

}
