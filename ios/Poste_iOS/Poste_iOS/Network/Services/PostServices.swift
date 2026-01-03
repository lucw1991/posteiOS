import Foundation


final class PostServices {
    
    private let client: PosteAPIClient
    
    init(client: PosteAPIClient) {
        self.client = client
    }
    
    func listMyPosts(folderId: String? = nil, pageSize: Int? = nil, after: String? = nil) async throws -> PagedResponse<PostDTO> {
        var q: [URLQueryItem] = []
        if let folderId { q.append(URLQueryItem(name: "folderId", value: folderId)) }
        if let pageSize { q.append(URLQueryItem(name: "pageSize", value: String(pageSize))) }
        if let after { q.append(URLQueryItem(name: "after", value: after)) }

        return try await client.request(path: "/api/v1/posts",
                                        method: .GET,
                                        queryItems: q)
    }
    
    func createPost(_ dto: PostCreateDTO) async throws -> PostDTO {
        return try await client.request(path: "/api/v1/posts",
                                        method: .POST,
                                        body: dto)
    }
    
    func getPost(postId: String) async throws -> PostDTO {
        return try await client.request(path: "/api/v1/posts/\(postId)",
                                        method: .GET)
    }
    
}
