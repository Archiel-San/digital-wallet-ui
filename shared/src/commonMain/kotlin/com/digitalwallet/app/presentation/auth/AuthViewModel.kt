package com.digitalwallet.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitalwallet.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthUiState (
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false
)

class AuthViewModel(private val authRepository: AuthRepository): ViewModel(){

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state

    fun login (email: String, password: String){
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            authRepository.login(email, password).onSuccess {
                _state.value = _state.value.copy(isLoading = false, isLoggedIn = true)
            }
                .onFailure {
                    e -> _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Invalid email or Password"
                    )
                }

        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _state.value = AuthUiState()
        }
    }


}
