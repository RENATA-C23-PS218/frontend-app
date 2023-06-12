package com.renata.view.activity.grow

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.renata.databinding.ActivityGrowBinding
import com.renata.view.activity.main.NavigationActivity

class GrowActivity : AppCompatActivity() {
    private lateinit var growBinding: ActivityGrowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        growBinding = ActivityGrowBinding.inflate(layoutInflater)
        setContentView(growBinding.root)
        showLoading(false)

        growBinding.growingStep.visibility = View.GONE
        plantET()
        val detectedClassGrow = intent.getStringExtra("detected_class")
        growBinding.soilTypeGrow.text = detectedClassGrow
        val plantRecommendationGrow = intent.getStringExtra("plant_recommendation")
        growBinding.recomCropGrow.text = plantRecommendationGrow
        growBinding.backButton.setOnClickListener {
            backToMain()
        }
        growBinding.growButton.setOnClickListener {
            growingStepProcess(detectedClassGrow!!)
        }
    }

    private fun plantET() {
        val myLoginEmailET = growBinding.edPlant
        myLoginEmailET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val plant = growBinding.edPlant.text.toString()
                if (plant.isEmpty()) {
                } else {
                    growBinding.errorPlant.visibility = View.INVISIBLE
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun growingStepProcess(soilType: String) {
        val plant = growBinding.edPlant.text.toString()
        when {
            plant.isEmpty() -> {
                showLoading(false)
                growBinding.errorPlant.visibility = View.VISIBLE
                growBinding.errorPlant.text = "Please Insert Plant Name"
            }
            else -> {
                if (!TextUtils.isEmpty(plant)) {
                    growStep(plant, soilType)
                } else {
                    showLoading(false)
                    showAlert(
                        "Failed to get step on grow",
                        "Make sure you enter the correct plant name"
                    ) { finish() }
                }
            }
        }
    }

    private fun growStep(plantName: String, soilTypeName: String) {
        growBinding.growingStep.visibility = View.VISIBLE
    }

    private fun showAlert(
        title: String,
        message: String,
        positiveAction: (dialog: DialogInterface) -> Unit
    ) {
        AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("OK") { dialog, _ -> positiveAction.invoke(dialog) }
            setCancelable(false)
            create()
            show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        backToMain()
    }

    private fun backToMain() {
        val intent = Intent(this@GrowActivity, NavigationActivity::class.java)
        startActivity(intent)
        overridePendingTransition(
            com.renata.R.anim.slide_out_bottom,
            com.renata.R.anim.slide_in_bottom
        )
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        growBinding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}