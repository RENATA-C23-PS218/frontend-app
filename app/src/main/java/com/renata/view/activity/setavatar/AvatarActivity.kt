package com.renata.view.activity.setavatar

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.renata.data.retrofit.ApiConfig
import com.renata.data.user.login.LoginPreferences
import com.renata.data.user.login.LoginResult
import com.renata.data.user.updateprofile.UpdatePhotoResponse
import com.renata.databinding.ActivityAvatarBinding
import com.renata.utils.createCustomTempFile
import com.renata.utils.reduceFileImage
import com.renata.utils.rotateFile
import com.renata.utils.uriToFile
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AvatarActivity : AppCompatActivity() {

    private lateinit var avatarBinding: ActivityAvatarBinding
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        avatarBinding = ActivityAvatarBinding.inflate(layoutInflater)
        setContentView(avatarBinding.root)
        showLoading(false)
        avatarBinding.cameraButton.setOnClickListener { cameraPhoto() }
        avatarBinding.galleryButton.setOnClickListener { galleryPhoto() }
        avatarBinding.changeButton.setOnClickListener {uploadPhoto()}
        avatarBinding.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun cameraPhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AvatarActivity,
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
    ) {
        showLoading(false)
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                rotateFile(myFile, true)
                getFile = myFile
                avatarBinding.previewImageView.setImageBitmap(BitmapFactory.decodeFile(file.path))
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
            val myFile = uriToFile(selectedImg, this@AvatarActivity)
            getFile = myFile
            avatarBinding.previewImageView.setImageURI(selectedImg)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        avatarBinding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun uploadPhoto() {
            if (getFile != null) {
                val file = reduceFileImage(getFile as File)
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "file",
                    file.name,
                    requestImageFile
                )

                val intent = intent.getStringExtra("token")
                val token = "Bearer $intent"
                val client = ApiConfig.getApiService().uploadProfilePict(token, imageMultipart)
                client.enqueue(object: Callback<UpdatePhotoResponse>{
                    override fun onResponse(
                        call: Call<UpdatePhotoResponse>,
                        response: Response<UpdatePhotoResponse>
                    ) {
                        if (response.isSuccessful){
                            val responseBody = response.body()
                            if (responseBody != null){
                                Toast.makeText(this@AvatarActivity, responseBody.message, Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Log.e(TAG, "onFailure: ${response.message()}")
                            Toast.makeText(this@AvatarActivity, response.message(), Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<UpdatePhotoResponse>, t: Throwable) {
                        Toast.makeText(this@AvatarActivity, t.message, Toast.LENGTH_SHORT).show()
                    }

                })

            }else{
                Toast.makeText(this@AvatarActivity, "Choose the image first", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        const val TAG = "AvatarActivity"
    }

}