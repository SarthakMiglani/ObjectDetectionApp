package com.example.objectdetectionapp.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.readymon.objectdetectionapp.data.HistoryEntity

@Composable
fun HistoryItemComposable(item: HistoryEntity) {
    Text(text = "Detected: ${item.detectedObjects}")
}

