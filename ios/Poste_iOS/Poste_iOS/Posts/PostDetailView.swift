import SwiftUI


struct PostDetailView: View {
    
    let post: PostItem
    
    // Local copy so edits can update the UI
    @State private var currentPost: PostItem
    
    @State private var isShowingEditSheet = false
    @State private var isShowingDeleteAlert = false
    
    init(post: PostItem) {
        self.post = post
        _currentPost = State(initialValue: post)
    }
    
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                
                // Title
                Text(currentPost.title)
                    .font(.title)
                    .fontWeight(.bold)
                
                // Image from URL, or a placeholder
                if let url = URL(string: currentPost.url) {
                    AsyncImage(url: url) { phase in
                        switch phase {
                        case .empty:
                            ProgressView().frame(height: 180)
                        case .success(let image):
                            image.resizable().scaledToFit().frame(maxWidth: .infinity).cornerRadius(8)
                        case .failure:
                            Rectangle()
                                .fill(Color(.systemGray5))
                                .frame(height: 180)
                                .overlay(Text("Image failed to load").font(.caption))
                        @unknown default:
                            EmptyView()
                        
                        }
                    }
                }
                
                // URL Section
                VStack(alignment: .leading, spacing: 4) {
                    Text("URL:")
                        .font(.headline)
                    
                    if let url = URL(string: currentPost.url) {
                        Link(currentPost.url, destination: url)
                            .font(.subheadline)
                            .foregroundColor(.blue)
                            .underline()
                    } else {
                        Text(currentPost.url)
                            .font(.subheadline)
                    }
                }
                
                // Notes Section
                VStack(alignment: .leading, spacing: 4) {
                    Text("Notes:")
                        .font(.headline)
                    
                    Text(currentPost.notes)
                        .font(.body)
                }
                
                // Tags chips
                if !currentPost.tags.isEmpty {
                    VStack(alignment: .leading, spacing: 4) {
                        Text("Tags:")
                            .font(.headline)
                        
                        WrapTagsView(tags: currentPost.tags)
                    }
                }
                
                // Created at
                Text("Created \(currentPost.createdAt)")
                    .font(.footnote)
                    .foregroundColor(.gray)
                
                // Edit and delete buttons
                HStack(spacing: 16) {
                    Button {
                        isShowingEditSheet = true
                    } label: {
                        Text("Edit")
                            .padding(.horizontal, 24)
                            .padding(.vertical, 10)
                            .background(Color(.systemIndigo))
                            .foregroundColor(.white)
                            .cornerRadius(12)
                    }
                    
                    Button {
                        isShowingDeleteAlert = true
                    } label: {
                        Text("Delete")
                            .padding(.horizontal, 24)
                            .padding(.vertical, 10)
                            .background(Color(.systemIndigo))
                            .foregroundColor(.white)
                            .cornerRadius(12)
                    }
                }
                .padding(.top, 8)
                
            }
            .padding()
            
        }
        .navigationTitle("Post Details")
        .navigationBarTitleDisplayMode(.inline)
        // Edit sheet
        .sheet(isPresented: $isShowingEditSheet) {
            EditPostSheet(
                originalPost: currentPost,
                onUpdate: { updated in
                    // Only local state for now
                    currentPost = updated
                }
            )
        }
        // Delete confirmation
        .alert("Delete Post", isPresented: $isShowingDeleteAlert) {
            Button("Cancel", role: .cancel) { }
            Button("Delete", role: .destructive) {
                // Make a real delete function here.
            }
        } message: {
            Text("Are you sure you want to delete this post? This cannot be undone.")
        }
        
    }
    
}


// Simple chip layout for tags
struct WrapTagsView: View {
    let tags: [String]
    
    var body: some View {
        // Simple row. We can replace with something more complex when ready.
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 8) {
                ForEach(tags, id: \.self) { tag in
                    Text(tag)
                        .font(.subheadline)
                        .padding(.horizontal, 12)
                        .padding(.vertical, 6)
                        .background(Color(.systemGray5))
                        .foregroundColor(.primary)
                        .cornerRadius(12)
                }
            }
        }
    }
}


struct EditPostSheet: View {
    @Environment(\.dismiss) private var dismiss
    
    let originalPost: PostItem
    let availableFolders: [String]
    let onUpdate: (PostItem) -> Void
    
    // Editable fields
    @State private var title: String
    @State private var url: String
    @State private var notes: String
    @State private var folderId: String
    @State private var tags: [String]
    @State private var newTag: String = ""
    
