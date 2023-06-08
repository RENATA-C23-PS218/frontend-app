package com.renata.view.activity.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.renata.R
import com.renata.data.user.login.LoginPreferences
import com.renata.data.user.login.LoginResult
import com.renata.view.activity.login.LoginActivity
import com.renata.view.activity.main.NavigationActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var loginPreference: LoginPreferences
    private lateinit var loginModel: LoginResult

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginPreference = LoginPreferences(this)
        loginModel = loginPreference.getUser()
        lifecycleScope.launch {
            delay(splashTime)
            withContext(Dispatchers.Main) {
                if (loginModel.id != null && loginModel.email != null && loginModel.token != null) {
                    val intentToMain =
                        Intent(this@SplashScreenActivity, NavigationActivity::class.java)
                    startActivity(intentToMain)
                    overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom)
                    finish()
                } else {
                    val intentToLogin = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                    startActivity(intentToLogin)
                    overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom)
                    finish()
                }
            }
        }
    }

    companion object {
        const val splashTime: Long = 1000
    }
}