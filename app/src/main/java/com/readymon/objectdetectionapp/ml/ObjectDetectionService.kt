package com.readymon.objectdetectionapp.ml

import android.content.Context
import android.graphics.*
import com.readymon.objectdetectionapp.viewmodel.DetectionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class ObjectDetectionService(context: Context) {

    private val interpreter: Interpreter
    private val labels: List<String>

    init {
        val modelBuffer = loadModelFile(context)
        interpreter = Interpreter(modelBuffer)
        labels = context.assets.open("labels.txt").bufferedReader().readLines()
    }

    private fun loadModelFile(context: Context): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd("model.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            startOffset,
            declaredLength
        )
    }

    suspend fun detect(bitmap: Bitmap): DetectionResult = withContext(Dispatchers.Default) {
        val input = com.readymon.objectdetectionapp.ml.ImagePreprocessor.preprocess(bitmap)

        val boxes = Array(1) { Array(10) { FloatArray(4) } }
        val classes = Array(1) { FloatArray(10) }
        val scores = Array(1) { FloatArray(10) }
        val count = FloatArray(1)

        val outputs = mapOf(
            0 to boxes,
            1 to classes,
            2 to scores,
            3 to count
        )

        interpreter.runForMultipleInputsOutputs(arrayOf(input), outputs)

        val resultBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(resultBitmap)
        val paint = Paint().apply {
            color = Color.RED
            strokeWidth = 4f
            style = Paint.Style.STROKE
        }

        val detectedLabels = mutableListOf<String>()

        for (i in 0 until count[0].toInt()) {
            if (scores[0][i] > 0.5f) {
                val box = boxes[0][i]
                val left = box[1] * bitmap.width
                val top = box[0] * bitmap.height
                val right = box[3] * bitmap.width
                val bottom = box[2] * bitmap.height
                canvas.drawRect(left, top, right, bottom, paint)
                detectedLabels.add(labels[classes[0][i].toInt()])
            }
        }

        DetectionResult(resultBitmap, detectedLabels)
    }
}
