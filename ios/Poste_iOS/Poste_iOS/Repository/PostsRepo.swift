import Foundation


protocol PostsRepo {
    // Call backend here
    func getPosts() async throws -> [PostItem]
}

// Temporary mock repository, I tried to be identical to what we have in Kotlin
struct MockPostsRepository: PostsRepo {
    func getPosts() async throws -> [PostItem] {
        // Simulate small network delay
        try await Task.sleep(nanoseconds: 300_000_000)  // 0.3 seconds
        
        return [
            PostItem(id: "1",
                     title: "Welcome to Poste!",
                     notes: "This is your intro to the platform. Check out announcements!",
                    createdAt: "2025-10-11",
                     url:"https://www.petlandtexas.com/wp-content/uploads/2022/04/shutterstock_1290320698-1-scaled.jpg",
                     tags: [],
                     folderId: "Welcome"),
            
            PostItem(id: "2",
                     title: "How to Save a Post",
                     notes: "Tutorial on how to save posts.",
                     createdAt: "2025-10-12",
                     url: "www.google.com",
                     tags: [],
                     folderId: "Welcome"),
            
            PostItem(id: "3",
                     title: "Organizing with Folders",
                     notes: "Folders help us stay organized.",
                    createdAt: "2025-10-13",
                     url: "www.google.com",
                     tags: [],
                     folderId: "Welcome"),
            
            PostItem(id: "1",
                     title: "New Features Coming Soon",
                     notes: "Calendar integration coming soon!",
                    createdAt: "2025-10-14",
                     url: "www.google.com",
                     tags: [],
                     folderId: "Announcements"),
            
            PostItem(id: "1",
                     title: "Maintenance Scheduled",
                     notes: "Maintenance weekly on Tuesday evenings!",
                    createdAt: "2025-10-14",
                     url: "www.google.com",
                     tags: [],
                    folderId: "Announcements")
        ]
    }
}

// Potential for later when we have the backend integrated. This is more of a guess for the setup but I think this would be it.
/*
 struct ApiPostsRepo: PostsRepo {
 func getPosts() async throws -> [PostItem] {
 // Server call should go here and map DTO to PostItem
    }
 }
 */
