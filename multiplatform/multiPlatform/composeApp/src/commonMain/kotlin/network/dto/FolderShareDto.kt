package network.dto

import kotlinx.serialization.Serializable

/**
 * DTO for FolderShare responses
 */
@Serializable
data class FolderShareDto(
    val id: String,
    val folderId: String,
    val sharedWithUserId: String,
    val permission: String,
    val sharedAt: String
)

