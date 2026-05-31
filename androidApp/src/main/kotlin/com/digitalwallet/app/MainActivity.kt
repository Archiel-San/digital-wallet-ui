package com.digitalwallet.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.digitalwallet.app.data.local.TokenStorage
import com.digitalwallet.app.data.repository.AuthRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val tokenStorage = TokenStorage(applicationContext)

        setContent {
            App(
                authRepository = AuthRepository(tokenStorage),
                tokenStorage   = tokenStorage
            )
        }
    }
}
