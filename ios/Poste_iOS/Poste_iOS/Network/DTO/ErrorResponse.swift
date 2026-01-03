import Foundation


struct ErrorResponse: Codable {
    
    let message: String?
    let details: [ValidationDetail]?
    
    struct ValidationDetail: Codable {
        
        let field: String?
        let reason: String?
        
    }
    
}
