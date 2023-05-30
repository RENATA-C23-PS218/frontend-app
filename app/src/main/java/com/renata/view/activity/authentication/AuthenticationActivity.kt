package com.renata.view.activity.authentication

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
import com.renata.databinding.ActivityAuthenticationBinding
import com.renata.utils.ViewModelFactory
import com.renata.view.activity.login.LoginActivity

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var authenticationBinding: ActivityAuthenticationBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var otpInputViews: Array<TextView>
    private lateinit var email: String
    private lateinit var id: String
    private lateinit var otp: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authenticationBinding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(authenticationBinding.root)
        authViewModel = obtainViewModel(this as AppCompatActivity)
        otpInputViews = arrayOf(
            authenticationBinding.authOtp1,
            authenticationBinding.authOtp2,
            authenticationBinding.authOtp3,
            authenticationBinding.authOtp4
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
                            otp = otpInputViews.joinToString("") { it.text.toString() }
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {}
                })
            }
        }
        email = intent.getStringExtra("email").toString()
        id = intent.getStringExtra("id").toString()
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
        authenticationBinding.verifyButton.setOnClickListener { authProcess(id, otp) }
    }

    private fun obtainViewModel(activity: AppCompatActivity): AuthViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[AuthViewModel::class.java]
    }

    private fun authProcess(id: String, otp: String) {
//        val authOtp1 = authenticationBinding.authOtp1.text.toString()
//        val authOtp2 = authenticationBinding.authOtp2.text.toString()
//        val authOtp3 = authenticationBinding.authOtp3.text.toString()
//        val authOtp4 = authenticationBinding.authOtp4.text.toString()
//        val combinedText = authOtp1 + authOtp2 + authOtp3 + authOtp4
        authViewModel.userAuthentication(id, otp)
            .observe(this@AuthenticationActivity) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            showLoading(true)
                        }
                        is Result.Error -> {
                            showLoading(false)
                            showAlert(
                                getString(R.string.regis_fail),
                                getString(R.string.regis_fail_cause2)
                            ) { }
                        }
                        is Result.Success -> {
                            showLoading(false)
                            showAlert(
                                getString(R.string.auth_success),
                                getString(R.string.auth_to_login)
                            ) { moveToLogin() }
                        }
                    }
                }
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