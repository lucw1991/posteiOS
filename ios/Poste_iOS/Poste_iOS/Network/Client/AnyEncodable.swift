import Foundation


// This is a type erased Encodable wrapper so request() can accept any Encodable body
struct AnyEncodable: Encodable {
    
    private let encodeFunc: (Encoder) throws -> Void
    
    init(_ wrapped: Encodable) {
        self.encodeFunc = wrapped.encode
    }
    
    func encode(to encoder: Encoder) throws {
        try encodeFunc(encoder)
    }
    
}
