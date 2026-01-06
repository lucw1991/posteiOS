package network.service

import network.PosteAPIClient
import network.dto.HealthResponse




class HealthService(private val api: PosteAPIClient) {
    suspend fun health(): HealthResponse =
        api.get("/api/health")
}