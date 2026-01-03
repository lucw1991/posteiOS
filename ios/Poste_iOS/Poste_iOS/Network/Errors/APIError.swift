import Foundation


enum APIError: Error, LocalizedError {
    
    case badRequest(message: String, details: [ErrorResponse.ValidationDetail]?)
    case unauthorized(message: String)
    case forbidden(message: String)
    case notFound(message: String)
    case server(message: String)
    case decoding(message: String)
    case transport(message: String)
    case unknown(code: Int, message: String)
    
    var errorDescription: String? {
        
        switch self {
        case .badRequest(message: let message, let details):
            if let details, !details.isEmpty {
                let detailsText = details.map { d in
                    let f = d.field ?? "field"
                    let r = d.reason ?? "invalid"
                    return "\(f): \(r)"
                }
                .joined(separator: ", ")
            return "\(message) (\(detailsText)"
                
            }
            return message
            
        case .unauthorized(message: let message):
            return message
            
        case .forbidden(message: let message):
            return message
            
        case .notFound(message: let message):
            return message
            
        case .server(message: let message):
            return message
            
        case .decoding(message: let message):
            return "Decoding error - \(message)"
            
        case .transport(message: let message):
            return "Network error - \(message)"
            
        case .unknown(_, message: let message):
            return "Unknown error - \(message)"
        }
        
    }
}
