package com.renata.view.activity.authentication

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.renata.R
import com.renata.databinding.ActivityAuthenticationBinding
import com.renata.view.activity.login.LoginActivity

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var authenticationBinding: ActivityAuthenticationBinding
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authenticationBinding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(authenticationBinding.root)
        email = intent.getStringExtra("email").toString()
        authenticationBinding.email.text = email
        showLoading(false)
        setupView()
        setupAnimation()
        authenticationBinding.resendOTP.setOnClickListener {
            showAlert(
                getString(R.string.resend_otp_req),
                getString(R.string.resend_otp_res)
            ) {}
        }
        authenticationBinding.verifyButton.setOnClickListener {
            showAlert(
                getString(R.string.auth_success),
                getString(R.string.auth_to_login)
            ) { moveToLogin() }
        }
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
    }

    private fun setupAnimation() {
        val title =
            ObjectAnimator.ofFloat(authenticationBinding.titleTextView, View.ALPHA, 1f)
                .setDuration(500)
        val title2 =
            ObjectAnimator.ofFloat(authenticationBinding.titleTextView2, View.ALPHA, 1f)
                .setDuration(500)
        val email =
            ObjectAnimator.ofFloat(authenticationBinding.email, View.ALPHA, 1f).setDuration(500)
        val title3 =
            ObjectAnimator.ofFloat(authenticationBinding.titleTextView3, View.ALPHA, 1f)
                .setDuration(500)
        val otp1 =
            ObjectAnimator.ofFloat(authenticationBinding.authOtp1, View.ALPHA, 1f).setDuration(500)
        val otp2 =
            ObjectAnimator.ofFloat(authenticationBinding.authOtp2, View.ALPHA, 1f).setDuration(500)
        val otp3 =
            ObjectAnimator.ofFloat(authenticationBinding.authOtp3, View.ALPHA, 1f).setDuration(500)
        val otp4 =
            ObjectAnimator.ofFloat(authenticationBinding.authOtp4, View.ALPHA, 1f).setDuration(500)
        val verifyButton =
            ObjectAnimator.ofFloat(authenticationBinding.verifyButton, View.ALPHA, 1f)
                .setDuration(500)
        val didnReceive =
            ObjectAnimator.ofFloat(authenticationBinding.notReceiveOTP, View.ALPHA, 1f)
                .setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                title,
                title2,
                email,
                title3,
                otp1,
                otp2,
                otp3,
                otp4,
                verifyButton,
                didnReceive
            )
            start()
        }
    }

    private fun moveToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun showAlert(
        title: String,
        message: String,
        positiveAction: (dialog: DialogInterface) -> Unit
    ) {
        AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("OK") { dialog, _ ->
                positiveAction.invoke(dialog)
            }
            setCancelable(false)
            create()
            show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        authenticationBinding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}