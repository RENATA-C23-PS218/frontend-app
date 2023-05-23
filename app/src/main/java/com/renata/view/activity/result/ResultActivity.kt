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
        val detectedClass = intent.getStringExtra("detected_class")
        resultBinding.soilType.text = detectedClass
        showLoading(false)
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