package com.renata.view.fragment.history

import android.app.Application
import androidx.lifecycle.ViewModel
import com.renata.data.RenataRepository

class HistoryViewModel(application: Application) : ViewModel() {
    private val repository = RenataRepository(application)
    fun scanHistories(token: String) = repository.scanHistory(token)
    fun detailHistory(token: String, scanId: String) = repository.detailHistory(token, scanId)
}