package com.renata.data

import android.app.Application
import android.graphics.Bitmap
import com.renata.ml.Model
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder


class RenataRepository(private val application: Application) : RenataInterface {
    private var imageSize: Int = 224

    override suspend fun classifyImage(image: Bitmap): String {
        try {
            val model: Model = Model.newInstance(application.applicationContext)
            val inputFeature0 = TensorBuffer.createFixedSize(
                intArrayOf(1, imageSize, imageSize, 3),
                DataType.FLOAT32
            )
            val byteBuffer: ByteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
            byteBuffer.order(ByteOrder.nativeOrder())
            val intValues = IntArray(imageSize * imageSize)
            image.getPixels(intValues, 0, image.width, 0, 0, image.width, image.height)
            var pixel = 0
            for (i in 0 until imageSize) {
                for (j in 0 until imageSize) {
                    val `val` = intValues[pixel++] // RGB
                    byteBuffer.putFloat((`val` shr 16 and 0xFF) * (1f / 1))
                    byteBuffer.putFloat((`val` shr 8 and 0xFF) * (1f / 1))
                    byteBuffer.putFloat((`val` and 0xFF) * (1f / 1))
                }
            }
            inputFeature0.loadBuffer(byteBuffer)
            val outputs: Model.Outputs = model.process(inputFeature0)
            val outputFeature0: TensorBuffer = outputs.outputFeature0AsTensorBuffer
            val confidences: FloatArray = outputFeature0.floatArray
            var maxPos = 0
            var maxConfidence = 0f
            for (i in confidences.indices) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i]
                    maxPos = i
                }
            }
            val classes = arrayOf(
                "Unknown",
                "Aluvial",
                "Andosol",
                "Entisol",
                "Humus",
                "Inceptisol",
                "Kapur",
                "Laterit",
                "Pasir"
            )
            val detectedClass = classes[maxPos]
            model.close()
            return detectedClass
        } catch (e: IOException) {
            throw Exception("Failed to classify image")
        }
    }
}