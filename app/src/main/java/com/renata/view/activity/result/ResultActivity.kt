package com.renata.view.activity.result

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.renata.databinding.ActivityResultBinding
import com.renata.view.activity.main.NavigationActivity

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
        val plantRecommendation = intent.getStringExtra("plant_recommendation")
        resultBinding.recomCrop.text = plantRecommendation
        resultBinding.backButton.setOnClickListener {
            backToMain()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        backToMain()
    }

    private fun backToMain() {
        val intent = Intent(this@ResultActivity, NavigationActivity::class.java)
        startActivity(intent)
        overridePendingTransition(
            com.renata.R.anim.slide_out_bottom,
            com.renata.R.anim.slide_in_bottom
        )
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        resultBinding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}

