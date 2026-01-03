import SwiftUI


struct PreviewTile: View {
    
    let imageName: String
    let label: String
    
    var body: some View {
        
        ZStack(alignment: .bottomLeading) {
            
            Image(imageName)
                .resizable()
                .scaledToFill()
                .frame(width: 140, height: 95)
                .clipped()
                .cornerRadius(14)
            
            VStack(alignment: .leading, spacing: 2) {
                Text(label)
                    .font(.caption)
                    .foregroundColor(.black)
                    .padding(6)
                    .background(Color.white.opacity(0.65).cornerRadius(8))
            }
            .padding(8)
            
        }
    }
}
