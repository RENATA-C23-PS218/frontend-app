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
import androidx.lifecycle.ViewModelProvider
import com.renata.databinding.ActivityScanBinding
import com.renata.ml.Model
import com.renata.utils.ViewModelFactory
import com.renata.utils.createCustomTempFile
import com.renata.utils.rotateFile
import com.renata.utils.uriToFile
import com.renata.view.activity.result.ResultActivity
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.min

class ScanActivity : AppCompatActivity() {

    private lateinit var scanBinding: ActivityScanBinding
    private lateinit var scanViewModel: ScanViewModel
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private var imageSize: Int = 224

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scanBinding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(scanBinding.root)
        scanViewModel = obtainViewModel(this as AppCompatActivity)
//        scanBinding.layoutAfter?.visibility = View.GONE
        showLoading(false)
        scanBinding.previewImageView.scaleType = ImageView.ScaleType.CENTER_CROP
        scanBinding.cameraButton.setOnClickListener { cameraPhoto() }
        scanBinding.galleryButton.setOnClickListener { galleryPhoto() }
        scanBinding.detectButton.setOnClickListener { detectPhoto() }
        scanBinding.backButton.setOnClickListener { onBackPressed() }
    }

    private fun obtainViewModel(activity: AppCompatActivity): ScanViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[ScanViewModel::class.java]
    }

    private fun detectPhoto() {
        showLoading(true)
        val drawable = scanBinding.previewImageView.drawable
        if (drawable != null) {
            val image = drawable.toBitmap()
            val dimension = min(image.width, image.height)
            val thumbnail = ThumbnailUtils.extractThumbnail(image, dimension, dimension)
            scanBinding.previewImageView.setImageBitmap(thumbnail)
            val scaledImage = Bitmap.createScaledBitmap(thumbnail, imageSize, imageSize, false)
            //classifyImage(scaledImage)
            scanViewModel.classifyImage(scaledImage).observe(this, { detectedClass ->
                if (detectedClass != null) {
                    showResultDialog(detectedClass, image)
                } else {
                    alertFail()
                }
                showLoading(false)
            })
        } else {
            showLoading(false)
            alertNull()
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
                "pasir"
            )
            val detectedClass = classes[maxPos]
            model.close()
            showResultDialog(detectedClass, image)
        } catch (e: IOException) {
            alertFail()
        }
    }

    private fun showResultDialog(detectedClass: String, image: Bitmap) {
        showLoading(false)
        val builder = AlertDialog.Builder(this, com.renata.R.style.CustomAlertDialog).create()
        val view = layoutInflater.inflate(com.renata.R.layout.custom_alert_dialog_success, null)
        val button = view.findViewById<Button>(com.renata.R.id.dialogDismiss_button)
        builder.setView(view)
        button.setOnClickListener {
            builder.dismiss()
//            scanBinding.layoutBefore?.visibility = View.GONE
//            scanBinding.layoutAfter?.visibility = View.VISIBLE
//            scanBinding.soilType?.text = detectedClass
            val compressedImage = compressBitmap(image) // Kompres ukuran bitmap
            val intent = Intent(this@ScanActivity, ResultActivity::class.java)
            val bStream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.PNG, 100, bStream)
            val byteArray = bStream.toByteArray()
            intent.putExtra("image", byteArray)
            intent.putExtra("detected_class", detectedClass)
            startActivity(intent)
            overridePendingTransition(
                com.renata.R.anim.slide_out_bottom,
                com.renata.R.anim.slide_in_bottom
            )
            finish()
        }
        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

    fun restartActivity(view: View) {
        val intent = intent
        finish()
        startActivity(intent)
    }

    private fun compressBitmap(bitmap: Bitmap): Bitmap {
        val maxFileSize = 1024 * 1024
        var compression = 90
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, compression, outputStream)
        while (outputStream.toByteArray().size > maxFileSize && compression > 10) {
            compression -= 10
            outputStream.reset()
            bitmap.compress(Bitmap.CompressFormat.PNG, compression, outputStream)
        }
        val compressedBitmap = BitmapFactory.decodeByteArray(
            outputStream.toByteArray(),
            0,
            outputStream.toByteArray().size
        )
        outputStream.close()
        return compressedBitmap
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

    private fun alertNull() {
        val builder = AlertDialog.Builder(this, com.renata.R.style.CustomAlertDialog)
            .create()
        val view = layoutInflater.inflate(com.renata.R.layout.custom_alert_dialog_image_null, null)
        val button = view.findViewById<Button>(com.renata.R.id.dialogTry_button)
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
        if (result.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                rotateFile(myFile, true)
                getFile = myFile
                scanBinding.imageLoading.visibility = View.GONE
                scanBinding.previewImageView.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        } else {
            showLoading(false)
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