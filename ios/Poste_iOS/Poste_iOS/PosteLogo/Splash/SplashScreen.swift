import SwiftUI


// This will show the splash logo quickly and then move on to login
struct SplashScreen: View {
    
    // Called when splash should transition to auth
    let onFinished: () -> Void
    
    var body: some View {
        
        ZStack {
            Color.white.ignoresSafeArea()
            
            SplashLogo()
        }
        .onAppear() {
            
            // Show splash for a second then move on
            DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) {
                onFinished()
            }
            
        }
        
    }
    
}
