## I tried to recreat what we had in the Kotlin backend as best as I understood. It is pretty simple so fixes should not be too difficult to locate and implement when need be. I think I matched all as it should but once it gets so big it all gets muddied up so we will fix what needs it as we go! It runs and works for now so that is all that matters to me.

    - Layout:
        - Client:
            - PosteAPIClient
                - The core networking client. Includes the base URL, auth token, and request/response handling.
            - AnyEncodable
                - Type-erased wrapper so the client can accept any Encodable request body.
                
        - Services:
            - FolderServices
                - Folder endpoints. Includes list, create, get, share, unshare
            - PostServices
                - Post endpoints including list, create, get.
            - HealthService
                - Simple backend health check enpoint.
                
        - DTOs 
            - Codable structs matching the JSON payloads -- double chack.
                - Repsonse helpers: 
                    - PagedResponse, EmptyResponse, HealthResponse, ErrorResponse
                - Folder Payloads: 
                    - FolderDTO, FolderCreateDTO, FolderShareDTO, FolderShareCreateDTO
                - Post Payloads:
                    - PostDTO, PostCreateDTO
                
        - Errors
            - APIError is a single error type being used across the client/services with user readable messages.
            
            
    - Where the data is at
        - Base Url
            - Stored in PosteAPIClient.baseURL with the default value https://eposte.up.railway.app.
            - Change at runtime via setBaseURL() and inspect via getBaseURL()
            
        - Auth token (Bearer)
            - Stored in PosteAPIClient.authToken
            - Set or clear via setAuthToken() and clearAuthToken()
            - Automatically applied to outgoing requests as Authorization: Bearer <token>
            
        - JSON Encoding
            - The client owns a JSONEnconder and JSONDecoder and uses them in the request function.
            
        
    - Core request logic where most network behavior is controlled
        - PosteAPIClient.request<T: Decodable>() is the implementation being used by all services.
        
        - Behaviors:
            - Builds URLs by combining baseURL and path and appending query items
            - Adds standard headers (`Accept: application/json`, plus `Content-Type` when a body is present).
            - Encodes request bodies using AnyEncondable so any DTO can be passed.
            - Handles empty response cases cleanly using EmptyResponse.
            - Decodes successful JSON payloads into type T.
            - Uses ErrorResponse for non 2xx errors and maps HTTP status codes into APIError cases.


    - Services
        - Health:
            - HealthServices.health() --> GET /client/health  --> HealthResponse
            
        - Folders:
            - listMyFolders(pageSize: ,after:)  --> GET /api/v1/folders --> PagedResponse<FolderDTO>
            - createFolder(FolderCreateDTO) --> POST /api/v1/folders --> FolderDTO
            - getFolder(folderId:) --> GET /api/v1/folders/{id} --> FolderDTO 
            - getSharedFolders() --> GET /api/v1/folders/shared --> [FolderDTO]
            - shareFolder(folderId:, dto:) --> POST /api/v1/folders/{id}/shares --> FolderShareDTO
            - listFolderShares(folderId:) --> GET /api/v1/folders/{id}/shares --> [FolderShareDTO]
            - unshareFolder(folderId: ,userEmail:) --> DELETE /api/v1/folders/{id}/shares/{email} --> returns true when request succeeds (uses EmptyResponse).
            
        - Posts:
            - listMyPosts(folderId:pageSize:after:) --> GET /api/v1/posts --> PagedResponse<PostDTO>
            - createPost(PostCreateDTO) --> POST /api/v1/posts --> PostDTO
            - getPost(postId:) --> GET /api/v1/posts/{id} --> PostDTO
            
    - DTOs, will change when the backend changes I believe correct?
        - If the backend adds or renames fields, these files need change:
            - FolderDTO and PostDTO represent server responses.
            - FolderCreateDTO and PostCreateDTO represent create request bodies.
            - PagedResponse<T> describes the backend paging.
                - data, nextCursor, links 
            - ErrorResponse describes the backend error payload.    
            
            
    - What I think the next parts needed are, probably obvious but I put this in just to organize my thoughts.
        - Using services inside real repositories.
        - Map DTO to app models with PostDTO --> PostItem and FolderDTO --> FolderItem
        - Auth token needs to be wired. Once login returns a roken, we call PosteAPIClient,shared.setAuthToken so all following requests get authenticated.

            

