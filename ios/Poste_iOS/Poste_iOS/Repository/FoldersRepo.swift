import Foundation


protocol FoldersRepo {

    func getFolders() async throws -> [FolderItem]
    func getFoldersById(_ id: UUID) async throws -> FolderItem?
    func deleteFolder(_ id: UUID) async throws
    func updateFolder(_ folder: FolderItem) async throws
    func addFolder(_ folder: FolderItem) async throws
    
}


// Just temporary. I tried to keep it close to what we have in Android.
actor MockFoldersRepository: FoldersRepo {
    
    private var folders: [FolderItem] = []
    
    init() {
        
        folders = [
            FolderItem(
                id: UUID(uuidString: "550E8400-E29B-41D4-A716-446655440001")!,
                title: "Welcome",
                description: "Getting started with ePoste",
                visibility: .public,
                createdAt: "2025-01-15",
                theme: nil
            ),
            FolderItem(
                id: UUID(uuidString: "550E8400-E29B-41D4-A716-446655440002")!,
                title: "Announcements",
                description: "Important updates and news",
                visibility: .private,
                createdAt: "2025-01-14",
                theme: nil
            ),
            FolderItem(
                id: UUID(uuidString: "550E8400-E29B-41D4-A716-446655440003")!,
                title: "Help & Support",
                description: "Get help with common issues",
                visibility: .public,
                createdAt: "2025-01-10",
                theme: nil
            ),
            FolderItem(
                id: UUID(uuidString: "550E8400-E29B-41D4-A716-446655440004")!,
                title: "Feature Requests",
                description: nil,
                visibility: .unlisted,
                createdAt: "2025-01-08",
                theme: nil
            )
        ]
    }
    
    
    func getFolders() async throws -> [FolderItem] {
        try await Task.sleep(nanoseconds: 300_000_000)
        return folders
    }
    
    
    func getFoldersById(_ id: UUID) async throws -> FolderItem? {
        try await Task.sleep(nanoseconds: 300_000_000)
        return folders.first(where: { $0.id == id })
    }
    
    
    func deleteFolder(_ id: UUID) async throws {
        folders.removeAll(where: { $0.id == id })
    }
    
    
    func updateFolder(_ folder: FolderItem) async throws {
        try await Task.sleep(nanoseconds: 500_000_000)
        guard let idx = folders.firstIndex(where: { $0.id == folder.id }) else {
            throw NSError(domain: "FoldersRepo", code: 404, userInfo: [NSLocalizedDescriptionKey: "Folder not found."])
        }
        folders[idx] = folder
    }
    
    
    func addFolder(_ folder: FolderItem) async throws {
        folders.append(folder)
    }
}
