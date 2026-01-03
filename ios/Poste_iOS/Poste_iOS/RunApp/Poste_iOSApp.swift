import SwiftUI

@main
struct Poste_iOSApp: App {
    
    // Track which screen we are on
    private enum RootScreen {
        case splash
        case auth
        case home
    }
    
    @State
    private var rootScreen: RootScreen = .splash
    
    var body: some Scene {
        WindowGroup {
            NavigationStack {
                
                switch rootScreen {
                case .splash:
                    // Show splash screen, then call onFinished to go to auth
                    SplashScreen {
                        rootScreen = .auth
                    }
                    
                case .auth:
                    // Log in and Sign up screen, skip goes to home
                    LoginView(onSkip: {
                        rootScreen = .home
                    })
                    
                case .home:
                    // Home screen
                    HomeView()
                }
                
                
            }
        }
    }
}
