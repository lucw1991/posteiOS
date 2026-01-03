import SwiftUI

struct PosteFullLogo: View {
    
    var body: some View {
        ZStack {
            
            Color.white
            
            Image("Logo_full").resizable().scaledToFit().frame(width: 180, height: 180)
            
        }
        .ignoresSafeArea()
    }
    
}

