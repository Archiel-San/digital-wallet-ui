package com.digitalwallet.app.presentation.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitalwallet.app.data.model.TransactionResponse
import com.digitalwallet.app.data.model.UserResponse
import com.digitalwallet.app.data.repository.PaymentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


data class TransferUiState(
    val isSearching: Boolean = false,
    val isSending: Boolean = false,
    val receiver: UserResponse? = null,
    val success: TransactionResponse? = null,
    val error: String? = null
){}


class TransferViewModel(
    private val paymentRepository: PaymentRepository
): ViewModel() {

    private val _state = MutableStateFlow(TransferUiState())
    val state: StateFlow<TransferUiState> = _state


    // -- Step 1: find receiver by email
    fun findReceiver(email: String){
        viewModelScope.launch {
            _state.value = state.value.copy(
                isSearching = true,
                receiver = null,
                error = null,
                success = null
            )

            paymentRepository.findUserByEmail(email)
                .onSuccess {
                    user -> _state.value = _state.value.copy(
                        isSearching = false,
                        receiver = user
                    )
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        isSearching = false,
                        error = "User not found"
                    )
                }
        }
    }

    // step 2: confirm and send
    fun transfer(amount: Double, description: String?){
        val receiver = _state.value.receiver?: return

        viewModelScope.launch {
            _state.value = _state.value.copy(
                isSending = true,
                error = null,
            )

            //TODO ta dar internal server error, verificar 
            paymentRepository.transfer(receiver.keycloakId, amount, description)
                .onSuccess {
                    tx -> _state.value = _state.value.copy(
                        isSending = false,
                        success = tx,
                        receiver = null // reset for necxt transfer
                    )
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(
                        isSending = false,
                        error = e.message ?: "Transfer Failed"
                    )
                }

        }

    }

    fun reset(){
        _state.value = TransferUiState()
    }


}