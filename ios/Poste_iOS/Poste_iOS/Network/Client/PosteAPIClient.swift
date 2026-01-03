import Foundation

enum HTTPMethod: String {
    case GET, POST, PUT, PATCH, DELETE
}

final class PosteAPIClient {

    static let shared = PosteAPIClient()

    
    // Configuration

    private var baseURL: URL = URL(string: "https://eposte.up.railway.app")!
    private var authToken: String? = nil

    private let session: URLSession
    private let encoder: JSONEncoder
    private let decoder: JSONDecoder


    init(session: URLSession = .shared) {
        self.session = session

        self.encoder = JSONEncoder()
        self.decoder = JSONDecoder()
    }

    
    // Public configuration

    func setBaseUrl(_ urlString: String) {
        let trimmed = urlString.trimmingCharacters(in: .whitespacesAndNewlines).trimmingCharacters(in: CharacterSet(charactersIn: "/"))
        if let url = URL(string: trimmed) {
            baseURL = url
        }
    }

    func getBaseUrl() -> String {
        baseURL.absoluteString
    }

    func setAuthToken(_ token: String?) {
        authToken = token
    }

    func clearAuthToken() {
        authToken = nil
    }

    func hasAuthToken() -> Bool {
        authToken != nil
    }

    // Core request helper
    func request<T: Decodable>(
        path: String,
        method: HTTPMethod,
        queryItems: [URLQueryItem] = [],
        body: Encodable? = nil
    ) async throws -> T {

        let url = try buildURL(path: path, queryItems: queryItems)
        var req = URLRequest(url: url)
        req.httpMethod = method.rawValue
        req.setValue("application/json", forHTTPHeaderField: "Accept")

        if let token = authToken {
            req.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        }

        if let body {
            req.setValue("application/json", forHTTPHeaderField: "Content-Type")
            req.httpBody = try encoder.encode(AnyEncodable(body))
        }

        do {
            let (data, response) = try await session.data(for: req)

            guard let http = response as? HTTPURLResponse else {
                throw APIError.transport(message: "Bad server response.")
            }

            // Success
            if (200...299).contains(http.statusCode) {

                // Handle truly empty responses cleanly
                if T.self == EmptyResponse.self {
                    return EmptyResponse() as! T
                }

                // 204 with empty data.
                if data.isEmpty, let empty = EmptyResponse() as? T {
                    return empty
                }

                do {
                    return try decoder.decode(T.self, from: data)
                } catch {
                    // Debug logging here.
                    throw APIError.decoding(message: error.localizedDescription)
                }
            }

            // Non-2xx mapped to APIError
            let (msg, details) = parseErrorBody(data) // FIX: standardized name parseErrorBody
            throw mapStatusToAPIError(status: http.statusCode, message: msg, details: details)

        } catch let urlError as URLError {
            throw APIError.transport(message: urlError.localizedDescription)
        }
    }

    
    // URL building

    private func buildURL(path: String, queryItems: [URLQueryItem]) throws -> URL {
        
        let normalizedPath = path.hasPrefix("/") ? String(path.dropFirst()) : path

        guard var comps = URLComponents(url: baseURL, resolvingAgainstBaseURL: false) else {
            throw APIError.unknown(code: -1, message: "Invalid base URL")
        }

        let basePath = comps.path.trimmingCharacters(in: CharacterSet(charactersIn: "/"))
        let finalPath = ([basePath, normalizedPath].filter { !$0.isEmpty }).joined(separator: "/")
        comps.path = "/" + finalPath

        if !queryItems.isEmpty {
            comps.queryItems = queryItems
        }

        guard let url = comps.url else {
            throw APIError.unknown(code: -1, message: "Invalid request URL.")
        }
        return url
        
    }

    
    // Error parsing and mapping

    private func parseErrorBody(_ data: Data) -> (message: String, details: [ErrorResponse.ValidationDetail]?) {
        guard !data.isEmpty else {
            return ("Request failed.", nil)
        }

        if let parsed = try? decoder.decode(ErrorResponse.self, from: data) {
            let message = (parsed.message ?? "Request failed.")
            return (message, parsed.details)
        }

        // As a fallback, try to show raw text for debugging.
        if let text = String(data: data, encoding: .utf8), !text.isEmpty {
            return (text, nil)
        }

        return ("Request failed.", nil)
    }

    
    private func mapStatusToAPIError(
        status: Int,
        message: String,
        details: [ErrorResponse.ValidationDetail]?
    ) -> APIError {

        switch status {
        case 400:
            return .badRequest(message: message, details: details)
        case 401:
            return .unauthorized(message: message)
        case 403:
            return .forbidden(message: message)
        case 404:
            return .notFound(message: message)
        case 500...599:
            return .server(message: message)
        default:
            return .unknown(code: status, message: "HTTP \(status): \(message)")
        }
    }
}
