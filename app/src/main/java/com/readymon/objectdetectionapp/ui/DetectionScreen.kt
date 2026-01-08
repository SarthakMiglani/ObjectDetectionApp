package com.readymon.objectdetectionapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.objectdetectionapp.ui.HistoryItemComposable
import com.readymon.objectdetectionapp.viewmodel.DetectionViewModel

@Composable
fun DetectionScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewModel: DetectionViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = {
            viewModel.captureImage(context, lifecycleOwner)
        }) {
            Text("Capture Image")
        }

        Spacer(modifier = Modifier.height(12.dp))

        state.processedBitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(300.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn {
            items(state.history.size) {
                HistoryItemComposable(state.history[it])
            }
        }
    }
}
