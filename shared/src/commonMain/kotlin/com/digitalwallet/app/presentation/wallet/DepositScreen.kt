package com.digitalwallet.app.presentation.wallet

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun DepositScreen (
    viewModel: DepositViewModel,
    onBack:() -> Unit
){

    val state by viewModel.state.collectAsState()

    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("")}

    //Success dialog
    if (state.success!= null){
        AlertDialog(
            onDismissRequest = {viewModel.reset(); onBack()},
            title = {Text("Deposit successful ✅")},
            text = {
                Text("New balance: $${state.success!!.balance}")
            },
            confirmButton = {
                TextButton(onClick = {viewModel.reset(); onBack()}){
                    Text("Done")
                }
            }
        )
        return // importante para devolver o screen ao home
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ){
        Spacer(Modifier.height(32.dp))

        //Header

        Row(verticalAlignment = Alignment.CenterVertically){
            TextButton(onClick = onBack) {
                Text("← Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Funds", fontWeight = FontWeight.Bold, fontSize = 22.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = {amount = it},
            label = {Text("Amount")},
            prefix = {Text("$")},
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = {description= it},
            label ={Text("Description (optional)")},
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

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
                val amt = amount.toDoubleOrNull()?: return@Button
                viewModel.deposit(amt, description.ifBlank { null })
            },
            enabled = amount.isNotBlank() && !state.isSending,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ){
            if(state.isSending){
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }else{
                Text("Add $$amount")
            }
        }

    }




}