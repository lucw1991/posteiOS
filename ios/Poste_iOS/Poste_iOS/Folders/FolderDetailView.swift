import SwiftUI
internal import Combine


@MainActor
final class FolderDetailViewModel: ObservableObject {
    
    @Published var posts: [PostItem] = []
    @Published var isLoading = false
    @Published var errorMsg: String? = nil
    
    private let postsRepo: PostsRepo
    private let folderId: String
    
    init(postsRepo: PostsRepo, folderId: String) {
        self.postsRepo = postsRepo
        self.folderId = folderId
    }
    
    
    func load() {
        isLoading = true
        errorMsg = nil
        
        Task {
            do {
                let all = try await postsRepo.getPosts()
                posts = all.filter { $0.folderId == folderId }
            } catch {
                errorMsg = error.localizedDescription
            }
            isLoading = false
            
        }
    }
}


struct FolderDetailView: View {
    
    @Environment(\.dismiss) private var dismiss
    
    @State private var folder: FolderItem
    
    private let foldersRepo: FoldersRepo
    private let postsRepo: PostsRepo
    
    @StateObject private var vm: FolderDetailViewModel
    
    @State private var showDeleteAlert = false
    @State private var showNewPostSheet = false
    @State private var showEditScreen = false
    
    init(
        folder: FolderItem,
        folderRepo: FoldersRepo = MockFoldersRepository(),
        postsRepo: PostsRepo = MockPostsRepository()
    ) {
        _folder = State(initialValue: folder)
        self.foldersRepo = folderRepo
        self.postsRepo = postsRepo
        _vm = StateObject(wrappedValue: FolderDetailViewModel(postsRepo: postsRepo, folderId: folder.title))
    }
    
    
    var body: some View {
        VStack(alignment: .leading, spacing: 14) {
            
            headerCard
            
            // Actions row for edit, share, and delete
            HStack(spacing: 10) {
                Button {
                    showEditScreen = true
                } label: {
                    Label("Edit", systemImage: "pencil")
                        .frame(maxWidth: .infinity)
                }
                .buttonStyle(.bordered)
                
                
                Button {
                    // No function for now. Currently researching social media sharing!
                } label: {
                    Label("Share", systemImage: "square.and.arrow.up")
                        .frame(maxWidth: .infinity)
                }
                .buttonStyle(.bordered)
                
                
                Button(role: .destructive) {
                    showDeleteAlert = true
                } label: {
                    Label("Delete", systemImage: "trash")
                        .frame(maxWidth: .infinity)
                }
                .buttonStyle(.bordered)
                
            }
            .padding(.horizontal, 16)
            
            
            // Posts header and New Post button
            HStack {
                Text("Posts in this folder")
                    .font(.title2)
                    .fontWeight(.semibold)
                
                Spacer()
                
                Button {
                    showNewPostSheet = true
                } label: {
                    HStack(spacing: 8) {
                        Image(systemName: "plus.circle")
                        Text("New\nPost")
                            .multilineTextAlignment(.leading)
                    }
                    .font(.subheadline)
                    .padding(.horizontal, 16)
                    .padding(.vertical, 10)
                    .background(Capsule().fill(Color.blue.opacity(0.9)))
                    .foregroundColor(.white)
                }
            }
            .padding(.horizontal, 16)
            .padding(.top, 6)
            
            
            // Posts List
            Group {
                if vm.isLoading{
                    VStack {
                        Spacer(); ProgressView(); Spacer()
                    }
                } else if let error = vm.errorMsg {
                    VStack { Spacer(); Text(error).foregroundColor(.red); Spacer() }
                } else {
                    ScrollView {
                        VStack(spacing: 12) {
                            ForEach(vm.posts) { post in
                                NavigationLink(destination: PostDetailView(post: post)) {
                                    PostCardView(post: post)
                                        .padding(.horizontal, 16)
                                }
                                .buttonStyle(.plain)
                                
                            }
                        }
                        .padding(.top, 8)
                        .padding(.bottom, 16)
                        
                    }
                }
            }
            
            Spacer(minLength: 0)
            
        }
        .navigationTitle(folder.title)
        .navigationBarTitleDisplayMode(.inline)
        .task { vm.load() }
        .alert("Delete folder?", isPresented: $showDeleteAlert) {
            Button("Cancel", role: .cancel) { }
            Button("Delete", role: .destructive) {
                Task {
                    try? await foldersRepo.deleteFolder(folder.id)
                    dismiss()
                }
            }
        } message: {
            Text("Are you sure you want to delete \"\(folder.title)\"? This cannot be undone.")
        }
        .sheet(isPresented: $showNewPostSheet) {
            // Create button disabled for now, so this is only UI.
            NewPostSheet().applyNewPostSheetPresentation()
        }
        .navigationDestination(isPresented: $showEditScreen) {
            EditFolderView(folder: folder) { updated in
                folder = updated
                Task { try? await foldersRepo.updateFolder(updated) }
            }
        }
    }
    
    private var headerCard: some View {
        HStack(alignment: .top) {
            VStack(alignment: .leading, spacing: 6) {
                Text(folder.title)
                    .font(.title2)
                    .fontWeight(.semibold)
                
                Text(folder.safeDescription)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
            }
            
            Spacer()
            
            VisibilityPill(visibility: folder.visibility)
        }
        .padding(16)
        .background(RoundedRectangle(cornerRadius: 16).fill(Color(.systemGray6)))
        .padding(.horizontal, 16)
        .padding(.top, 10)
        
    }
}
