package com.readymon.objectdetectionapp.utils


import android.content.Context
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner

object CameraCaptureManager {

    fun capture(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        onBitmap: (android.graphics.Bitmap) -> Unit
    ) {
        val providerFuture = ProcessCameraProvider.getInstance(context)

        providerFuture.addListener({
            val cameraProvider = providerFuture.get()
            cameraProvider.unbindAll()

            val imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                imageCapture
            )

            imageCapture.takePicture(
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        val bitmap = BitmapUtils.fromImageProxy(image)
                        image.close()
                        onBitmap(bitmap)
                    }
                }
            )
        }, ContextCompat.getMainExecutor(context))
    }
}
