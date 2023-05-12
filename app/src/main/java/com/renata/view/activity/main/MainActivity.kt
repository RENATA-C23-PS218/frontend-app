package com.renata.view.activity.main

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.renata.R
import com.renata.databinding.ActivityMainBinding
import com.renata.utils.ViewModelFactory
import com.renata.view.fragment.account.AccountFragment
import com.renata.view.fragment.history.HistoryFragment
import com.renata.view.fragment.scan.ScanFragment

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private var activeNavItem: Int = R.id.History

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        mainViewModel = obtainViewModel(this as AppCompatActivity)

        val colorPrimary = TypedValue()
        theme.resolveAttribute(android.R.attr.colorPrimary, colorPrimary, true)
        val activeColor = colorPrimary.data
        val inactiveColor = ContextCompat.getColor(this, R.color.grey_bottomNav)

        val colorStateList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(-android.R.attr.state_checked)
            ),
            intArrayOf(activeColor, inactiveColor)
        )
        mainBinding.bottomNavigationView.itemTextColor = colorStateList
        mainBinding.bottomNavigationView.itemIconTintList = colorStateList
        mainBinding.bottomNavigationView.background = null
        mainBinding.bottomNavigationView.menu.getItem(1).isEnabled = false
        mainBinding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.History -> {
                    replaceFragment(HistoryFragment())
                    activeNavItem = R.id.History
                    return@setOnItemSelectedListener true
                }
                R.id.Profile -> {
                    replaceFragment(AccountFragment())
                    activeNavItem = R.id.Profile
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
        replaceFragment(HistoryFragment())
        mainBinding.bottomNavigationView.selectedItemId = R.id.History
        mainBinding.scanFab.setOnClickListener { scannerButton() }
    }

    private fun obtainViewModel(activity: AppCompatActivity): MainViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[MainViewModel::class.java]
    }

    override fun onBackPressed() {
        val currentFragment =
            supportFragmentManager.findFragmentById(mainBinding.layoutContainer.id)
        when (currentFragment) {
            is HistoryFragment -> {
                finishAffinity()
            }
            else -> {
                replaceFragment(HistoryFragment())
                mainBinding.bottomNavigationView.selectedItemId = R.id.History
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(
                R.anim.slide_in,
                R.anim.slide_out
            )
            replace(mainBinding.layoutContainer.id, fragment, fragment.javaClass.simpleName)
            addToBackStack(null)
            commit()
        }
    }

    private fun scannerButton() {
        replaceFragment(ScanFragment())
        mainBinding.bottomNavigationView.selectedItemId = R.id.placeholder
    }

}