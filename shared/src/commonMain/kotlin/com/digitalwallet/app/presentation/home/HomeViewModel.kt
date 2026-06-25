package com.digitalwallet.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitalwallet.app.data.model.LedgerEntry
import com.digitalwallet.app.data.model.UserResponse
import com.digitalwallet.app.data.model.WalletResponse
import com.digitalwallet.app.data.repository.UserRepository
import com.digitalwallet.app.data.repository.WalletRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val user: UserResponse? = null,
    val wallet: WalletResponse? = null,
    val ledger: List<LedgerEntry> = emptyList(),
    val error: String? = null,
    val sessionExpired: Boolean = false
)

class HomeViewModel(private val userRepository: UserRepository,
                    private val walletRepository: WalletRepository,
): ViewModel() {

    //live data stream...sempre precisa de valor inicial, neste caso é o DTO
    //util para o frontend que na recomposicao ou no lock screen, algo ja orgânico
    private val _state = MutableStateFlow(HomeUiState())
    // for UI purposes only
    val state: StateFlow<HomeUiState> = _state

    // calls the function straight away
    init {
        loadAll()
    }

    /*
    fun loadAll() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error =  null)

            val user = userRepository.getMe()
            val wallet = walletRepository.getWallet()
            val ledger = walletRepository.getLedger()


            val sessionExpired = listOf(user, wallet).any {
                val message = it.exceptionOrNull()?.message
                message == "Session expired" || message == "Not logged in"
            }

            _state.value = HomeUiState(
                isLoading = false,
                user = user.getOrNull(),
                wallet = wallet.getOrNull(),
                ledger = ledger.getOrElse { emptyList() },
                error = if (!sessionExpired && user.isFailure) {
                    "Failed to load user: ${user.exceptionOrNull()?.message ?: "unknown error"}"
                } else if (!sessionExpired && wallet.isFailure) {
                    "Failed to load wallet: ${wallet.exceptionOrNull()?.message ?: "unknown error"}"
                } else {
                    null
                },
                sessionExpired = sessionExpired
            )
        }
    }

     */

    fun loadAll() {
        viewModelScope.launch {
            // ← don't reset wallet/user/ledger — keep showing old data while refreshing
            _state.value = _state.value.copy(isLoading = true, error = null)

            val userDeferred   = async { userRepository.getMe() }
            val walletDeferred = async { walletRepository.getWallet() }
            val ledgerDeferred = async { walletRepository.getLedger() }

            val user   = userDeferred.await()
            val wallet = walletDeferred.await()
            val ledger = ledgerDeferred.await()

            _state.value = _state.value.copy(
                isLoading = false,
                user      = user.getOrNull()   ?: _state.value.user,   // keep old if fails
                wallet    = wallet.getOrNull() ?: _state.value.wallet,
                ledger    = ledger.getOrElse { _state.value.ledger },
                error     = if (user.isFailure && wallet.isFailure) "Failed to refresh" else null
            )
        }
    }
}
