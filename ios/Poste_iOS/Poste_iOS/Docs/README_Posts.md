## The Posts folder contains everything posts related. 

    - Where data is held:
        - PostItem defines the in app model being used in the UI. It is Identifiable so SwiftUI lists render it. 
        - As it is now, post data is from our mock repo within PostsViewModel.
        
    - UI Flow
        - PostsView
            - Creates and owns a PostsViewModel as a @StateObject.
            - By default it uses PostsViewModel(repository: MockPostsRepository()) with mock data for now
            - Call viewModel.loadPosts() on its first appearance.
            - Renders the list and navigates to PostDetailView(post:)
            
        - PostsViewModel
            - Holds view state: posts, isLoading, errorMsg
            - Fetches posts via repository.getPosts()
            
        - PostDetailView
            - Show details for a single PostItem and keeps a local editable copy, currentPost, for UI updates.
            - Edit currently only updates the local state and our delete is just a placeholder at the moment.
            
        - NewPostSheet
            - UI only create-post sheet. Disabled for now until backend and social media integration is more understood.
            
    - What we need to change for integration as I have it set up
        - Replace our MockRepository usage. 
            - MockPostsRepository and MockFoldersRepository are instantiated inside PostsView and FoldersView
        - Add write operations.
            - We will need to implement create, update, delete, functions in the repo layer and call them from NewPostSheet (Create), EditPostSheet (Update), PostDetailView (Delete).
            - Right now all of these are local functioning.
        - Align the IDs and timestamps with the server
            - EditPostSheet currently constructs an updated post with a hardcoded id that I put in there just to make it work. The real backend will preserve the unique id's.
