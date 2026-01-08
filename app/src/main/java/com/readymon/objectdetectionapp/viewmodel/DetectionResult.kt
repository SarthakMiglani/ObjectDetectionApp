package com.readymon.objectdetectionapp.viewmodel


import android.graphics.Bitmap

data class DetectionResult(
    val bitmap: Bitmap,
    val labels: List<String>
)
