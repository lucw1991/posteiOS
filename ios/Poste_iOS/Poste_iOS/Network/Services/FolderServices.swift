import Foundation


final class FolderServices {
    
    private let client: PosteAPIClient
    
    init(client: PosteAPIClient = .shared) {
        self.client = client
    }
    
    func listMyFolders(pageSize: Int? = nil, after: String? = nil) async throws -> PagedResponse<FolderDTO> {
            var q: [URLQueryItem] = []
            if let pageSize { q.append(URLQueryItem(name: "pageSize", value: String(pageSize))) }
            if let after { q.append(URLQueryItem(name: "after", value: after)) }

        return try await client.request(path: "/api/v1/folders",
                                        method: .GET,
                                        queryItems: q)
        }

    func createFolder(_ dto: FolderCreateDTO) async throws -> FolderDTO {
        return try await client.request(path: "/api/v1/folders",
                                        method: .POST,
                                        body: dto)
    }
    
    func getFolder(folderId: String) async throws -> FolderDTO {
        return try await client.request(path: "/api/v1/folders/\(folderId)",
                                        method: .GET)
    }

    func getSharedFolders() async throws -> [FolderDTO] {
        return try await client.request(path: "/api/v1/folders/shared",
                                        method: .GET)
    }

    func shareFolder(folderId: String, dto: FolderShareCreateDTO) async throws -> FolderShareDTO {
        return try await client.request(path: "/api/v1/folders/\(folderId)/shares",
                                        method: .POST,
                                        body: dto)
    }
    
    func listFolderShares(folderId: String) async throws -> [FolderShareDTO] {
        return try await client.request(path: "/api/v1/folders/\(folderId)/shares",
                                        method: .GET)
    }
    
    /*
    Our Kotlin version returns a Response<Unit> and caller checks isSuccessful but in Swift we will return true if 2xx and give an APIError otherwise.
    */
    func unshareFolder(folderId: String, userEmail: String) async throws -> Bool {
        let _: EmptyResponse = try await client.request(path: "/api/v1/folders/\(folderId)/shares/\(userEmail)",
                                                        method: .DELETE)
        return true
    }
    
}
