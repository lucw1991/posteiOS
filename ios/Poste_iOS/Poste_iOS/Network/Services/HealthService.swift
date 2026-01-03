import Foundation


final class HealthService {
    
    private let client: PosteAPIClient
    
    init(client: PosteAPIClient = .shared) {
        self.client = client
    }
    
    func health() async throws -> HealthResponse {
        try await client.request(path: "/client/health",
                              method: .GET)
    }
    
}
