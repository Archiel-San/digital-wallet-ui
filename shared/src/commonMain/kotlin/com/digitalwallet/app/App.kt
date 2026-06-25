package com.digitalwallet.app

// App.kt — root composable with navigation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.digitalwallet.app.data.local.TokenStorage
import com.digitalwallet.app.data.repository.AuthRepository
import com.digitalwallet.app.data.repository.PaymentRepository
import com.digitalwallet.app.data.repository.UserRepository
import com.digitalwallet.app.data.repository.WalletRepository
import com.digitalwallet.app.presentation.auth.AuthViewModel
import com.digitalwallet.app.presentation.auth.LoginScreen
import com.digitalwallet.app.presentation.home.HomeScreen
import com.digitalwallet.app.presentation.home.HomeViewModel
import com.digitalwallet.app.presentation.payment.TransferScreen
import com.digitalwallet.app.presentation.payment.TransferViewModel
import com.digitalwallet.app.presentation.wallet.DepositScreen
import com.digitalwallet.app.presentation.wallet.DepositViewModel

@Composable
fun App(
    authRepository: AuthRepository,
    tokenStorage: TokenStorage,
) {
    val navController = rememberNavController()

    // ← CHECK TOKENS ON STARTUP
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        startDestination = if (tokenStorage.getTokens() != null) "home" else "login"
    }

    val authViewModel = remember { AuthViewModel(authRepository) }
    // Wait until we know where to start
    if (startDestination == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }


    NavHost(
        navController = navController,
        startDestination = startDestination!!
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
        // Add to NavHost in App.kt
        composable("transfer") {
            val transferViewModel = remember {
                TransferViewModel(
                    paymentRepository = PaymentRepository(
                        tokenStorage = tokenStorage,
                        //TODO see if this is really needed
                        /*
                        onSessionExpired = {
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }

                         */
                    )
                )
            }
            TransferScreen(
                viewModel = transferViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("home") {
            val homeViewModel = remember {
                HomeViewModel(
                    userRepository   = UserRepository(tokenStorage),
                    walletRepository = WalletRepository(tokenStorage = tokenStorage)
                )
            }
            HomeScreen(
                viewModel = homeViewModel,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onNavigateToTransfer = { navController.navigate("transfer") },
                onNavigateToDeposit  = { navController.navigate("deposit") }  // ← add this
            )

        }

        composable("deposit") {
            val depositViewModel = remember {
                DepositViewModel(
                    walletRepository = WalletRepository(
                        tokenStorage = tokenStorage,
                        /*
                        onSessionExpired = {
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }

                         */
                    )
                )
            }
            DepositScreen(
                viewModel = depositViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
