import Foundation
internal import Combine


@MainActor
class PostsViewModel: ObservableObject {
    
    @Published var posts: [PostItem] = []
    @Published var isLoading: Bool = false
    @Published var errorMsg: String? = nil
    
    private let repository: PostsRepo
    
    init(repository: PostsRepo) {
        self.repository = repository
    }
    
    // Loads posts from the repo
    func loadPosts() {
        Task {
            await fetchPosts()
        }
    }
    
    private func fetchPosts() async {

        isLoading = true
        
        defer {
            isLoading = false
        }
        
        do {
            let loaded = try await repository.getPosts()
            posts = loaded
            errorMsg = nil
        } catch {
            errorMsg = "Failed to load posts."
        }
    }
    
}
