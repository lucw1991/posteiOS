import SwiftUI


// Single card matching our Android app
struct PostCardView: View {
    
    let post: PostItem
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(post.title)
                .font(.title)
                .fontWeight(.semibold)
                .foregroundColor(.primary)
            
            Text(post.notes)
                .font(.body)
                .foregroundColor(.secondary)
            
            Text("Created \(post.createdAt)")
                .font(.caption)
                .foregroundColor(.secondary)
        }
        .padding(16)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(RoundedRectangle(cornerRadius: 20).fill(Color(.systemGray6)))
    }
}


// Main posts screen
struct PostsView: View {
    
    @StateObject var viewModel: PostsViewModel
    @State private var showingNewPostSheet = false
    
    /*
       Default so PostView() still works and we can still inject later
       PostsViewModel is @MainActor so the default must be inside one too.
     */
    @MainActor
    init(viewModel: PostsViewModel? = nil) {
        let vm = viewModel ?? PostsViewModel(repository: MockPostsRepository())
        _viewModel = StateObject(wrappedValue: vm)
    }
    
    
    var body: some View {
        NavigationStack {
            VStack(alignment: .leading, spacing: 0) {
                
                // Header row. All Posts and a new post button
                HStack {
                    Text("All Posts")
                        .font(.largeTitle)
                        .fontWeight(.bold)
                    
                    Spacer()
                    
                    Button(action: {
                        // Hook up Create Post here
                        showingNewPostSheet = true
                    }) {
                        HStack(spacing: 8) {
                            Image(systemName: "plus.circle")
                            Text("New Post")
                        }
                        .font(.subheadline)
                        .padding(.horizontal, 16)
                        .padding(.vertical, 8)
                        .background(Capsule().fill(Color.blue.opacity(0.9)))
                        .foregroundColor(.white)
                        
                    }
                }
                .padding(.horizontal, 16)
                .padding(.top, 16)
                .padding(.bottom, 8)
                
                
                // Content Area
                Group {
                    if viewModel.isLoading {
                        // Simple spinner while loading
                        VStack {
                            Spacer()
                            ProgressView()
                            Spacer()
                        }
                    } else if let error = viewModel.errorMsg {
                        // Error message
                        VStack {
                            Spacer()
                            Text(error)
                                .foregroundColor(.red)
                                .padding()
                            Spacer()
                        }
                    } else {
                        // Posts list
                        ScrollView {
                            VStack(spacing: 12) {
                                ForEach(viewModel.posts) { post in
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
            }
            .navigationTitle("All Posts")
            .navigationBarTitleDisplayMode(.inline)
            .task {
                // Load posts when this view first appears
                viewModel.loadPosts()
            }
        }
        .sheet(isPresented: $showingNewPostSheet) {
            NewPostSheet().applyNewPostSheetPresentation()
            
        }
    }
}


// Sheet presentation helper

extension View {
    @ViewBuilder
    func applyNewPostSheetPresentation() -> some View {
        if #available(iOS 16.0, *) {
            self
                .presentationDetents([.height(560)])
                .presentationDragIndicator(.visible)
                .modifier(presentationCornerRadiusIfAvailable())
        } else {
            self
            
        }
    }
}


private struct presentationCornerRadiusIfAvailable: ViewModifier {
    @ViewBuilder
    func body(content: Content) -> some View {
        if #available(iOS 16.4, *) {
            content.presentationCornerRadius(28)
        } else {
            content
        }
    }
}
