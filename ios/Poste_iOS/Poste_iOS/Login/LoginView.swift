import SwiftUI


// Login screen remade for iOS. I got it as close as I could to our Android Verison.
struct LoginView: View {
    
    // @State means SwiftUI owns and watches these values. When they change, the ui updates
    
    // Called when skipping the login
    let onSkip: () -> Void
    
    // True = login, False = sign up
    @State private var loginOrSignUp: Bool = true
    
    // Email and Passwrod fields
    @State private var email: String = ""
    @State private var password: String = ""
    
    
    var body: some View {
        
        // NavigationStack is like NavController and will allow us to push new screens later
        NavigationStack {
            
            // Vertical stack
            VStack(alignment: .leading, spacing: 32) {
                
                // Top logo
                PosteFullLogo().padding(.top, 40).padding(.horizontal)
                
                // Form title for log in or sign up
                Text(loginOrSignUp ? "Log In" : "New User Sign Up")
                    .font(.title2)
                    .fontWeight(.bold)
                    .padding(.horizontal)
                
                // Form fields and primary button
                VStack(spacing: 16) {
                    
                    // Email label and field
                    VStack(alignment: .leading, spacing: 6) {
                        
                        Text("Email").font(.subheadline).foregroundColor(.black)
                        
                        TextField("Email", text: $email)
                            .textContentType(.emailAddress)
                            .keyboardType(.emailAddress)
                            .autocapitalization(.none)
                            .disableAutocorrection(true)
                            .padding()
                            .background(Color(red: 0.94, green: 0.94, blue: 0.96))
                            .cornerRadius(6)
                        
                    }
                    
                    // Password label and field
                    VStack(alignment: .leading, spacing: 6) {
                        
                        Text("Password").font(.subheadline).foregroundStyle(Color(.black))
                        
                        SecureField("Password", text: $password)
                            .textContentType(.password)
                            .padding()
                            .background(Color(red: 0.94, green: 0.94, blue: 0.96))
                            .cornerRadius(6)
                        
                    }
                    
                    // Button to toggle log in or sign up
                    Button(action: {
                        // This will be where we call the backend whenever we are ready.
                        print("Log In / Sign Up button functioning!")
                    }) {
                        
                        Text(loginOrSignUp
                             ? "Don't have an account?" : "Already have an account?" )
                        .font(.subheadline)
                        .foregroundColor(.primary)
                        
                        Button(action: {
                            // Flip the mode and clear the fields
                            withAnimation {
                                loginOrSignUp.toggle()
                            }
                        }) {
                            
                            Text(loginOrSignUp ? "Sign Up" : "Log In")
                                .font(.subheadline)
                                .fontWeight(.semibold)
                                .foregroundColor(Color(red: 0.08, green: 0.37, blue: 0.56))
                            
                        }
                    }
                    .padding(.top, 4)
                    
                    // Log in / Sign up button. Will need to be implemented with backend. Does nothing for now.
                    Button(action: {
                        // Not wired correctly yet.
                        print("Dummy \(loginOrSignUp ? "Log In" : "Sign Up") button working!")
                    }) {
                        
                        Text(loginOrSignUp ? "Log In" : "Sign Up")
                            .fontWeight(.medium)
                            .foregroundColor(Color(red: 0.08, green: 0.37, blue: 0.56))
                            .padding(.vertical, 8)
                            .frame(maxWidth: .infinity)
                            .overlay(RoundedRectangle(cornerRadius: 16)
                                .stroke(Color(red: 0.08, green: 0.37, blue: 0.56), lineWidth: 1))
                        
                    }
                    
                }
                .padding(.horizontal)
                
                Spacer()
                
                
                // Skip button for log in
                HStack {
                    
                    Spacer()
                    Button(action: {
                        
                        onSkip()
                        
                    }) {
                        
                        Text("Skip Log In")
                            .fontWeight(.semibold)
                            .foregroundColor(.white)
                            .padding(.vertical, 10)
                            .padding(.horizontal, 30)
                            .background(Color.red)
                            .cornerRadius(18)
                        
                    }
                    Spacer()
                    
                }
                .padding(.bottom, 40)
                
            }
        }
    }
}
