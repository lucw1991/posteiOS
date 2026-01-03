import Foundation


struct FolderDTO: Codable {
    
    let id: String
    let ownerId: String?
    let title: String
    let description: String?
    let visibility: String?
    let createdAt: String?
    let deletedAt: String?
    
}
