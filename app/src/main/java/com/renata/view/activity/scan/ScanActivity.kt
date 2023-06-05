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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModelProvider
import com.renata.data.Result
import com.renata.data.user.login.LoginPreferences
import com.renata.data.user.login.LoginResult
import com.renata.databinding.ActivityScanBinding
import com.renata.utils.ViewModelFactory
import com.renata.utils.createCustomTempFile
import com.renata.utils.rotateFile
import com.renata.utils.uriToFile
import com.renata.view.activity.result.ResultActivity
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.math.min

class ScanActivity : AppCompatActivity() {
    private lateinit var scanBinding: ActivityScanBinding
    private lateinit var scanViewModel: ScanViewModel
    private lateinit var currentPhotoPath: String
    private lateinit var loginPreference: LoginPreferences
    private lateinit var loginResult: LoginResult
    val plantNames = mutableListOf<String>()
    private var plantRecommendation: String = ""
    private var getFile: File? = null
    private var imageSize: Int = 224

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scanBinding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(scanBinding.root)

        loginPreference = LoginPreferences(this)
        loginResult = loginPreference.getUser()
        val token = "Bearer ${loginResult.token}"
        scanViewModel = obtainViewModel(this as AppCompatActivity)
        scanBinding.layoutAfter?.visibility = View.GONE
        showLoading(false)
        scanBinding.previewImageView.scaleType = ImageView.ScaleType.CENTER_CROP
        scanBinding.cameraButton.setOnClickListener { cameraPhoto() }
        scanBinding.galleryButton.setOnClickListener { galleryPhoto() }
        scanBinding.detectButton.setOnClickListener {
            if (token != null) {
                detectPhoto(token)
            }
        }
        scanBinding.backButton.setOnClickListener { onBackPressed() }
    }

    private fun obtainViewModel(activity: AppCompatActivity): ScanViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[ScanViewModel::class.java]
    }

    private fun saveToHistory() {

    }

    private fun generateCrop(detectedClass: String) {
    }

    private fun detectPhoto(token: String) {
        showLoading(true)
        val drawable = scanBinding.previewImageView.drawable
        if (drawable != null) {
            val image = drawable.toBitmap()
            val dimension = min(image.width, image.height)
            val thumbnail = ThumbnailUtils.extractThumbnail(image, dimension, dimension)
            scanBinding.previewImageView.setImageBitmap(thumbnail)
            val compressedImage = compressBitmap(thumbnail)
            scanViewModel.classifyImage(compressedImage).observe(this) { detectedClass ->
                if (detectedClass != null) {
                    showResult(detectedClass, image, token)
                } else {
                    alertFail()
                }
                showLoading(false)
            }
        } else {
            showLoading(false)
            alertNull()
        }
    }

    private fun compressBitmap(bitmap: Bitmap): Bitmap {
        val scaleFactor = calculateScaleFactor(bitmap.width, bitmap.height, imageSize)
        return Bitmap.createScaledBitmap(
            bitmap,
            (bitmap.width * scaleFactor).toInt(),
            (bitmap.height * scaleFactor).toInt(),
            false
        )
    }

    private fun calculateScaleFactor(width: Int, height: Int, targetSize: Int): Float {
        val maxWidth = width.coerceAtMost(height)
        return if (maxWidth <= targetSize) {
            1.0f
        } else {
            targetSize.toFloat() / maxWidth.toFloat()
        }
    }

    private fun showResult(detectedClass: String, image: Bitmap, token: String) {
        showLoading(false)
        val builder = AlertDialog.Builder(this, com.renata.R.style.CustomAlertDialog).create()
        val view = layoutInflater.inflate(com.renata.R.layout.custom_alert_dialog_success, null)
        val button = view.findViewById<Button>(com.renata.R.id.dialogDismiss_button)
        builder.setView(view)
        button.setOnClickListener {
            builder.dismiss()
            scanBinding.layoutBefore?.visibility = View.GONE
            scanBinding.layoutAfter?.visibility = View.VISIBLE
            scanBinding.soilType?.text = detectedClass
            scanBinding.cropButton?.setOnClickListener {
                scanViewModel.cropRecommendation(token, detectedClass)
                    .observe(this@ScanActivity) { result ->
                        if (result != null) {
                            when (result) {
                                is Result.Loading -> {
                                    showLoading(true)
                                }
                                is Result.Error -> {
                                    showLoading(false)
                                    Toast.makeText(this, "Masuk Result.Error", Toast.LENGTH_SHORT)
                                        .show()
                                }
                                is Result.Success -> {
                                    showLoading(false)
                                    val plantRecommendation =
                                        result.data.dataPlant.joinToString(", ") { it.plant_name }
                                    val compressedImage = compressBitmap(image)
                                    val intent =
                                        Intent(this@ScanActivity, ResultActivity::class.java)
                                    val bStream = ByteArrayOutputStream()
                                    compressedImage.compress(Bitmap.CompressFormat.PNG, 50, bStream)
                                    val byteArray = bStream.toByteArray()
                                    intent.putExtra("image", byteArray)
                                    intent.putExtra("detected_class", detectedClass)
                                    intent.putExtra("plant_recommendation", plantRecommendation)
                                    startActivity(intent)
                                    overridePendingTransition(
                                        com.renata.R.anim.slide_out_bottom,
                                        com.renata.R.anim.slide_in_bottom
                                    )
                                }
                            }
                        }
                    }
            }
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
        button.setOnClickListener { builder.dismiss() }
        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

    private fun alertNull() {
        val builder = AlertDialog.Builder(this, com.renata.R.style.CustomAlertDialog)
            .create()
        val view = layoutInflater.inflate(com.renata.R.layout.custom_alert_dialog_image_null, null)
        val button = view.findViewById<Button>(com.renata.R.id.dialogTry_button)
        builder.setView(view)
        button.setOnClickListener { builder.dismiss() }
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

    fun restartActivity(view: View) {
        val intent = intent
        finish()
        startActivity(intent)
    }
}