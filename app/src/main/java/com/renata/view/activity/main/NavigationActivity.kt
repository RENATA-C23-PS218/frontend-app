package com.renata.view.activity.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.renata.R
import com.renata.databinding.ActivityNavigationBinding
import com.renata.view.activity.scan.ScanActivity

class NavigationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNavigationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_navigation)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_history, R.id.navigation_scan, R.id.navigation_account
            )
        )
        navView.setupWithNavController(navController)
        binding.scanFab.setOnClickListener {
            val intent = Intent(this, ScanActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        val navController = findNavController(R.id.nav_host_fragment_activity_navigation)
        when (navController.currentDestination?.id) {
            R.id.navigation_history -> {
                finishAffinity()
            }
            else -> {
                val historyFragment = navController.graph.findNode(R.id.navigation_history)
                navController.popBackStack(historyFragment!!.id, false)
            }
        }
    }

}