package lk.nibm.kandy.hdse252ft.smartrestaurantpos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.UserRole
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.repository.AuthRepository
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.repository.UserRepository
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.ui.navigation.Screen
import javax.inject.Inject

sealed class SplashDestination {
    data object AdminDashboard : SplashDestination()
    data class Menu(val tableNumber: Int) : SplashDestination()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination?>(null)
    val destination = _destination.asStateFlow()

    fun resolveDestination() {
        viewModelScope.launch {
            if (authRepository.isLoggedIn) {
                val uid = authRepository.currentUserId
                val role = userRepository.getUserRole(uid)
                _destination.value = if (role == UserRole.ADMIN) {
                    SplashDestination.AdminDashboard
                } else {
                    SplashDestination.Menu(0)
                }
            } else {
                _destination.value = SplashDestination.Menu(0)
            }
        }
    }

    fun setDeepLinkTable(tableNumber: Int) {
        _destination.value = SplashDestination.Menu(tableNumber)
    }

    fun destinationRoute(destination: SplashDestination): String = when (destination) {
        SplashDestination.AdminDashboard -> Screen.AdminDashboard.route
        is SplashDestination.Menu -> Screen.Menu.createRoute(destination.tableNumber)
    }
}
