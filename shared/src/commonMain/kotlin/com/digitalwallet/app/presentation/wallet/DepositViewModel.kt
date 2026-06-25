package com.digitalwallet.app.presentation.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitalwallet.app.data.model.WalletResponse
import com.digitalwallet.app.data.repository.WalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


data class DepositUiState(
    val isSending: Boolean = false,
    val success: WalletResponse? = null,
    val error: String? = null
){}


class DepositViewModel(
    private val walletRepository: WalletRepository
): ViewModel() {
    private val _state = MutableStateFlow(DepositUiState())
    val state: StateFlow<DepositUiState> = _state


    fun deposit(amount: Double, description: String?){
        viewModelScope.launch {
            _state.value = _state.value.copy(isSending = true, error = null)

            walletRepository.deposit(amount, description)
                .onSuccess {
                    wallet ->
                    _state.value = _state.value
                        .copy(isSending = true, success = wallet)
                }
                .onFailure {
                    e ->
                    _state.value = _state.value.copy(
                        isSending = false, error = e.message?: "Deposit failed"
                    )
                }
        }
    }

    fun reset(){
        _state.value = DepositUiState()
    }



}