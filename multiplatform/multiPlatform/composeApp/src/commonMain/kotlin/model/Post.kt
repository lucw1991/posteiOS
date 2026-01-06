package model

data class Post(
    val id: String,
    val folderId: String,
    val title: String,
    val url: String,
    val notes: String?,
    val tags: List<String> = emptyList(),
    val createdAt: String
)