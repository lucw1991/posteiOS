package ui.activities.homePage

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import model.Folder
import model.Post
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import network.PosteAPIClient
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.painterResource
import multiplatform.composeapp.generated.resources.Res
import multiplatform.composeapp.generated.resources.happy1
import multiplatform.composeapp.generated.resources.happy2
import multiplatform.composeapp.generated.resources.happy3
import multiplatform.composeapp.generated.resources.happy4
import multiplatform.composeapp.generated.resources.happy5




@Composable
fun HomePreviewSection(onOpenPosts: () -> Unit,
                       onOpenFolders: () -> Unit) {

    val api = remember {
        PosteAPIClient.getInstance()
    }
    val repo = remember {
        repository.MockRepository
    }

    val posts by produceState(initialValue = emptyList<Post>()) {

        val fromApi = try {
            val page = api.postService.listPosts()
            page.data.map { dto ->
                Post(id = dto.id,
                     title = dto.title,
                     notes = dto.notes,
                     tags = dto.tags,
                     folderId = dto.folderId,
                     url = dto.url,
                     createdAt = dto.createdAt)
            }.take(10)
        } catch (_: Exception) {
            emptyList()
        }

        value = fromApi.ifEmpty {
            repo.getPosts()
        }

    }

    val folders by produceState(initialValue = emptyList<Folder>()) {

        val fromApi = try {
            val page = api.folderService.listMyFolders()
            page.data.map { dto ->
                Folder(id = dto.id,
                       title = dto.title,
                       description = dto.description,
                       createdAt = dto.createdAt)
            }.take(10)
        } catch (_: Exception) {
            emptyList()
        }

        value = fromApi.ifEmpty {
            repo.getFolders()
        }

    }

    Column(modifier = Modifier
                      .fillMaxWidth()
                      .padding(8.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)) {

                PreviewTile(title = "Posts",
                            body = {
                                if (posts.isEmpty()) {
                                    ExamplePostsNoData(onOpenPosts)
                                } else {
                                    PostsPreviewRow(posts = posts,
                                                    onPostClick = {
                                                        onOpenPosts()
                                                    })
                                }
                            },
                            onClick = onOpenPosts)

                PreviewTile(title = "Folders",
                            body = {
                                if (folders.isEmpty()) {
                                    ExampleFoldersNoData(onOpenFolders)
                                } else {
                                    FoldersPreviewRow(folders = folders,
                                                      onFolderClick = {
                                                          onOpenFolders()
                                                    })
                                }
                            },
                            onClick = onOpenFolders)
          }

}


@Composable
private fun PreviewTile(title: String,
                        body: @Composable () -> Unit,
                        onClick: () -> Unit) {

    Card(modifier = Modifier.fillMaxWidth(),
         shape = MaterialTheme.shapes.large,
         colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {

        Column(modifier = Modifier.padding(12.dp),
               verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title,
                 style = MaterialTheme.typography.titleMedium)
            // Makes the body an interactive preview area
            Box(Modifier.fillMaxWidth()
                        .clickable(onClick = onClick)) {
                body()
            }
        }
    }

}


// Preview rows
@Composable
fun PostsPreviewRow(posts: List<Post>,
                    onPostClick: (Post) -> Unit) {

    AutoScrollRow(items = posts,
                  speed = 10.dp,
                  reverse = true,  // Right to left. False is left to right.
                  contentPadding = PaddingValues(horizontal = 8.dp)) { post ->
        val index = posts.indexOf(post)

        Surface(tonalElevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.width(150.dp)
                                   .height(120.dp)) {
            Column(modifier = Modifier.clickable {
                                  onPostClick(post)
                              }.padding(8.dp),
                   verticalArrangement = Arrangement.spacedBy(6.dp)) {

                // Image placeholder
                Image(painter = previewImageForIndex(index),
                      contentDescription = "Post preview",
                      modifier = Modifier.fillMaxWidth()
                                         .height(70.dp))

                // Title under the image
                Text(post.title,
                     style = MaterialTheme.typography.bodyMedium,
                     maxLines = 1)
            }
        }
    }

}

@Composable
fun FoldersPreviewRow(folders: List<Folder>,
                      onFolderClick: (Folder) -> Unit) {

    AutoScrollRow(items = folders,
                  speed = 10.dp,
                  reverse = false,
                  contentPadding = PaddingValues(horizontal = 8.dp),
                  itemSpacing = 8.dp) { folder ->

        val index = folders.indexOf(folder)

        Surface(tonalElevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.width(150.dp)
                                   .height(120.dp)) {
            Column(modifier = Modifier.clickable {
                                  onFolderClick(folder)
                              }.padding(8.dp),
                   verticalArrangement = Arrangement.spacedBy(6.dp)) {

                // Image placeholder
                Image(painter = previewImageForIndex(index),
                      contentDescription = "Folder preview",
                      modifier = Modifier.fillMaxWidth()
                                         .height(70.dp),)

                // Folder title under the image
                Text(folder.title,
                     style = MaterialTheme.typography.bodyMedium,
                     maxLines = 1)
            }
        }
    }

}


// No data place holder ui
@Composable
fun ExamplePostsNoData(onOpenPosts: () -> Unit) {
    Text("No posts yet. Tap to open Posts.")
}

@Composable
fun ExampleFoldersNoData(onOpenFolders: () -> Unit) {
    Text("No folders yet. Tap to open Folders.")
}


// Helper to choose an image by index
@Composable
private fun previewImageForIndex(index: Int): Painter {

    return when (index % 5) {
        0 -> painterResource(Res.drawable.happy1)
        1 -> painterResource(Res.drawable.happy2)
        2 -> painterResource(Res.drawable.happy3)
        3 -> painterResource(Res.drawable.happy4)
        else -> painterResource(Res.drawable.happy5)
    }

}
