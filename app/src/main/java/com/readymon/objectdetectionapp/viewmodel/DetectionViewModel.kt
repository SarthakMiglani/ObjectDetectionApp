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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DetectionUiState(
    val processedBitmap: Bitmap? = null,
    val history: List<HistoryEntity> = emptyList()
)

class DetectionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DetectionUiState())
    val uiState = _uiState.asStateFlow()

    fun captureImage(context: Context, lifecycleOwner: LifecycleOwner) {
        CameraCaptureManager.capture(context, lifecycleOwner) {
            processBitmap(context, it)
        }
    }

    private fun processBitmap(context: Context, bitmap: Bitmap) {
        viewModelScope.launch {
            val service = ObjectDetectionService(context)
            val result = service.detect(bitmap)
            val path = FileStorageHelper.saveBitmap(context, result.bitmap)
            val entity = HistoryEntity(
                imagePath = path,
                detectedObjects = result.labels.joinToString(),
                timestamp = System.currentTimeMillis()
            )
            val db = AppDatabase.get(context)
            db.historyDao().insert(entity)
            val history = db.historyDao().getAll()
            _uiState.value = DetectionUiState(result.bitmap, history)
        }
    }
}
