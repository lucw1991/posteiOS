import SwiftUI


struct VisibilityPill: View {
    
    let visibility: FolderVisibility
    
    var body: some View {
        Text(visibility.label)
            .font(.caption2)
            .fontWeight(.semibold)
            .padding(.horizontal, 10)
            .padding(.vertical, 6)
            .background(backgrnd)
            .foregroundColor(.white)
            .clipShape(Capsule())
    }
    
    private var backgrnd: Color {
        switch visibility {
        case .public:
            return Color.blue.opacity(0.85)
        case .private:
            return Color.gray.opacity(0.85)
        case .unlisted:
            return Color.black.opacity(0.65)
        }
    }
}


private struct FolderCardView: View {
    
    let folder: FolderItem
    let postCountText: String
    let onDelete: () -> Void
    
    var body: some View {
        HStack(alignment: .top, spacing: 12) {
            VStack(alignment: .leading, spacing: 6) {
                
                Text(folder.title)
                    .font(.title3)
                    .fontWeight(.semibold)
                
                Text(folder.safeDescription)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                
                Text("\(postCountText) - Created \(folder.createdAt)")
                    .font(.caption)
                    .foregroundColor(.secondary)
                    .padding(.top, 2)
            
            }
            
            Spacer()
            
            VStack(alignment: .trailing, spacing: 10) {
                VisibilityPill(visibility: folder.visibility)
                
                Button(action: onDelete) {
                    Image(systemName: "trash.fill")
                        .foregroundColor(.red)
                }
                .buttonStyle(.plain)
                
            }
        }
        .padding(16)
        .background(RoundedRectangle(cornerRadius: 16).fill(Color(.systemGray6)))
        
    }
}


struct FoldersView: View {
    
    @StateObject private var viewModel: FoldersViewModel
    @State private var showAddFolderNoOp = false
    
    private let postsRepo: PostsRepo
    
    // Local cache for counts
    @State private var postCountByFolderId: [String: Int] = [:]
    
    init(
        foldersRepo: FoldersRepo = MockFoldersRepository(),
        postsRepo: PostsRepo = MockPostsRepository()
    ) {
        self.postsRepo = postsRepo
        _viewModel = StateObject(
            wrappedValue: FoldersViewModel(foldersRepo: foldersRepo, postsRepo: postsRepo)
        )
    }
    
    var body: some View {
        NavigationStack {
            ZStack(alignment: .bottomTrailing) {
                
                content
                    .navigationTitle("Folders")
                    .navigationBarTitleDisplayMode(.inline)
                    .task {
                        viewModel.load()
                        await loadPostCounts()
                    }
                    .alert(
                        "Delete Folder?",
                        isPresented: $viewModel.showDeleteAlert,
                        presenting: viewModel.pendingDeleteFolder
                    ) { _ in
                        Button("Cancel", role: .cancel) {}
                        Button("Delete", role: .destructive) {
                            viewModel.confirmDelete()
                            Task { await loadPostCounts() }
                        }
                    } message: { folder in
                        Text(
                            """
                            Are you sure you want to delete "\(folder.title)"?
                            
                            This folder contains \(viewModel.pendingDeleteCount) posts. This cannot be undone.
                            """
                        )
                    }
                
                // Add folder button. No function to it yet.
                Button {
                    // No operation for now. Fix here. Variable for debugging.
                    showAddFolderNoOp = true
                } label: {
                    Image(systemName: "folder.badge.plus")
                        .font(.title3)
                        .padding(18)
                        .background(Circle().fill(Color.blue.opacity(0.85)))
                        .foregroundColor(.white)
                        .shadow(radius: 8)
                    
                }
                .padding(.trailing, 18)
                .padding(.bottom, 18)
                .alert("Add Folder", isPresented: $showAddFolderNoOp) {
                    Button("OK", role: .cancel) { }
                } message: {
                    Text("To be implemented later!")
                }
            }
        }
    }
    
    
    @ViewBuilder
    private var content: some View {
        if viewModel.isLoading {
            VStack { Spacer(); ProgressView(); Spacer() }
        } else if let error = viewModel.errorMsg {
            VStack { Spacer(); Text(error).foregroundColor(.red); Spacer() }
        } else {
            ScrollView {
                VStack(spacing: 12) {
                    ForEach(viewModel.folders) { folder in
                        NavigationLink {
                            FolderDetailView(folder: folder)
                        } label: {
                            let count = postCountByFolderId[folder.title, default: 0]
                            
                            FolderCardView(
                                folder: folder,
                                postCountText: "\(count) posts",
                                onDelete: { viewModel.requestDelete(folder: folder) }
                                
                            )
                            .padding(.horizontal, 16)
                            
                        }
                        .buttonStyle(.plain)
                        
                    }
                }
                .padding(.top, 12)
                .padding(.bottom, 90)
                
            }
        }
    }
    
    
    @MainActor
    private func loadPostCounts() async {
        do {
            let posts = try await postsRepo.getPosts()
            
            var counts: [String: Int] = [:]
            for post in posts {
                counts[post.folderId, default: 0] += 1
            }
            
            postCountByFolderId = counts
        } catch {
            // Keep whatever counts we had. Folders can still render. I am not sure how to fill this out or if I need to?
        }
    }
    
}
