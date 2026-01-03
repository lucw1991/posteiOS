import Foundation


struct PagedResponse<T: Codable>: Codable {
    
    let data: [T]
    let nextCursor: String?
    let links: PageMeta
    
    struct PageMeta: Codable {
        
        let size: Int?
        let nextCursor: String?
        
    }
    
}
