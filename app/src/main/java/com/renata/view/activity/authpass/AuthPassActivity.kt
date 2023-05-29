package com.renata.view.activity.authpass

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
import androidx.lifecycle.ViewModelProvider
import com.renata.R
import com.renata.databinding.ActivityAuthPassBinding
import com.renata.utils.ViewModelFactory
import com.renata.view.activity.forgotpass.ForgotPassViewModel
import com.renata.view.activity.reset.ResetPassActivity
import com.renata.view.activity.reset.ResetPassViewModel

class AuthPassActivity : AppCompatActivity() {

    private lateinit var authPassBinding: ActivityAuthPassBinding
    private lateinit var authPassViewModel: AuthPassViewModel
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authPassBinding = ActivityAuthPassBinding.inflate(layoutInflater)
        setContentView(authPassBinding.root)
        showLoading(false)
        authPassViewModel = obtainViewModel(this as AppCompatActivity)
        email = intent.getStringExtra("email").toString()
        authPassBinding.email.text = email
        setupView()
        setupAnimation()
        authPassBinding.resendOTP.setOnClickListener {
            showAlert(
                getString(R.string.resend_otp_req),
                getString(R.string.resend_otp_res)
            ) {}
        }
        authPassBinding.verifyResetButton?.setOnClickListener {
            showAlert(
                getString(R.string.auth_success),
                getString(R.string.auth_to_reset)
            ) { val moveToReset = Intent(
                this@AuthPassActivity,
                ResetPassActivity::class.java
            )
                moveToReset.putExtra("email", email)
                startActivity(moveToReset)
                finish() }
        }
    }

    private fun obtainViewModel(activity: AppCompatActivity): AuthPassViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[AuthPassViewModel::class.java]
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
            ObjectAnimator.ofFloat(authPassBinding.titleTextView, View.ALPHA, 1f)
                .setDuration(500)
        val title2 =
            ObjectAnimator.ofFloat(authPassBinding.titleTextView2, View.ALPHA, 1f)
                .setDuration(500)
        val email =
            ObjectAnimator.ofFloat(authPassBinding.email, View.ALPHA, 1f).setDuration(500)
        val title3 =
            ObjectAnimator.ofFloat(authPassBinding.titleTextView3, View.ALPHA, 1f)
                .setDuration(500)
        val otp1 =
            ObjectAnimator.ofFloat(authPassBinding.resOtp1, View.ALPHA, 1f).setDuration(500)
        val otp2 =
            ObjectAnimator.ofFloat(authPassBinding.resOtp2, View.ALPHA, 1f).setDuration(500)
        val otp3 =
            ObjectAnimator.ofFloat(authPassBinding.resOtp3, View.ALPHA, 1f).setDuration(500)
        val otp4 =
            ObjectAnimator.ofFloat(authPassBinding.resOtp4, View.ALPHA, 1f).setDuration(500)
        val verifyButton =
            ObjectAnimator.ofFloat(authPassBinding.verifyResetButton, View.ALPHA, 1f)
                .setDuration(500)
        val didnReceive =
            ObjectAnimator.ofFloat(authPassBinding.notReceiveOTP, View.ALPHA, 1f)
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

    private fun moveToReset() {
        startActivity(Intent(this, ResetPassActivity::class.java))
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
        authPassBinding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}