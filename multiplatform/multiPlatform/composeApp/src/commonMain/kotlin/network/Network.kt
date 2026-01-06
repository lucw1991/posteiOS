package network

import network.service.FolderService
import network.service.HealthService
import network.service.PostService



/*
This should be a simple wiring point for all network services. It keeps our construction in one
place so we can all find it quickly.
*/

class Network(val api: PosteAPIClient = PosteAPIClient()) {

    val health = HealthService(api)
    val posts = PostService(api)
    val folders = FolderService(api)

}