    init(
        originalPost: PostItem,
        availableFolders: [String] = ["Welcome", "Tips", "Announcements"],
        onUpdate: @escaping (PostItem) -> Void
    ) {
        self.originalPost = originalPost
        self.availableFolders = availableFolders
        self.onUpdate = onUpdate
        
        _title = State(initialValue: originalPost.title)
        _url = State(initialValue: originalPost.url)
        _notes = State(initialValue: originalPost.notes)
        _folderId = State(initialValue: originalPost.folderId)
        _tags = State(initialValue: originalPost.tags)
      }
    
    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    
                    // Title field
                    VStack(alignment: .leading, spacing: 4) {
                        Text("Title:")
                            .font(.caption)
                            .foregroundColor(.secondary)
                        
                        TextField("Title", text: $title)
                            .textFieldStyle(RoundedBorderTextFieldStyle())
                    }
                    
                    // URL Field
                    VStack(alignment: .leading, spacing: 4) {
                        Text("URL:")
                            .font(.caption)
                            .foregroundColor(.secondary)
                        
                        TextField("URL", text: $url)
                            .textFieldStyle(RoundedBorderTextFieldStyle())
                            .keyboardType(.URL)
                    }
                    
                    // Notes
                    VStack(alignment: .leading, spacing: 4) {
                        Text("Notes:")
                            .font(.caption)
                            .foregroundColor(.secondary)
                        
                        TextEditor(text: $notes)
                            .frame(minHeight: 120)
                            .overlay(RoundedRectangle(cornerRadius: 8)
                                .stroke(Color(.systemGray4), lineWidth: 1))
                    }
                    
                    // Folder picker, dummy values for now
                    VStack(alignment: .leading, spacing: 4) {
                        Text("Folder")
                            .font(.caption)
                            .foregroundColor(.secondary)
                        
                        Picker("Folder", selection: $folderId) {
                            ForEach(availableFolders, id: \.self) { folder in
                                Text(folder).tag(folder)
                            }
                        }
                        .pickerStyle(.menu)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .padding(10)
                        .background(RoundedRectangle(cornerRadius: 8)
                            .stroke(Color(.systemGray4), lineWidth: 1))
                    }
                    
                    // Tags input and chips. Simple for now.
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Tags")
                            .font(.caption)
                            .foregroundColor(.secondary)
                        
                        HStack {
                            TextField("Add Tag", text: $newTag)
                                .textFieldStyle(RoundedBorderTextFieldStyle())
                            
                            Button {
                                let trimmed = newTag.trimmingCharacters(in: .whitespacesAndNewlines)
                                guard !trimmed.isEmpty else { return }
                                if !tags.contains(trimmed) {
                                    tags.append(trimmed)
                                }
                                newTag = ""
                            } label: {
                                Image(systemName: "plus.circle.fill")
                            }
                        }
                        
                        // Simple horizontal tag list with remove buttons
                        ScrollView(.horizontal, showsIndicators: false) {
                            HStack(spacing: 8) {
                                ForEach(tags, id: \.self) { tag in
                                    HStack(spacing: 8) {
                                        Text(tag)
                                            .padding(.horizontal, 8)
                                            .padding(.vertical, 4)
                                            .background(Color(.systemGray5))
                                            .cornerRadius(10)
                                        
                                        Button {
                                            tags.removeAll {$0 == tag}
                                        } label: {
                                            Image(systemName: "xmark.circle.fill")
                                                .font(.caption)
                                                .foregroundColor(.gray)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                .padding()
                
            }
            .navigationTitle("Edit Post")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") {
                        dismiss()
                    }
                }
                
                ToolbarItem(placement: .confirmationAction) {
                    Button("Update") {
                        let updated = PostItem(
                            id: "00",
                            title: title,
                            notes: notes,
                            createdAt: originalPost.createdAt,
                            url: url,
                            tags: tags,
                            folderId: folderId
                        )
                        onUpdate(updated)
                        dismiss()
                        
                    }
                }
            }
        }
    }

    
    
    // Helpers
    private func sectionHeader(_ text: String) -> some View {
        Text(text)
            .font(.title2)
            .fontWeight(.semibold)
            .padding(.bottom, 4)
    }
    
    private func labeledField<Content: View>(
        title: String, @ViewBuilder content: () -> Content
    ) -> some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(title)
                .font(.caption)
                .foregroundColor(.gray)
            content()
        }
    }
    
}
