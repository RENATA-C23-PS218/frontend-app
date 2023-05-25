package com.renata.view.activity.result

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.renata.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    private lateinit var resultBinding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultBinding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(resultBinding.root)
        showLoading(false)

        val byteArray = intent.getByteArrayExtra("image")
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
        resultBinding.previewImageView.setImageBitmap(bitmap)

        val detectedClass = intent.getStringExtra("detected_class")
        resultBinding.soilType.text = detectedClass

        resultBinding.backButton.setOnClickListener {
            resultBinding.soilType.text = ""
            finish()
            onBackPressed()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        resultBinding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}
