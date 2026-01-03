import Foundation


enum FolderVisibility: String, CaseIterable, Identifiable {
    
    case `public`
    case `private`
    case unlisted
    
    var id: String { rawValue }
    
    var label: String {
        switch self {
        case .public: 
            return "public"
        case .private:
            return "private"
        case .unlisted:
            return "unlisted"
            
        }
    }
    
    
    var pickerLabel: String {
        switch self {
        case .public:
            return "Public - Anyone can view"
        case .private: 
            return "Private - Only visible to you"
        case .unlisted:
            return "Unlisted"
            
        }
    }
}


struct FolderItem: Identifiable, Equatable {
    
    let id: UUID
    var title: String
    var description: String?
    var visibility: FolderVisibility
    var createdAt: String   // Do we want to make this a date enventually?
    var theme: String?
    
    var safeDescription: String {
        (description?.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty == false) ? (description ?? "") : "No description"
    }
    
}
