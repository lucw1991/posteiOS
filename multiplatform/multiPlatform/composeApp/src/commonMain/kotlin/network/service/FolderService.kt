package network.service

import io.ktor.http.encodeURLQueryComponent
import network.PosteAPIClient
import network.dto.FolderCreateDto
import network.dto.FolderDto
import network.dto.FolderShareCreateDto
import network.dto.FolderShareDto
import network.dto.PagedResponse




class FolderService(private val api: PosteAPIClient) {

    suspend fun listMyFolders(pageSize: Int? = null,
                              after: String? = null): PagedResponse<FolderDto> {

        val query = buildString {
            val params = mutableListOf<String>()
            pageSize?.let {
                params += "pageSize=$it"
            }
            after?.let {
                params += "after=${it.encodeURLQueryComponent()}"
            }

            if (params.isNotEmpty()) {
                append("?")
                append(params.joinToString("&"))
            }
        }

        return api.get("/api/v1/folders$query")
    }

    suspend fun createFolder(dto: FolderCreateDto): FolderDto =
        api.post("/api/v1/folders", dto)

    suspend fun getFolder(folderId: String): FolderDto =
        api.get("/api/v1/folders/$folderId")

    suspend fun getSharedFolders(): List<FolderDto> =
        api.get("/api/v1/folders/shared")

    suspend fun shareFolder(folderId: String, dto: FolderShareCreateDto): FolderShareDto =
        api.post("/api/v1/folders/$folderId/share", dto)

    suspend fun listFolderShares(folderId: String): List<FolderShareDto> =
        api.get("/api/v1/folders/$folderId/shares")

    suspend fun unshareFolder(folderId: String, userEmail: String) {
        api.delete("/api/v1/folders/$folderId/share/${userEmail.encodeURLQueryComponent()}")
    }

}