package com.renata.view.activity.authpass

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.renata.R
import com.renata.data.Result
import com.renata.databinding.ActivityAuthPassBinding
import com.renata.utils.ViewModelFactory
import com.renata.view.activity.reset.ResetPassActivity

class AuthPassActivity : AppCompatActivity() {
    private lateinit var authPassBinding: ActivityAuthPassBinding
    private lateinit var authPassViewModel: AuthPassViewModel
    private lateinit var otpInputViews: Array<TextView>
    private lateinit var email: String
    private var otpInt: Int? = null

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
        otpInputViews = arrayOf(
            authPassBinding.resOtp1,
            authPassBinding.resOtp2,
            authPassBinding.resOtp3,
            authPassBinding.resOtp4
        )
        otpInputViews.forEachIndexed { index, textView ->
            textView.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1 && index < otpInputViews.lastIndex) {
                        otpInputViews[index + 1].requestFocus()
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })
            if (index == otpInputViews.lastIndex) {
                textView.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        if (s?.length == 1) {
                            val otp = otpInputViews.joinToString("") { it.text.toString() }
                            otpInt = otp.toIntOrNull()
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {}
                })
            }
        }
        authPassBinding.resendOTP.setOnClickListener {
            resendOtpReset(email)
        }
        authPassBinding.verifyResetButton.setOnClickListener {
            verifyReset(email, otpInt!!)
        }
    }

    private fun resendOtpReset(email: String) {
        authPassViewModel.resendOTPReset(email).observe(this@AuthPassActivity) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Error -> {
                        showLoading(false)
                        showAlert(
                            "Send OTP Failed",
                            "Make sure Email are filled in correctly"
                        ) { otpClear() }
                    }
                    is Result.Success -> {
                        showLoading(false)
                        showAlert(
                            getString(R.string.resend_otp_req),
                            getString(R.string.resend_otp_res)
                        ) { otpClear() }
                    }
                }
            }
        }
    }

    private fun verifyReset(email: String, otp: Int) {
        authPassViewModel.resetAuthentication(email, otp)
            .observe(this@AuthPassActivity) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            showLoading(true)
                        }
                        is Result.Error -> {
                            showLoading(false)
                            val errorMessage = result.data
                            showAlert(
                                "Authentication Fail",
                                errorMessage
                            ) { otpClear() }
                        }
                        is Result.Success -> {
                            showLoading(false)
                            showAlert(
                                getString(R.string.auth_success),
                                getString(R.string.auth_to_reset)
                            ) {
                                val moveToReset = Intent(
                                    this@AuthPassActivity,
                                    ResetPassActivity::class.java
                                )
                                moveToReset.putExtra("email", email)
                                startActivity(moveToReset)
                                finish()
                            }
                        }
                    }
                }
            }
    }

    private fun otpClear() {
        authPassBinding.resOtp1.setText("")
        authPassBinding.resOtp2.setText("")
        authPassBinding.resOtp3.setText("")
        authPassBinding.resOtp4.setText("")
        authPassBinding.resOtp1.requestFocus()
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

    private fun showAlert(
        title: String,
        message: String,
        positiveAction: (dialog: DialogInterface) -> Unit
    ) {
        AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("OK") { dialog, _ -> positiveAction.invoke(dialog) }
            setCancelable(false)
            create()
            show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        authPassBinding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}