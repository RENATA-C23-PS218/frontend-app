package com.renata.view.activity.scan

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import com.renata.databinding.ActivityScanBinding
import com.renata.ml.Model
import com.renata.utils.createCustomTempFile
import com.renata.utils.rotateFile
import com.renata.utils.uriToFile
import com.renata.view.activity.result.ResultActivity
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.min

class ScanActivity : AppCompatActivity() {

    private lateinit var scanBinding: ActivityScanBinding
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private var imageSize: Int = 224

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scanBinding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(scanBinding.root)
        showLoading(false)
        scanBinding.previewImageView.scaleType = ImageView.ScaleType.CENTER_CROP
        scanBinding.cameraButton.setOnClickListener { cameraPhoto() }
        scanBinding.galleryButton.setOnClickListener { galleryPhoto() }
        scanBinding.detectButton.setOnClickListener { detectPhoto() }
        scanBinding.backButton.setOnClickListener { onBackPressed() }
    }

    private fun detectPhoto() {
        val drawable = scanBinding.previewImageView.drawable
        if (drawable != null) {
            val image = drawable.toBitmap()
            val dimension = min(image.width, image.height)
            val thumbnail = ThumbnailUtils.extractThumbnail(image, dimension, dimension)
            scanBinding.previewImageView.setImageBitmap(thumbnail)
            val scaledImage = Bitmap.createScaledBitmap(thumbnail, imageSize, imageSize, false)
            classifyImage(scaledImage)
        } else {
            alertFail()
        }
    }

    fun classifyImage(image: Bitmap) {
        try {
            val model: Model = Model.newInstance(applicationContext)
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
            val outputFeature0: TensorBuffer = outputs.getOutputFeature0AsTensorBuffer()
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
            showResultDialog(detectedClass)
        } catch (e: IOException) {
            // TODO Handle the exception
        }
    }

    private fun showResultDialog(detectedClass: String) {
        val builder = AlertDialog.Builder(this, com.renata.R.style.CustomAlertDialog)
            .create()
        val view = layoutInflater.inflate(com.renata.R.layout.custom_alert_dialog_success, null)
        val button = view.findViewById<Button>(com.renata.R.id.dialogDismiss_button)
        builder.setView(view)
        button.setOnClickListener {
            builder.dismiss()
            val intent = Intent(this@ScanActivity, ResultActivity::class.java)
            intent.putExtra("detected_class", detectedClass)
            startActivity(intent)
            overridePendingTransition(
                com.renata.R.anim.slide_out_bottom,
                com.renata.R.anim.slide_in_bottom
            )
        }
        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

    private fun alertFail() {
        val builder = AlertDialog.Builder(this, com.renata.R.style.CustomAlertDialog)
            .create()
        val view = layoutInflater.inflate(com.renata.R.layout.custom_alert_dialog_fail, null)
        val button = view.findViewById<Button>(com.renata.R.id.dialogFailDismiss_button)
        builder.setView(view)
        button.setOnClickListener {
            builder.dismiss()
        }
        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

    private fun cameraPhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@ScanActivity,
                "com.renata",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        showLoading(false)
        if (result.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                rotateFile(myFile, true)
                getFile = myFile
                scanBinding.imageLoading.visibility = View.GONE
                scanBinding.previewImageView.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private fun galleryPhoto() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        showLoading(false)
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@ScanActivity)
            getFile = myFile
            scanBinding.imageLoading.visibility = View.GONE
            scanBinding.previewImageView.setImageURI(selectedImg)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        scanBinding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
