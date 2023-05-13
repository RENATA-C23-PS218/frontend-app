package com.renata.view.activity.forgotpass

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.renata.R
import com.renata.databinding.ActivityForgotPassBinding
import com.renata.utils.emailValidation
import com.renata.view.activity.authpass.AuthPassActivity

class ForgotPassActivity : AppCompatActivity() {

    private lateinit var forgotPassBinding: ActivityForgotPassBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forgotPassBinding = ActivityForgotPassBinding.inflate(layoutInflater)
        setContentView(forgotPassBinding.root)

        showLoading(false)
        setupView()
        setupAnimation()
        emailET()
        forgotPassBinding.sendOTPButton.setOnClickListener { sendOtp() }
    }

    private fun insertEmail() {
        forgotPassBinding.errorEmail.visibility = View.VISIBLE
        forgotPassBinding.errorEmail.text = getString(R.string.insert_email)
    }

    private fun sendOtp() {
        val email = forgotPassBinding.edEmailForgot.text.toString()
        when {
            email.isEmpty() -> {
                insertEmail()
            }
            else -> {
                if (!TextUtils.isEmpty(email)) {
                    if (emailValidation(email)) {
                        val moveToOtp = Intent(this, AuthPassActivity::class.java)
                        startActivity(moveToOtp)
                        overridePendingTransition(
                            R.anim.slide_out_bottom,
                            R.anim.slide_in_bottom
                        )
                    } else {
                        showAlert(
                            getString(R.string.send_otp_fail),
                            getString(R.string.send_otp_fail_cause1)
                        )
                        { }
                    }
                } else {
                    showAlert(
                        getString(R.string.send_otp_fail), getString(R.string.send_otp_fail_cause2)
                    )
                    { finish() }
                }
            }
        }
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

    private fun emailET() {
        val myLoginEmailET = forgotPassBinding.edEmailForgot
        myLoginEmailET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val email = forgotPassBinding.edEmailForgot.text.toString()
                if (email.isEmpty()) {
                    forgotPassBinding.errorEmail.visibility = View.GONE
                } else {
                    if (emailValidation(email)) {
                        forgotPassBinding.errorEmail.visibility = View.GONE
                    } else {
                        forgotPassBinding.errorEmail.visibility = View.VISIBLE
                        forgotPassBinding.errorEmail.text = getString(R.string.error_email)
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

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
            ObjectAnimator.ofFloat(forgotPassBinding.edEmailForgot, View.ALPHA, 1f).setDuration(500)
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