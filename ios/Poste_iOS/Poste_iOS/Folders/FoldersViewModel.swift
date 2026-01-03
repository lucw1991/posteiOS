import Foundation
internal import Combine


@MainActor
final class FoldersViewModel: ObservableObject {
    
    @Published var folders: [FolderItem] = []
    @Published var isLoading: Bool = false
    @Published var errorMsg: String? = nil
    
    
    // Delete dialog state
    @Published var pendingDeleteFolder: FolderItem? = nil
    @Published var pendingDeleteCount: Int = 0
    @Published var showDeleteAlert: Bool = false
    
    private let foldersRepo: FoldersRepo
    private let postsRepo: PostsRepo
    
    init(foldersRepo: FoldersRepo, postsRepo: PostsRepo) {
        self.foldersRepo = foldersRepo
        self.postsRepo = postsRepo
        
    }
    
    
    func load() {
        
        isLoading = true
        errorMsg = nil
        
        Task {
            do {
                let data = try await foldersRepo.getFolders()
                folders = data
            } catch {
                errorMsg = error.localizedDescription
            }
            isLoading = false
        }
    }
    
    
    func requestDelete(folder: FolderItem) {
        
        pendingDeleteFolder = folder
        pendingDeleteCount = 0
        
        Task {
            do {
                let posts = try await postsRepo.getPosts()
                pendingDeleteCount = posts.filter { $0.folderId == folder.title }.count
            } catch {
                pendingDeleteCount = 0
            }
            showDeleteAlert = true
        }
    }
    
    
    func confirmDelete() {
        
        guard let folder = pendingDeleteFolder else { return }
        
        Task {
            do {
                try await foldersRepo.deleteFolder(folder.id)
                folders.removeAll { $0.id == folder.id }
            } catch {
                errorMsg = error.localizedDescription
            }
            pendingDeleteFolder = nil
            pendingDeleteCount = 0
        }
    }
    
    
}
