package lk.nibm.kandy.hdse252ft.smartrestaurantpos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    val isLoggedIn: Boolean
        get() = authRepository.isLoggedIn

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _isAuthSuccess = MutableStateFlow(false)
    val isAuthSuccess = _isAuthSuccess.asStateFlow()

    fun onEmailChange(value: String) {
        _email.value = value
        _errorMessage.value = null
    }

    fun onPasswordChange(value: String) {
        _password.value = value
        _errorMessage.value = null
    }

    fun login(onSuccess: () -> Unit) {
        val mail = _email.value.trim()
        val pwd = _password.value.trim()

        if (mail.isEmpty() || pwd.isEmpty()) {
            _errorMessage.value = "Email and Password cannot be empty"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = authRepository.loginWithEmail(mail, pwd)
            _isLoading.value = false
            
            result.fold(
                onSuccess = {
                    _isAuthSuccess.value = true
                    onSuccess()
                },
                onFailure = { error ->
                    _errorMessage.value = error.localizedMessage ?: "Login failed. Please try again."
                }
            )
        }
    }

    fun register(onSuccess: () -> Unit) {
        val mail = _email.value.trim()
        val pwd = _password.value.trim()

        if (mail.isEmpty() || pwd.isEmpty()) {
            _errorMessage.value = "Email and Password cannot be empty"
            return
        }
        if (pwd.length < 6) {
            _errorMessage.value = "Password must be at least 6 characters long"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = authRepository.registerWithEmail(mail, pwd)
            _isLoading.value = false
            
            result.fold(
                onSuccess = {
                    _isAuthSuccess.value = true
                    onSuccess()
                },
                onFailure = { error ->
                    _errorMessage.value = error.localizedMessage ?: "Registration failed. Please try again."
                }
            )
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
    
    fun logout() {
        authRepository.logout()
        _isAuthSuccess.value = false
    }
}
