package com.renata.data.plant.scanhistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.renata.databinding.HistoryLayoutBinding
import com.renata.utils.DateFormatter
import java.util.*

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
    private var scanHistories: List<ScanHistory> = listOf()
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    fun updateData(histories: List<ScanHistory>) {
        scanHistories = histories.sortedByDescending { it.date }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = HistoryLayoutBinding.inflate(inflater, parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val scanHistory = scanHistories[position]
        holder.bind(scanHistory)
    }

    override fun getItemCount(): Int {
        return scanHistories.size
    }

    inner class HistoryViewHolder(private val binding: HistoryLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(scanHistory: ScanHistory) {
            binding.tvSoilName.text = scanHistory.soil_Type
            binding.tvScanDate.text =
                DateFormatter.formatDate(scanHistory.date, TimeZone.getDefault().id)
            Glide.with(itemView)
                .load(scanHistory.image)
                .into(binding.ivSoilImage)
            binding.root.setOnClickListener {
                onItemClickCallback.onItemClicked(scanHistory)
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ScanHistory)
    }
}