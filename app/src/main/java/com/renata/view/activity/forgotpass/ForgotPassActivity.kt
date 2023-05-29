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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.renata.R
import com.renata.data.Result
import com.renata.databinding.ActivityForgotPassBinding
import com.renata.utils.ViewModelFactory
import com.renata.utils.emailValidation
import com.renata.view.activity.authentication.AuthenticationActivity
import com.renata.view.activity.authpass.AuthPassActivity
import com.renata.view.activity.main.NavigationActivity

class ForgotPassActivity : AppCompatActivity() {

    private lateinit var forgotPassBinding: ActivityForgotPassBinding
    private lateinit var forgotPassViewModel: ForgotPassViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forgotPassBinding = ActivityForgotPassBinding.inflate(layoutInflater)
        setContentView(forgotPassBinding.root)
        showLoading(false)
        forgotPassViewModel = obtainViewModel(this as AppCompatActivity)
        setupView()
        setupAnimation()
        emailET()
        forgotPassBinding.sendOTPButton.setOnClickListener { sendOtp() }
    }

    private fun obtainViewModel(activity: AppCompatActivity): ForgotPassViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[ForgotPassViewModel::class.java]
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
//                        sendEmail(email)
//                        val moveToOtp = Intent(this, AuthPassActivity::class.java)
//                        startActivity(moveToOtp)
//                        overridePendingTransition(
//                            R.anim.slide_out_bottom,
//                            R.anim.slide_in_bottom
//                        )
                        val moveToAuth = Intent(
                            this@ForgotPassActivity,
                            AuthPassActivity::class.java
                        )
                        moveToAuth.putExtra("email", email)
                        startActivity(moveToAuth)
                        finish()
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

    private fun sendEmail(email: String) {
        forgotPassViewModel.userForgotPass(email).observe(this@ForgotPassActivity) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Error -> {
                        showLoading(false)
                        showToast("login Method")
                        showAlert(
                            "Send OTP Failed",
                            "Make sure Email are filled in correctly"
                        )
                        {}
                    }
                    is Result.Success -> {
                        showLoading(false)
                        val moveToAuth = Intent(
                            this@ForgotPassActivity,
                            AuthPassActivity::class.java
                        )
                        moveToAuth.putExtra("email", email)
                        startActivity(moveToAuth)
                        finish()
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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