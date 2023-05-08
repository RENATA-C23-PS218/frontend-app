package com.renata.view.activity.authentication

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.renata.databinding.ActivityAuthenticationBinding
import com.renata.view.activity.main.MainActivity

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var authenticationBinding: ActivityAuthenticationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authenticationBinding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(authenticationBinding.root)

        verifyButton()
        showLoading(false)
    }

    private fun verifyButton() {
        authenticationBinding.loginButton.setOnClickListener {
            val moveToHome = Intent(this, MainActivity::class.java)
            startActivity(moveToHome)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        authenticationBinding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}