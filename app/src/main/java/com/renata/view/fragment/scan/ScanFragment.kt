package com.renata.view.fragment.scan

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.renata.R
import com.renata.databinding.FragmentScanBinding
import com.renata.utils.createCustomTempFile
import com.renata.utils.rotateFile
import com.renata.utils.uriToFile
import com.renata.view.activity.main.MainActivity
import com.renata.view.fragment.account.AccountFragment
import com.renata.view.fragment.history.HistoryFragment
import java.io.File

class ScanFragment : Fragment() {

    private var _binding: FragmentScanBinding? = null
    private val scanBinding get() = _binding!!
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        return scanBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLoading(false)
        scanBinding.cameraButton.setOnClickListener { cameraPhoto() }
        scanBinding.galleryButton.setOnClickListener { galleryPhoto() }
        scanBinding.detectButton.setOnClickListener {}
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun cameraPhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(requireContext().packageManager)
        createCustomTempFile(requireContext().applicationContext).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.renata",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            showLoading(true)
            launcherIntentCamera.launch(intent)
        }
    }


    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        showLoading(false)
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
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
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        showLoading(true)
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        showLoading(false)
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, requireContext())
            getFile = myFile
            scanBinding.imageLoading.visibility = View.GONE
            scanBinding.previewImageView.setImageURI(selectedImg)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        scanBinding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

//    private fun backToHistory() {
//        val moveToMain = Intent(activity, MainActivity::class.java)
//        startActivity(moveToMain)
//    }
}