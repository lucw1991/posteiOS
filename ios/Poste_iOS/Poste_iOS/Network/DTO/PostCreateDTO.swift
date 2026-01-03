import Foundation


struct PostCreateDTO: Codable {
    
    let folderId: String?
    let title: String
    let url: String?
    let notes: String?
    let tags: [String]?
    
    
}
