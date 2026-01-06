package network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronized
import kotlinx.serialization.json.Json
import network.dto.ErrorResponse
import network.exception.BadRequestException
import network.exception.ForbiddenException
import network.exception.NetworkException
import network.exception.NotFoundException
import network.exception.ServerException
import network.exception.UnauthorizedException
import network.exception.UnknownApiException
import network.service.FolderService
import network.service.HealthService
import network.service.PostService
import kotlin.concurrent.Volatile


class PosteAPIClient(baseUrl: String = "https://eposte.up.railway.app") {

    private var baseUrl: String = baseUrl.trimEnd('/')
    private var authToken: String? = null

    // Ignore unknown keys
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }

    val postService: PostService by lazy {
        PostService(this)
    }
    val folderService: FolderService by lazy {
        FolderService(this)
    }
    val healthService: HealthService by lazy {
        HealthService(this)
    }

    /*
    Client construction kept in commonMain for now and we will rely on Gradle and defaults.
    If I understand correctly, we will need to inject the correct engine per platform since
    Android uses OkHttp and iOS uses Darwin. The commonMain code can't hardcode a single engine
    that works everywhere so we could do something like define a function to set the platform
    client then give the actual implementations in androidMain and iosMain that will return
    HttpClient(OkHttp) and HttpClient(Darwin). Correct me if I do not have that right when we all
    start getting into this though!
    */
    @PublishedApi
    internal val client = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }
    }

    fun setBaseUrl(url: String) {
        baseUrl = url.trim().trimEnd('/')
    }

    fun getBaseUrl(): String = baseUrl

    fun setAuthToken(token: String?) {
        authToken = token
    }

    fun clearAuthToken() {
        authToken = null
    }

    fun hasAuthToken(): Boolean = authToken != null



    // Core request helpers

    suspend inline fun <reified T> get (path: String): T = requestAndMapErrors {
        client.get(buildUrl(path)) {
            attachHeaders()
        }
    }

    suspend inline fun <reified T> post(path: String, body: Any): T =
        requestAndMapErrors {
            client.post(buildUrl(path)) {
                attachHeaders()
                header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(body)
            }
        }

    suspend inline fun <reified T> put(path: String, body: Any): T =
        requestAndMapErrors {
            client.put(buildUrl(path)) {
                attachHeaders()
                header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(body)
            }
        }

    suspend fun delete(path: String) {
        requestAndMapErrors<Unit> {
            client.delete(buildUrl(path)) { attachHeaders() }
        }
    }

    @PublishedApi
    /*
    @PublishedApi internal means these are not part of our public API surface for normal use but they
    are allowed to be referenced from public inline functions.
    */
    internal fun buildUrl(path: String): String {
        val cleanPath = if (path.startsWith("/")) path else "/$path"
        return "$baseUrl$cleanPath"
    }

    @PublishedApi
    internal fun HttpRequestBuilder.attachHeaders() {
        header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
    }

    @PublishedApi
    internal suspend inline fun <reified T> requestAndMapErrors(block: () -> HttpResponse): T {
        try {
            val resp = block()
            if (resp.status.isSuccess()) {
                return if (T::class == Unit::class) Unit as T else resp.body()
            }

            throw mapHttpError(resp)

        } catch (e: ResponseException) {
            throw mapHttpError(e.response, cause = e)
        } catch (e: Exception) {
            throw NetworkException(message = e.message ?: "Network error", cause = e)
        }
    }

    @PublishedApi
    internal suspend fun mapHttpError(resp: HttpResponse, cause: Throwable? = null): Exception {
        val status = resp.status.value

        val parsed: ErrorResponse? = runCatching { resp.body<ErrorResponse>() }.getOrNull()
        val message = parsed?.message ?: "HTTP $status"

        return when (status) {
            400 -> BadRequestException(message, parsed?.details)
            401 -> UnauthorizedException(message)
            403 -> ForbiddenException(message)
            404 -> NotFoundException(message)
            in 500..599 -> ServerException(message, cause)
            else -> UnknownApiException("Unknown error: $status - $message", cause)
        }
    }


    companion object {
        @Volatile
        private var INSTANCE: PosteAPIClient? = null
        private val lock = Any()

        @OptIn(InternalCoroutinesApi::class)
        fun getInstance(): PosteAPIClient =
            INSTANCE ?: synchronized(lock as SynchronizedObject) {
                INSTANCE ?: PosteAPIClient().also {
                    INSTANCE = it
                }
            }
    }


}