package com.digitalwallet.app.presentation.payment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*;
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TransferScreen(
    viewModel: TransferViewModel,
    onBack: () -> Unit
){

    val state by viewModel.state.collectAsState()

    var email by remember { mutableStateOf("") }
    var amount by remember {mutableStateOf("")}
    var description by remember { mutableStateOf("")}

    //Sucess dialog

    // ── Success dialog — outside Column, blocks everything else ──────
    if (state.success != null) {
        AlertDialog(
            onDismissRequest = { viewModel.reset(); onBack() },
            title = { Text("Transfer Sent ✅") },
            text  = { Text("$${state.success!!.amount} sent successfully!") },
            confirmButton = {
                TextButton(onClick = { viewModel.reset(); onBack() }) {
                    Text("Done")
                }
            }
        )
        return  // ← stops the rest of the composable from rendering
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Spacer(Modifier.height(32.dp))

        //header
        Row (verticalAlignment = Alignment.CenterVertically){
            TextButton(onClick = onBack){
                Text(text = "⬅️ Back")
            }
            Spacer(Modifier.width(8.dp))
            Text("Send Money", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        // -------------Step 1: Email lookup-------------
        OutlinedTextField(
            value = email,
            onValueChange = {email = it},
            label = {Text ("Receiver email")},
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {viewModel.findReceiver(email.trim())},
            enabled = email.isNotBlank() && !state.isSearching,
            modifier = Modifier.fillMaxWidth()
        ){
            if (state.isSearching){
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }else{
                Text("Find user")
            }
        }

        // ── Receiver found card ───────────────────────────────────────
        state.receiver?.let {
            receiver ->
            Card (
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column (modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Sending to:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Text(
                        text = "${receiver.firstName} ${receiver.lastName}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Text(
                        text = receiver.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // ── Step 2: Amount + description ──────────────────────────
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it},
                label = {Text("Amount")},
                prefix = {Text("$")},
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = {description = it},
                label = {Text("Description (optional)")},
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            //Error
            state.error?.let {
                error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull() ?: return@Button
                    viewModel.transfer(amt,description.ifBlank { null })
                },
                enabled = amount.isNotBlank() && !state.isSending,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ){
                if (state.isSending){
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                else {
                    Text("Send $$amount")
                }
            }
        }

        //error when user not found
        if (state.error != null && state.receiver == null){
            Text(
                text = state.error!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }



    }


}