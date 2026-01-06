package ui.activities.folders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import repository.MockRepository
import ui.activities.appBar_searchBar.AppBar



@Composable
fun FolderEditScreen(folderId: String,
                     onBack: () -> Unit,
                     vm: FolderListViewModel = viewModel()) {

    // Wire in our actual backend repo here
    val repo = MockRepository

    // Load the folder once for this screen
    val currentFolder by produceState<Folder?>(initialValue = null,
                                               key1 = folderId) {
        value = try {
            repo.getFolderById(folderId)
        } catch (_: Exception) {
            null
        }
    }

    var folderName by remember {
        mutableStateOf("")
    }
    var description by remember {
        mutableStateOf("")
    }

    // Local state when loaded.
    LaunchedEffect(currentFolder) {
        currentFolder?.let { folder ->
            folderName = folder.title
            description = folder.description ?: ""
        }
    }

    Scaffold(topBar = {
                AppBar(title = "Edit Folder",
                       onNavigateBack = onBack,
                       onContinue = {  },  // Needed for app bar but not used here. Same with onRefresh
                       onRefresh = {  })
        }) { padding ->

            if (currentFolder == null) {
                // Show a simple loading state or "not found" message
                Box(modifier = Modifier.fillMaxSize()
                                       .padding(padding),
                    contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
                }
            } else {
                FolderEditContent(modifier = Modifier.fillMaxSize()
                                       .padding(padding)
                                       .padding(16.dp),
                                  folder = currentFolder!!,
                                  folderName = folderName,
                                  onFolderNameChange = { folderName = it },
                                  description = description,
                                  onDescriptionChange = { description = it },
                                  onSave = {
                                      val base = currentFolder ?: return@FolderEditContent

                                      val updated = base.copy(title = folderName.trim(),
                                                              description = description.trim()
                                                                                       .takeIf {
                                                                                           it.isNotBlank()
                                                                                       })

                                      vm.updateFolder(updated)
                                      onBack()
                                  })
            }
        }

}

@Composable
private fun FolderEditContent(modifier: Modifier,
                              folder: Folder,
                              folderName: String,
                              onFolderNameChange: (String) -> Unit,
                              description: String,
                              onDescriptionChange: (String) -> Unit,
                              onSave: () -> Unit) {
    Column(modifier = modifier,
           verticalArrangement = Arrangement.spacedBy(16.dp)) {
               // Folder icon and title card
               Card(modifier = Modifier.fillMaxWidth(),
                   colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                   Column(modifier = Modifier.fillMaxWidth()
                                             .padding(16.dp),
                   horizontalAlignment = Alignment.CenterHorizontally,
                   verticalArrangement = Arrangement.spacedBy(12.dp)) {
                       Icon(imageVector = Icons.Rounded.Folder,
                            contentDescription = "Folder",
                            modifier = Modifier.height(72.dp),
                            tint = MaterialTheme.colorScheme.primary)

                       Text(text = "Editing folder",
                            style = MaterialTheme.typography.titleMedium)
                   }
               }

               OutlinedTextField(value = folderName,
                                 onValueChange = onFolderNameChange,
                                 label = {
                                     Text("Folder name")
                                 },
                                 modifier = Modifier.fillMaxWidth(),
                                 singleLine = true)

               OutlinedTextField(value = description,
                                 onValueChange = onDescriptionChange,
                                 label = {
                                     Text("Description (optional)")
                                 },
                                 modifier = Modifier.fillMaxWidth(),
                                 minLines = 3,
                                 maxLines = 5)

               // Read-only metadata for now
               Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                   val created = folder.createdAt.takeIf {
                       it.isNotBlank()
                   }
                   if (created != null) {
                       Text(text = "Created: ${created.split('T').first()}",
                            style = MaterialTheme.typography.labelSmall)
                   }

                   Text(text = "Visibility: ${folder.visibility}",
                        style = MaterialTheme.typography.labelSmall)
                   folder.theme?.let { themeValue ->

                       Text(text = "Theme: $themeValue",
                            style = MaterialTheme.typography.labelSmall)

                   }
               }

               Spacer(Modifier.height(8.dp))

               Button(onClick = onSave,
                      modifier = Modifier.fillMaxWidth(),
                      enabled = folderName.isNotBlank()) {
                    Text("Save changes")
               }
           }

}
