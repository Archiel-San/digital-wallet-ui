package com.digitalwallet.app.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digitalwallet.app.data.model.LedgerEntry
import com.digitalwallet.app.data.model.LedgerEntryType


@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onLogout: () -> Unit,
    onNavigateToTransfer: () -> Unit,
    onNavigateToDeposit:() -> Unit,
    ){

    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.sessionExpired) {
        if (state.sessionExpired) {
            onLogout()
        }
    }

    if(state.isLoading){
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            CircularProgressIndicator()
        }
        return
    }

    state.error?.let { error ->
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(error, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(12.dp))
                Button(onClick = {viewModel.loadAll()}){
                    Text("Retry")
                }
            }
        }
        return
    }

    Scaffold(
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onNavigateToTransfer,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Send Money")
                    }

                    OutlinedButton(
                        onClick = onNavigateToDeposit,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Add Funds")
                    }
                }
            }
        }
    ) { contentPadding ->
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { viewModel.loadAll() },
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

            // ── Header ─────────────────────────────────────────────────
            item {

                Spacer(Modifier.height(52.dp))
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hello, ${state.user?.firstName ?: "there"} 👋",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text (
                            text = state.user?.email?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(onClick = onLogout){
                        Text("Logout")
                    }

                }
            }

            // ── Balance card ────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(24.dp)
                ){
                    Column{
                        Text(
                            text = "Available Balance",
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "$${state.wallet?.balance ?: "0.00" }",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Status: ${state.wallet?.status?: "-"}",
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                }
            }

            // ── Ledger header ───────────────────────────────────────────
            item {
                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if(state.ledger.isEmpty()){
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = "No Transactions Yet",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            else{
                items (state.ledger){
                        entry -> LedgerEntryRow(entry)
                }
            }

            item {
                Spacer(Modifier.height(24.dp))
            }

            }
        }
    }
}

@Composable
fun LedgerEntryRow(entry: LedgerEntry) {

    val isCredit = entry.ledgerType == LedgerEntryType.CREDIT
    val creditColor = Color(0xFF2E7D32)

    Row (
        modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(MaterialTheme.colorScheme.surfaceVariant)
        .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column (modifier = Modifier.weight(1f)) {
            Text(
                text = entry.description ?: if (isCredit) "Deposit" else "Transfer",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }

        Text(
            text = "${if (isCredit) "+" else "-"}$${entry.amount}",
            fontWeight = FontWeight.Bold,
            color =
                if(isCredit)
                    creditColor
                else
                    MaterialTheme.colorScheme.error
        )

    }


}
