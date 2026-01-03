import SwiftUI

struct HomeView: View {

    // Toggle the state between posts and folders. Posts = true, Folders = false
    @State private var postOrFolders: Bool = true

    // Text binding for live searching
    @State private var searchText: String = ""

    // Cached data for searching, loaded once from mock repos
    @State private var allPosts: [PostItem] = []
    @State private var allFolders: [FolderItem] = []
    @State private var searchDataLoaded = false

    private let postsRepo: PostsRepo = MockPostsRepository()
    private let foldersRepo: FoldersRepo = MockFoldersRepository()

    // Placeholder preview images
    let prevImages = [
        "happy1", "happy2", "happy3", "happy4", "happy5"
    ]

    // Helpers for search state and filtering
    private var trimmedQuery: String {
        searchText.trimmingCharacters(in: .whitespacesAndNewlines)
    }

    private var isSearching: Bool {
        !trimmedQuery.isEmpty
    }

    private var filteredPosts: [PostItem] {
        let q = trimmedQuery.lowercased()
        guard !q.isEmpty else { return [] }
        return allPosts
            .filter { $0.title.lowercased().contains(q) }
            .sorted { $0.title.localizedCaseInsensitiveCompare($1.title) == .orderedAscending }
    }

    private var filteredFolders: [FolderItem] {
        let q = trimmedQuery.lowercased()
        guard !q.isEmpty else { return [] }
        return allFolders
            .filter { $0.title.lowercased().contains(q) }
            .sorted { $0.title.localizedCaseInsensitiveCompare($1.title) == .orderedAscending }
    }

    var body: some View {

        VStack(spacing: 0) {

            // Logo at top
            Image("Logo_full")
                .resizable()
                .scaledToFit()
                .frame(maxWidth: .infinity)
                .padding(.horizontal, 16)
                .padding(.top, 12)

            // Search bar pushed towards the middle
            Spacer(minLength: 80)

            // Search and toggle
            searchAndToggle

            // Show search results when query is not empty, otherwise show previews
            if isSearching {
                searchResultsSection
                    .padding(.top, 12)
                Spacer(minLength: 0)
            } else {
                Spacer()

                // Preview Section
                VStack(spacing: 32) {
                    // Tappable buttons above tiles
                    postsPreviewSection(images: prevImages)
                    foldersPreviewSection(images: prevImages)
                }
                .padding(.bottom, 18)
            }
        }
        .navigationTitle("HomePage")
        // Load posts/folders once so search can filter right away
        .task {
            guard !searchDataLoaded else { return }
            searchDataLoaded = true
            await loadSearchData()
        }
    }

    
    
    // UI

    // Extracted search and toggle UI
    private var searchAndToggle: some View {
        VStack(spacing: 14) {

            // TextField and clear button
            HStack {
                Image(systemName: "magnifyingglass")

                TextField(postOrFolders ? "Search posts" : "Search folders", text: $searchText)
                    .textInputAutocapitalization(.never)
                    .disableAutocorrection(true)

                if !searchText.isEmpty {
                    Button {
                        searchText = ""
                    } label: {
                        Image(systemName: "xmark.circle.fill")
                            .foregroundColor(.gray.opacity(0.8))
                    }
                    .buttonStyle(.plain)
                }

                Spacer(minLength: 0)
            }
            .padding()
            .background(RoundedRectangle(cornerRadius: 16).fill(Color(.systemGray6)))
            .padding(.horizontal, 26)

            HStack(spacing: 12) {
                Button(action: {
                    postOrFolders = true
                }) {
                    Text("Posts")
                        .padding(.horizontal, 20)
                        .padding(.vertical, 8)
                        .background(postOrFolders ? Color.blue.opacity(0.25) : Color.white)
                        .cornerRadius(10)
                }

                Button(action: {
                    postOrFolders = false
                }) {
                    Text("Folders")
                        .padding(.horizontal, 20)
                        .padding(.vertical, 8)
                        .background(!postOrFolders ? Color.blue.opacity(0.25) : Color.white)
                        .cornerRadius(10)
                }
            }
        }
    }

