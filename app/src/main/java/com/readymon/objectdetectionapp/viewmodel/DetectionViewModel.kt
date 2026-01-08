package com.readymon.objectdetectionapp.viewmodel

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.readymon.objectdetectionapp.data.AppDatabase
import com.readymon.objectdetectionapp.data.HistoryEntity
import com.readymon.objectdetectionapp.ml.ObjectDetectionService
import com.readymon.objectdetectionapp.utils.CameraCaptureManager
import com.readymon.objectdetectionapp.utils.FileStorageHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class DetectionUiState(
    val processedBitmap: Bitmap? = null,
    val history: List<HistoryEntity> = emptyList()
)

class DetectionViewModel : ViewModel() {

    private var detectionService: ObjectDetectionService? = null

    private val _uiState = MutableStateFlow(DetectionUiState())
    val uiState = _uiState.asStateFlow()

    fun captureImage(context: Context, lifecycleOwner: LifecycleOwner) {
        CameraCaptureManager.capture(context, lifecycleOwner) { bitmap ->
            processBitmap(context.applicationContext, bitmap)
        }
    }

    private fun processBitmap(appContext: Context, bitmap: Bitmap) {
        viewModelScope.launch {

            // Initialize ML model once and reuse it
            if (detectionService == null) {
                detectionService = ObjectDetectionService(appContext)
            }

            val result = try {
                detectionService!!.detect(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
                return@launch
            }

            // Move disk I/O off the main thread
            val path = withContext(Dispatchers.IO) {
                FileStorageHelper.saveBitmap(appContext, result.bitmap)
            }

            val entity = HistoryEntity(
                imagePath = path,
                detectedObjects = result.labels.joinToString(),
                timestamp = System.currentTimeMillis()
            )

            val db = AppDatabase.get(appContext)
            db.historyDao().insert(entity)
            val history = db.historyDao().getAll()

            _uiState.value = DetectionUiState(
                processedBitmap = result.bitmap,
                history = history
            )
        }
    }
}
