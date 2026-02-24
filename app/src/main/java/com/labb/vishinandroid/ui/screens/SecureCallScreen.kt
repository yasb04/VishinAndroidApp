package com.labb.vishinandroid.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.labb.vishinandroid.ui.vievModel.SecureCallViewModel

@Composable
fun SecureCallScreen(
    onBackToFraudCheck: () -> Unit,
    secureCallViewModel: SecureCallViewModel = viewModel()
) {
    val callStatus by secureCallViewModel.callStatus.collectAsState()
    val localTranscript by secureCallViewModel.localTranscript.collectAsState()
    val remoteTranscript by secureCallViewModel.remoteTranscript.collectAsState()
    val fraudWarnings by secureCallViewModel.fraudWarnings.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Säkert samtal (beta)",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Status: ${callStatus.name}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { secureCallViewModel.startCall() },
            modifier = Modifier.fillMaxWidth(),
            enabled = callStatus == SecureCallViewModel.CallStatus.Idle
        ) {
            Text("Starta säkert samtal")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { secureCallViewModel.endCall() },
            modifier = Modifier.fillMaxWidth(),
            enabled = callStatus != SecureCallViewModel.CallStatus.Idle
        ) {
            Text("Avsluta samtal")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onBackToFraudCheck,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tillbaka till huvudvyn")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Din transkribering",
            style = MaterialTheme.typography.titleMedium
        )
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp)
            ) {
                Text(
                    text = if (localTranscript.isBlank()) "(Ingen transkribering ännu)" else localTranscript,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Motpartens transkribering",
            style = MaterialTheme.typography.titleMedium
        )
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp)
            ) {
                Text(
                    text = if (remoteTranscript.isBlank()) "(Ingen transkribering ännu)" else remoteTranscript,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Upptäckta riskfraser",
            style = MaterialTheme.typography.titleMedium
        )

        if (fraudWarnings.isEmpty()) {
            Text(
                text = "Inga misstänkta fraser ännu.",
                style = MaterialTheme.typography.bodySmall
            )
        } else {
            fraudWarnings.forEach { phrase ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    Text(
                        text = phrase,
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}


