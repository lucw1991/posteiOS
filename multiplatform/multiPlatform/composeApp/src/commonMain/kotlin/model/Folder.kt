package model

data class Folder(
    val id: String,
    val title: String,
    val description: String?,
    val visibility: String = "private",
    val createdAt: String,
    //val postCount: String
    val theme: String? = null
)