import SwiftUI

/*
 
 System will show this when launching. Apple doesn't let us animate or time delay from here so using this, we use
 another fake splash that can be animated.
 
*/

struct SplashLogo: View {
    
    var body: some View {
        ZStack {
            
            Color.white
            
            Image("Logo_splash").resizable().scaledToFit().frame(width: 180, height: 180)
            
        }
        .ignoresSafeArea()
    }
    
}
