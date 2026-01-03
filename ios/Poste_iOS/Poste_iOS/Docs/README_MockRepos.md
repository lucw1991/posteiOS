## Mock repositories to fill out dummy data before integration

    - This folder has a couple files. PostsRepo and FoldersRepo.
    
    - PostsRepo:
        - PostsRepo is the contract for post fetching.
        - MockPostsRepository is the temporary repo to populate posts.
        
    - FoldersRepo:
        - FoldersRepo is the contract for folder CRUD -- list, get-by-id, add, update, delete
        - MockFoldersRepository is the temporary repo to populate folders.
        
    - UI and ViewModels request data through PostsRepo and FoldersRepo rather than calling networking directly and home uses these repos to preload all posts and folders once for local searching.
    
    - Places to integrate 
        - Real implementations will need to be added, like ApiPostsRepo and ApiFoldersRepo, to satisfy protocolas and call the real backend.
