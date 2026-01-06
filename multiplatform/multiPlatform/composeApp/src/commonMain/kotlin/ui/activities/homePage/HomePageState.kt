package ui.activities.homePage

sealed class HomePageState {
    data object Loading : HomePageState()
    data class Success(val greeting: String = "Welcome User!") : HomePageState()
    data class Error(val message: String) : HomePageState()
}