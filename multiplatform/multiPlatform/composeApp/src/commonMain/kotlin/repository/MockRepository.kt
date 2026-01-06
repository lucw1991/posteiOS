package repository

import model.Folder
import model.Post
import kotlinx.coroutines.delay




object MockRepository {

    // Mock data
    private val _folders = mutableListOf<Folder>()
    private val _posts = mutableListOf<Post>()

    // Mock Folders
    private val mockFolders = listOf(
        Folder(
            id = "550E8400-e29b-41d4-A716-446655440001",
            title = "Welcome",
            description = "Getting started with ePoste",
            visibility = "public",
            createdAt = "2025-01-15",
            theme = null
        ),
        Folder(
            id = "550E8400-e29b-41d4-A716-446655440002",
            title = "Announcements",
            description = "Important updates and news",
            visibility = "private",
            createdAt = "2025-01-14",
            theme = null
        ),
        Folder(
            id = "550E8400-e29b-41d4-A716-446655440003",
            title = "Help & Support",
            description = "Get help with common issues",
            visibility = "public",
            createdAt = "2025-01-10",
            theme = null
        ),
        Folder(
            id = "550E8400-e29b-41d4-A716-446655440004",
            title = "Feature Requests",
            description = null,
            visibility = "unlisted",
            createdAt = "2025-01-08",
            theme = null
        )
    )

    // Mock posts
    private val mockPosts = mutableListOf(
        Post(
            id = "251d171d-f436-45cb-b52f-70d8eb073f30",
            folderId = "550E8400-e29b-41d4-A716-446655440001",
            title = "Welcome to ePoste!",
            url = "https://www.petlandtexas.com/wp-content/uploads/2022/04/shutterstock_1290320698-1-scaled.jpg",
            notes = "This is your intro to the platform. Check out announcements!",
            tags = listOf("welcome", "intro"),
            createdAt = "2025-10-11"
        ),
        Post(
            id = "251d171d-f436-45cb-b52f-70d8eb073f31",
            folderId = "550E8400-e29b-41d4-A716-446655440001",
            title = "How to Save a Post",
            url = "https://example.com/how-to-post",
            notes = "Tutorial on how to save posts.",
            tags = listOf("tutorial", "guide"),
            createdAt = "2025-10-12"
        ),
        Post(
            id = "251d171d-f436-45cb-b52f-70d8eb073f32",
            folderId = "550E8400-e29b-41d4-A716-446655440001",
            title = "Organizing with Folders",
            url = "https://example.com/organizing",
            notes = "Folders help us stay organized",
            tags = listOf("organization", "tips"),
            createdAt = "2025-10-13"
        ),
        Post(
            id = "251d171d-f436-45cb-b52f-70d8eb073f33",
            folderId = "550E8400-e29b-41d4-A716-446655440002",
            title = "New Features Coming Soon",
            url = "https://example.com/new-features",
            notes = "calendar integration coming soon!",
            tags = listOf("announcement", "features"),
            createdAt = "2025-10-13"
        ),
        Post(
            id = "251d171d-f436-45cb-b52f-70d8eb073f34",
            folderId = "550E8400-e29b-41d4-A716-446655440002",
            title = "Maintenance Scheduled",
            url = "https://example.com/maintenance",
            notes = "Maintenance weekly on Tuesday evenings!",
            tags = listOf("maintenance", "important"),
            createdAt = "2025-10-13"
        )
    )



    // Load list of folders at startup
    fun getFolders(): List<Folder> {
        if (_folders.isEmpty()) {
            _folders.addAll(mockFolders)
        }
        return _folders.toList()
    }

    // Get folder by UUID
    suspend fun getFolderById(folderId: String): Folder? {
        delay(300)
        if (_folders.isEmpty()) {
            _folders.addAll(mockFolders)
        }
        return _folders.find {
            it.id == folderId
        }
    }

    // Add folder to temp list
    fun addFolder(folder: Folder) {
        _folders.add(folder)
    }

    // Delete folder from temp list
    fun deleteFolder(folderId: String) {
        _folders.removeAll {
            it.id == folderId
        }
    }

    // Update an existing folder
    suspend fun updateFolder(folderId: String,
                             title: String,
                             description: String?,
                             visibility: String,
                             theme: String?) {

        delay(500)
        val index = _folders.indexOfFirst {
            it.id == folderId
        }

        if (index != -1) {
            val existingFolder = _folders[index]
            _folders[index] = existingFolder.copy(title = title,
                                                  description = description,
                                                  visibility = visibility,
                                                  theme = theme)
        } else {
            throw Exception("Folder not found.")
        }
    }


    // Post count helper
    fun getPostCountForFolder(folderId: String): Int {
        return _posts.count {
            it.folderId == folderId
        }
    }

    fun getPosts(): List<Post> {
        return _posts.toList()
    }

    fun addPost(post: Post) {
        _posts.add(post)
    }

    fun deletePost(post: Post) {
        _posts.remove(post)
    }

    fun updatePost(post: Post) {
        val index = _posts.indexOfFirst {
            it.id == post.id
        }

        if (index != -1) {
            _posts[index] = post
        } else {
            throw IllegalArgumentException("Post not found.")
        }
    }


    // Initialize mock repo with the mock data
    init {
        reset()
    }

    fun reset() {
        _posts.clear()
        _posts.addAll(mockPosts)
    }

}