    // Search results UI depending on toggle
    private var searchResultsSection: some View {
        VStack(alignment: .leading, spacing: 10) {

            Text(postOrFolders ? "Post Results" : "Folder Results")
                .font(.headline)
                .padding(.horizontal, 26)

            let resultsCount = postOrFolders ? filteredPosts.count : filteredFolders.count

            VStack(spacing: 10) {
                if resultsCount == 0 {
                    Text("No matches found.")
                        .foregroundColor(.secondary)
                        .frame(maxWidth: .infinity, alignment: .center)
                        .padding(.vertical, 18)
                } else {
                    ScrollView {
                        VStack(spacing: 10) {
                            if postOrFolders {
                                ForEach(filteredPosts) { post in
                                    NavigationLink(destination: PostDetailView(post: post)) {
                                        searchPostRow(post)
                                    }
                                    .buttonStyle(.plain)
                                }
                            } else {
                                ForEach(filteredFolders) { folder in
                                    NavigationLink(destination: FolderDetailView(folder: folder)) {
                                        searchFolderRow(folder)
                                    }
                                    .buttonStyle(.plain)
                                }
                            }
                        }
                        .padding(.vertical, 8)
                    }
                    .frame(maxHeight: 260)
                }
            }
            .padding(.horizontal, 26)
        }
    }

    
    
    
    // Search Row Views

    private func searchPostRow(_ post: PostItem) -> some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(post.title)
                .font(.headline)
                .foregroundColor(.primary)

            Text(post.notes)
                .font(.subheadline)
                .foregroundColor(.secondary)
                .lineLimit(1)

            Text("Created \(post.createdAt)")
                .font(.caption)
                .foregroundColor(.secondary)
        }
        .padding(14)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(RoundedRectangle(cornerRadius: 16).fill(Color(.systemGray6)))
    }

    private func searchFolderRow(_ folder: FolderItem) -> some View {
        VStack(alignment: .leading, spacing: 6) {
            HStack {
                Text(folder.title)
                    .font(.headline)
                    .foregroundColor(.primary)

                Spacer()

                // Use pill style from FoldersView
                VisibilityPill(visibility: folder.visibility)
            }

            Text(folder.safeDescription)
                .font(.subheadline)
                .foregroundColor(.secondary)
                .lineLimit(1)

            Text("Created \(folder.createdAt)")
                .font(.caption)
                .foregroundColor(.secondary)
        }
        .padding(14)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(RoundedRectangle(cornerRadius: 16).fill(Color(.systemGray6)))
    }

    
    
    
    // Preview Sections

    @ViewBuilder
    private func postsPreviewSection(images: [String]) -> some View {
        VStack(spacing: 10) {
            // Move to posts ui
            NavigationLink(destination: PostsView(viewModel: PostsViewModel(repository: MockPostsRepository()))) {
                Text("Posts")
                    .font(.headline)
                    .padding(.horizontal, 14)
                    .padding(.vertical, 6)
                    .background(Color(.systemGray5))
                    .cornerRadius(10)
            }

            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 10) {
                    ForEach(images.indices, id: \.self) { index in
                        let img = images[index]
                        PreviewTile(imageName: img, label: "No data loaded in yet!")
                            .frame(width: 130, height: 90)
                    }
                }
                .padding(.horizontal, 14)
            }
        }
    }

    @ViewBuilder
    private func foldersPreviewSection(images: [String]) -> some View {
        VStack(spacing: 10) {
            // Move to folders ui
            NavigationLink(destination: FoldersView()) {
                Text("Folders")
                    .font(.headline)
                    .padding(.horizontal, 14)
                    .padding(.vertical, 6)
                    .background(Color(.systemGray5))
                    .cornerRadius(10)
            }

            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 10) {
                    ForEach(images.indices, id: \.self) { index in
                        let img = images[index]
                        PreviewTile(imageName: img, label: "No data loaded in yet!")
                            .frame(width: 130, height: 90)
                    }
                }
                .padding(.horizontal, 14)
            }
        }
    }

    // Data Loading

    // Load posts and folders once for searching
    private func loadSearchData() async {
        do {
            let posts = try await postsRepo.getPosts()
            let folders = try await foldersRepo.getFolders()

            await MainActor.run {
                self.allPosts = posts
                self.allFolders = folders
            }
        } catch {
            // Silent fail for now, add logging here if we run into issues!
        }
    }
}
