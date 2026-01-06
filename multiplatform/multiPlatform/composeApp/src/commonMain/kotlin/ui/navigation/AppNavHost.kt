package ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import ui.activities.SplashScreen
import ui.activities.folders.FolderDetailScreen
import ui.activities.folders.FolderEditScreen
import ui.activities.folders.FolderListScreen
import ui.auth.SignInOrUpScreen
import ui.activities.homePage.HomePageRoute
import ui.activities.posts.PostInfoScreen
import ui.activities.posts.PostScreen

@Composable
fun AppNavHost() {
    val nav = rememberNavController()

    // Share the currently selected post/folder ID
    var selectedPostId by remember {
        mutableStateOf<String?>(null)
    }
    var selectedFolderId by remember {
        mutableStateOf<String?>(null)
    }

    NavHost(navController = nav,
            startDestination = Routes.SPLASH) {

        // Splash
        composable(Routes.SPLASH) {
            LaunchedEffect(Unit) {
                delay(2400)
                nav.navigate(Routes.AUTH) {
                    popUpTo(Routes.SPLASH) {
                        inclusive = true
                    }
                }
            }
            SplashScreen()
        }

        // Auth
        composable(Routes.AUTH) {
            SignInOrUpScreen(onAuthSuccess = {
                                nav.navigate(Routes.HOME) {
                                    popUpTo(Routes.AUTH) {
                                        inclusive = true
                                    }
                                }
                            },
                            onSkip = {
                                nav.navigate(Routes.HOME) {
                                    popUpTo(Routes.AUTH) {
                                        inclusive = true
                                    }
                                }
                            })
        }

        // Home
        composable(Routes.HOME) {
            HomePageRoute(onContinue = {
                              // Go to posts list
                              nav.navigate(Routes.POSTS)
                          },
                          onNavigateFolders = {
                              // Go to folders
                              nav.navigate(Routes.FOLDERS)
                          })
        }

        // Posts list
        composable(Routes.POSTS) {
            PostScreen(onBack = {
                           nav.popBackStack()
                       },
                       onOpenPost = { postId ->
                           // Store the selected post ID in shared state and then navigate to the detail screen
                           selectedPostId = postId
                           nav.navigate(Routes.POST_DETAIL)
                       })
        }

        // Post detail
        composable(Routes.POST_DETAIL) {
            val postId = selectedPostId.orEmpty()

            PostInfoScreen(postId = postId,
                           onNavigateBack = { nav.popBackStack() })
        }

        // Folders list
        composable(Routes.FOLDERS) {
            FolderListScreen(onBack = {
                                nav.popBackStack()
                            },
                            onOpenFolder = { folderId ->
                                // Store selected folder ID and navigate to detail
                                selectedFolderId = folderId
                                nav.navigate(Routes.FOLDER_DETAIL)
                            })
        }

        // Folder detail
        composable(Routes.FOLDER_DETAIL) {
            val folderId = selectedFolderId.orEmpty()

            FolderDetailScreen(folderId = folderId,
                               onBack = { nav.popBackStack() },
                               onEditFolder = { id ->
                                   // Update selected folder and go to edit screen
                                   selectedFolderId = id
                                   nav.navigate(Routes.FOLDER_EDIT)
                               },
                               onOpenPost = { postId ->
                                   selectedPostId = postId
                                   nav.navigate(Routes.POST_DETAIL)
                               })
        }

        // Folder edit
        composable(Routes.FOLDER_EDIT) {
            val folderId = selectedFolderId.orEmpty()

            FolderEditScreen(folderId = folderId,
                             onBack = {
                                 nav.popBackStack()
                             })
        }

    }
}
