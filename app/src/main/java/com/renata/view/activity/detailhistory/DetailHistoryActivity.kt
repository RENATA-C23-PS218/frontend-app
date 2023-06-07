package com.renata.view.activity.detailhistory

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.renata.databinding.ActivityDetailHistoryBinding
import com.renata.utils.DateFormatter
import java.util.*

class DetailHistoryActivity : AppCompatActivity() {
    private lateinit var detailHistoryBinding: ActivityDetailHistoryBinding

    companion object {
        const val SOIL_PICT = "image"
        const val SOIL_NAME = "name"
        const val SCAN_DATE = "date"
        const val PLANT_RECOMM = "plant"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailHistoryBinding = ActivityDetailHistoryBinding.inflate(layoutInflater)
        setContentView(detailHistoryBinding.root)

        showLoading(false)
        val soilPict = intent.getStringExtra(SOIL_PICT)
        val soilName = intent.getStringExtra(SOIL_NAME)
        val scanDate = intent.getStringExtra(SCAN_DATE)
        val plantRec = intent.getStringExtra(PLANT_RECOMM)
        detailHistoryBinding.recomCropDetail.text = plantRec
        detailHistoryBinding.soilTypeDetail.text = soilName
        detailHistoryBinding.scanDateDetail.text =
            DateFormatter.formatDate(scanDate!!, TimeZone.getDefault().id)
        Glide.with(this)
            .load(soilPict)
            .into(detailHistoryBinding.previewImageViewDetail)
        detailHistoryBinding.backButton.setOnClickListener {
            finish()
            onBackPressed()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        detailHistoryBinding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}