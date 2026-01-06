package network.service

import io.ktor.http.encodeURLQueryComponent
import network.PosteAPIClient
import network.dto.PagedResponse
import network.dto.PostCreateDto
import network.dto.PostDto




class PostService(private val api: PosteAPIClient) {

    suspend fun listPosts(folderId: String? = null,
                          pageSize: Int? = null,
                          after: String? = null): PagedResponse<PostDto> {

        val query = buildString {
            val params = mutableListOf<String>()
            folderId?.let {
                params += "folderId=$it"
            }
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

        return api.get("/api/v1/posts$query")
    }

    suspend fun createPost(dto: PostCreateDto): PostDto =
        api.post("/api/v1/posts", dto)

    suspend fun getPost(postId: String): PostDto =
        api.get("/api/v1/posts/$postId")

}