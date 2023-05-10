package com.renata.view.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.renata.databinding.ActivityForgotPassBinding

class ForgotPassActivity : AppCompatActivity() {

    private lateinit var forgotPassBinding: ActivityForgotPassBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forgotPassBinding = ActivityForgotPassBinding.inflate(layoutInflater)
        setContentView(forgotPassBinding.root)

        showLoading(false)
        setupView()
        setupAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
        forgotPassBinding.errorEmail.visibility = View.GONE
    }

    private fun setupAnimation() {
        val title =
            ObjectAnimator.ofFloat(forgotPassBinding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val title2 =
            ObjectAnimator.ofFloat(forgotPassBinding.titleTextView2, View.ALPHA, 1f)
                .setDuration(500)
        val email =
            ObjectAnimator.ofFloat(forgotPassBinding.edEmail, View.ALPHA, 1f).setDuration(500)
        val emailInput =
            ObjectAnimator.ofFloat(forgotPassBinding.emailEditTextLayout, View.ALPHA, 1f)
                .setDuration(500)
        val sendOTP =
            ObjectAnimator.ofFloat(forgotPassBinding.sendOTPButton, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                title,
                title2,
                email,
                emailInput,
                sendOTP
            )
            start()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        forgotPassBinding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}