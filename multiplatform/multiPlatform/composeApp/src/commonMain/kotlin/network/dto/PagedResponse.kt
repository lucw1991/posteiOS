package network.dto

import kotlinx.serialization.Serializable

/**
 * Generic wrapper for paginated responses
 */
@Serializable
data class PagedResponse<T>(
    val data: List<T>,
    val page: PageMeta
) {
    @Serializable
    data class PageMeta(
        val size: Int? = null,
        val nextCursor: String? = null
    )
}

