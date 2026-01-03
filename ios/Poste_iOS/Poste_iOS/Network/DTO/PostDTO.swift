import Foundation


struct PostDTO: Codable {
    
    let id: String
    let folderId: String?
    let title: String
    let url: String?
    let notes: String?
    let tags: [String]?
    let createdAt: String?
    let deletedAt: String?
    
}
