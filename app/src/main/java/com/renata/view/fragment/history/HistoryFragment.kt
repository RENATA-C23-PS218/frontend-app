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
import com.renata.R
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
                    if (data != null) {
                        if (data.success) {
                            val histories = data.dataHistory.scanHistory
                            if (histories.size != 0) {
                                historyBinding.layoutLoading.visibility = View.INVISIBLE
                                adapter.updateData(histories)
                            } else {
                                historyBinding.layoutLoading.visibility = View.VISIBLE
                            }
                        } else {
                            historyBinding.layoutLoading.visibility = View.VISIBLE
                        }
                    } else {
                        historyBinding.layoutLoading.visibility = View.VISIBLE
                    }
                }
                is Result.Error -> {
                    historyBinding.layoutLoading.visibility = View.VISIBLE
                    historyBinding.noHistory.text = getString(R.string.fail_show_history)
                }
                is Result.Loading -> {
                    historyBinding.layoutLoading.visibility = View.VISIBLE
                }
            }
        }
        adapter.setOnItemClickCallback(object : HistoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ScanHistory) {
                val scanId = data.scan_id
                historyViewModel.detailHistory(token, scanId)
                    .observe(viewLifecycleOwner) { result ->
                        when (result) {
                            is Result.Success -> {
                                val detailHistory = result.data.dataDetailHistory
                                val recommendedPlants =
                                    detailHistory.plant.shuffled().take(10)
                                val bullet = "\u2022" // Character for bullet symbol
                                val plantRecommendation =
                                    recommendedPlants.mapIndexed { index, plant ->
                                        if (index == 0) {
                                            "$bullet $plant"
                                        } else {
                                            "$bullet $plant"
                                        }
                                    }.joinToString("\n")
                                Intent(requireContext(), DetailHistoryActivity::class.java).also {
                                    it.putExtra(
                                        DetailHistoryActivity.SOIL_PICT,
                                        detailHistory.image
                                    )
                                    it.putExtra(
                                        DetailHistoryActivity.SOIL_NAME,
                                        detailHistory.soil_Type
                                    )
                                    it.putExtra(
                                        DetailHistoryActivity.PLANT_RECOMM,
                                        plantRecommendation
                                    )
                                    it.putExtra(DetailHistoryActivity.SCAN_DATE, detailHistory.date)
                                    startActivity(it)
                                }
                            }
                            is Result.Error -> {
                            }
                            is Result.Loading -> {
                            }
                        }
                    }
            }
        })
    }

    private fun obtainViewModel(activity: AppCompatActivity): HistoryViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[HistoryViewModel::class.java]
    }
}