import SwiftUI


struct EditFolderView: View {
    
    @Environment(\.dismiss) private var dismiss
    
    @State private var title: String
    @State private var description: String
    @State private var visibility: FolderVisibility
    @State private var theme: String
    
    private let original: FolderItem
    private let onSave: (FolderItem) -> Void
    
    init(folder: FolderItem, onSave: @escaping (FolderItem) -> Void) {
        self.original = folder
        self.onSave = onSave
        
        _title = State(initialValue: folder.title)
        _description = State(initialValue: folder.description ?? "")
        _visibility = State(initialValue: folder.visibility)
        _theme = State(initialValue: folder.theme ?? "No Theme")
    }
    
    
    var body: some View {
        VStack(spacing: 18) {
            Image(systemName: "folder.fill")
                .font(.system(size: 54))
                .foregroundColor(.gray)
                .padding(.top, 18)
            
            VStack(alignment: .leading, spacing: 6) {
                Text("Folder Name")
                    .font(.caption)
                    .foregroundColor(.secondary)
                
                TextField("", text: $title)
                    .textFieldStyle(.roundedBorder)
            }
            .padding(.horizontal, 16)
            
            VStack(alignment: .leading, spacing: 6) {
                Text("Description (Optional)")
                    .font(.caption)
                    .foregroundColor(.secondary)
                
                TextField("", text: $description)
                    .textFieldStyle(.roundedBorder)
            }
            .padding(.horizontal, 16)
            
            VStack(alignment: .leading, spacing: 6) {
                Text("Visibility")
                    .font(.caption)
                    .foregroundColor(.secondary)
                
                Picker("", selection: $visibility) {
                    ForEach(FolderVisibility.allCases) { v in
                        Text(v.pickerLabel).tag(v)
                    }
                }
                .pickerStyle(.menu)
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(12)
                .overlay(RoundedRectangle(cornerRadius: 10).stroke(Color(.systemGray4), lineWidth: 1))
                
            }
            .padding(.horizontal, 16)
            
            VStack(alignment: .leading, spacing: 6) {
                Text("Theme")
                    .font(.caption)
                    .foregroundColor(.secondary)
                
                Picker("", selection: $theme) {
                    Text("No Theme").tag("No Theme")
                    Text("Blue").tag("Blue")
                    Text("Gray").tag("Gray")
                }
                .pickerStyle(.menu)
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(12)
                .overlay(RoundedRectangle(cornerRadius: 10).stroke(Color(.systemGray4), lineWidth: 1))
            }
            .padding(.horizontal, 16)
            
            Spacer()
            
            Button {
                let updated = FolderItem(
                    id: original.id,
                    title: title.trimmingCharacters(in: .whitespacesAndNewlines),
                    description: description.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty ? nil : description,
                    visibility: visibility,
                    createdAt: original.createdAt,
                    theme: theme == "No Theme" ? nil : theme
                )
                onSave(updated)
                dismiss()
            } label: {
                Text("Save Changes")
                    .fontWeight(.semibold)
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 14)
                    .background(Capsule().fill(Color.blue.opacity(0.85)))
            }
            .padding(.horizontal, 16)
            .padding(.bottom, 18)
            
        }
        .navigationTitle("Edit Folder")
        .navigationBarTitleDisplayMode(.inline)
        
    }
}
