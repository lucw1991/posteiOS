import Foundation

// Simple, align this with DTO files
struct PostItem: Identifiable {
    let id: String
    let title: String
    let notes: String
    let createdAt: String   // This is how we have it in Kotlin. Will likely change to date.
    let url: String
    let tags: [String]
    let folderId: String
}
