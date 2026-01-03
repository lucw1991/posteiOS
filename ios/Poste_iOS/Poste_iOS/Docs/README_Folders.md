## Folders directory contains everything Folders related.

    - Where the data is held:
        - FolderItem defines the in app folder model being used. FolderItem is identifiable so SwiftUI lists can render it and it includes visibility, createdAt, and optional theme.
        - Folder visibility is represented with FolderVisibility (public, private, or unlisted) and is used by the UI for badge display.
        - As it is now, folders are loaded from FoldersRepo via FoldersViewModel.
        
    - UI Flow
        - FoldersView
            - Ownes a FoldersViewModel (@StateObject) and call viewModel.load() on its first appearance.
            - Also loads post counts from a local cache by calling postsRepo.getPosts() and counting by post.folderId.
            - Each folder is rendered as a card and NavigationLink to FolderDetailView(folder:).
            
        - FoldersViewModel
            - Holds a list, loading, and error stat plus the delete dialog states: pendingDeleteFolder, pendingDeleteCount, showDeleteAlert.
            - Delete flow:
                - requestDelete(folder:). This precomputes how many posts are in that folder by fetching all the posts and filtering by folder.title.
                - confimDelete() calls foldersRepo.deleteFolder(folder.id) and removes it locally for now on success.
                
        - FolderDetailView
            - Displays a single folder and loads the posts for it by fetching all posts and filtering by folderId.
            - Delete calls foldersRepo.deleteFolder(folderId) then dismisses.
            - Edit updates the local state for now and calls foldersRepo.updateFolder(updated)
            - New Post opens a dialog but I just made it as close the Kotlin version as I could and did not make it function yet.
            
        - EditFolderView
            - Edits a folder and returns an updated FolderItem via onSave, preserving id and createdAt.
            
    - What we need to change for integration as I have it set up
        - I made the folder key "folder title". I have it set up using folder.title as both the mapping and the key for counting. I think this will definitely cause a break I just did not fix it for time's sake. As we go we will address this.
            - We will change our backend wiring to use more stable naming and use it with posts as well.
        - Replace the mock repos obviously. FoldersView and FolderDetail view default to the mock repos at the moment.
        - Implement the create folder button. Right now it is just a button that brings up a dialog saying we will implement later.
        - Query optimization. Rather than loading all posts and filtering locally, we will probably want to find a more efficient solution. This is just how I have it set up to function at my current knowlege level with Swift!
