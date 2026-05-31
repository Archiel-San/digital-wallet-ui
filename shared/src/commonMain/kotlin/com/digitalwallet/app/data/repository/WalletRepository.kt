package com.digitalwallet.app.data.repository

import com.digitalwallet.app.data.local.TokenStorage
import com.digitalwallet.app.data.model.LedgerEntry
import com.digitalwallet.app.data.model.WalletResponse
import com.digitalwallet.app.network.ApiClient

class WalletRepository (
    private val tokenStorage: TokenStorage,
)
{
    suspend fun getWallet(): Result<WalletResponse>{
        return ApiClient.getWithAuth("/api/wallets/me", tokenStorage)
    }

    suspend fun getLedger() : Result<List<LedgerEntry>> {
        return ApiClient.getWithAuth("/api/wallets/me/ledger", tokenStorage)
    }
}
