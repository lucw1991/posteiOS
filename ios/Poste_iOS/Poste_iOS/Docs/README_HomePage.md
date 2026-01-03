## Home page view with previews at the bottom and search bar in the middle
    - I tried to structure this the same as the Kotlin one but I haven't quite figured out background animations or animations at all but I think it still looks better here. 
    
    - The only file here is HomeView
        - postOrFoilders is a toggle state that switches the search target between the posts or the folders.
        - searchText is a search state that drives the live filtering. 
        - The data cache for searching first loads all posts and folders once into allPosts and allFolders, then filters locally as you type.
        
        - Navigation:
            - From search results:
                - Posts -> 'PostsDetailView(post:)'
                - Folders -> 'FolderDetailView(folder:)'
            - From the preview buttons
                - Posts -> 'PostsView(viewModel: PostsViewModel(repostiory: MockPostsRepository()))'
                - Folders -> 'FoldersView()
                
        - Places to replace for the backend, I believe I set this up correct but should be easy to fix if not!
            - Replace MockPostsRepository and MockFoldersRepository with the actual repos.
            - We will decide how the preview tiles will populate, especially with the folders part. Right now it uses prevImages but we could just use the most liked or most popular post's photo to preview for a folder or for both posts and folders just pick whatever random images it can grab and use a new one each time the app is launched.
            
        - To Change:
            - The search behavior like what fields are used, the sorting, and the matching. This will be with folteredPosts and filteredFolders.
            - When and what gets loaded for the searches. Should be an edit to the .task {} block and with loadSearchData()
            - Where the buttons navigate. In NavigationLink we will need to change the destinations in the preview and the search results sections.
