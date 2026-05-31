package com.digitalwallet.app

// App.kt — root composable with navigation
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.digitalwallet.app.data.local.TokenStorage
import com.digitalwallet.app.data.repository.AuthRepository
import com.digitalwallet.app.data.repository.UserRepository
import com.digitalwallet.app.data.repository.WalletRepository
import com.digitalwallet.app.presentation.auth.AuthViewModel
import com.digitalwallet.app.presentation.auth.LoginScreen
import com.digitalwallet.app.presentation.home.HomeScreen
import com.digitalwallet.app.presentation.home.HomeViewModel

@Composable
fun App(
    authRepository: AuthRepository,
    tokenStorage: TokenStorage,
) {
    val navController = rememberNavController()
    val authViewModel = remember { AuthViewModel(authRepository) }

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            val homeViewModel = remember {
                HomeViewModel(
                    userRepository   = UserRepository(tokenStorage),
                    walletRepository = WalletRepository(tokenStorage)
                )
            }
            HomeScreen(
                viewModel = homeViewModel,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )

        }
    }
}
