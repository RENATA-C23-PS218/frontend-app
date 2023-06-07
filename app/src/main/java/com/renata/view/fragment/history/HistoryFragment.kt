package com.renata.view.fragment.history

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.renata.data.Result
import com.renata.data.plant.scanhistory.HistoryAdapter
import com.renata.data.plant.scanhistory.ScanHistory
import com.renata.data.user.login.LoginPreferences
import com.renata.data.user.login.LoginResult
import com.renata.databinding.FragmentHistoryBinding
import com.renata.utils.ViewModelFactory
import com.renata.view.activity.detailhistory.DetailHistoryActivity

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val historyBinding get() = _binding!!
    private lateinit var loginPreference: LoginPreferences
    private lateinit var loginResult: LoginResult
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var adapter: HistoryAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return historyBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginPreference = LoginPreferences(requireContext())
        loginResult = loginPreference.getUser()
        val token = "Bearer ${loginResult.token}"
        val adapter = HistoryAdapter()
        historyBinding.rvListHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            this.adapter = adapter
        }
        historyViewModel = obtainViewModel(context as AppCompatActivity)
        historyViewModel.scanHistories(token).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    val data = result.data
                    if (data.success) {
                        historyBinding.layoutLoading.visibility = View.INVISIBLE
                        val histories = data.dataHistory.scanHistory
                        adapter.updateData(histories)
                    } else {
                    }
                }
                is Result.Error -> {
                }
                is Result.Loading -> {
                }
            }
        }
        adapter.setOnItemClickCallback(object : HistoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ScanHistory) {
                Intent(requireContext(), DetailHistoryActivity::class.java).also {
                    it.putExtra(DetailHistoryActivity.SOIL_PICT, data.image)
                    it.putExtra(DetailHistoryActivity.SOIL_NAME, data.soil_Type)
                    // it.putExtra(DetailHistoryActivity.PLAN_RECOMM, data.) // Anda perlu menambahkan data yang ingin Anda kirim ke DetailHistoryActivity
                    it.putExtra(DetailHistoryActivity.SCAN_DATE, data.date)
                    startActivity(it)
                }
            }
        })
    }


    private fun obtainViewModel(activity: AppCompatActivity): HistoryViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[HistoryViewModel::class.java]
    }

    private fun showLoading(state: Boolean) {
        historyBinding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }
}