import SwiftUI


struct NewPostSheet: View {
    
    @Environment(\.dismiss) private var dismiss
    
    @State private var title: String = ""
    @State private var url: String = ""
    @State private var notes: String = ""
    
    @State private var folderName: String = "No Folder"
    private let availableFolders: [String] = ["No Folder", "Welcome", "Tips", "Announcements"]
    
    @State private var tags: [String] = []
    @State private var newTag: String = ""
    
    var body: some View {
        VStack(spacing: 18) {
            
            // Title
            Text("New Post")
                .font(.title2)
                .fontWeight(.semibold)
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.top, 6)
            
            
            // Fields
            VStack(spacing: 14) {
                textField(placeholder: "Title:", text: $title)
                
                textField(placeholder: "URL:", text: $url)
                    .keyboardType(.URL)
                    .autocapitalization(.none)
                    .disableAutocorrection(true)
                
                notesEditor
                folderPicker
                tagInput
            }
            Spacer(minLength: 0)
            
            // Bottom buttons for cancel and create
            HStack {
                Button("Cancel") {
                    dismiss()
                }
                .fontWeight(.semibold)
                
                Spacer()
                
                // Create does nothing for now. Will look into how we need to implement this when the backend is loaded.
                Button("Create") {
                    // Wire in here
                }
                .fontWeight(.semibold)
                .foregroundColor(.gray)
                .disabled(true)
            }
            .padding(.top, 6)
            
        }
        .padding(22)
        .background(Color(.systemBackground))
        
    }
    
    // Subviews
    private func textField(placeholder: String, text: Binding<String>) -> some View {
        TextField(placeholder, text: text)
            .padding()
            .background(Color(.systemGray6))
            .overlay(RoundedRectangle(cornerRadius: 10)
                .stroke(Color(.systemGray4), lineWidth: 1))
            .cornerRadius(10)
        
    }
    
    private var notesEditor: some View {
        ZStack(alignment: .topLeading) {
            TextEditor(text: $notes)
                .frame(minHeight: 120, maxHeight: 140)
                .padding(8)
                .background(Color(.systemGray6))
                .overlay(RoundedRectangle(cornerRadius: 10)
                    .stroke(Color(.systemGray4), lineWidth: 1))
                .cornerRadius(10)
            
            if notes.isEmpty {
                Text("Notes:")
                    .foregroundColor(.gray)
                    .padding(.horizontal, 16)
                    .padding(.vertical, 14)
            }
        }
    }
    
    private var folderPicker: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text("Folder")
                .font(.caption)
                .foregroundColor(.secondary)
            
            Picker("", selection: $folderName) {
                ForEach(availableFolders, id: \.self) { folder in
                    Text(folder).tag(folder)
                }
            }
            .pickerStyle(.menu)
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(12)
            .overlay(RoundedRectangle(cornerRadius: 10)
                .stroke(Color(.systemGray4), lineWidth: 1))
        }
    }
    
    private var tagInput: some View {
        VStack(alignment: .leading, spacing: 10) {
            HStack {
                TextField("Add a tag", text: $newTag)
                    .padding()
                    .background(Color(.systemGray6))
                    .overlay(RoundedRectangle(cornerRadius: 10)
                        .stroke(Color(.systemGray4), lineWidth: 1))
                    .cornerRadius(10)
                
                Button {
                    let trimmed = newTag.trimmingCharacters(in: .whitespacesAndNewlines)
                    guard !trimmed.isEmpty else { return }
                    if !tags.contains(trimmed) {
                        tags.append(trimmed)
                    }
                    newTag = ""
                    
                } label: {
                    Image(systemName: "plus.circle.fill")
                        .font(.title3)
                    
                }
                .padding(.leading, 6)
                
            }
            
            if !tags.isEmpty {
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 8) {
                        ForEach(tags, id: \.self) { tag in
                            HStack(spacing: 6) {
                                
                                Text(tag)
                                    .padding(.horizontal, 10)
                                    .padding(.vertical, 6)
                                    .background(Color(.systemGray5))
                                    .cornerRadius(10)
                                
                                Button {
                                    tags.removeAll { $0 == tag }
                                } label: {
                                    Image(systemName: "xmark.circle.fill")
                                        .foregroundColor(.gray)
                                    
                                }
                            }
                        }
                    }
                }
            }
            
        }
    }
    
}
