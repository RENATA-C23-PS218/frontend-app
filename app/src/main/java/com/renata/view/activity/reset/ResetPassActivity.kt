package com.renata.view.activity.reset

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
import androidx.lifecycle.ViewModelProvider
import com.renata.R
import com.renata.data.Result
import com.renata.databinding.ActivityResetPassBinding
import com.renata.utils.ViewModelFactory
import com.renata.utils.passwordValidation
import com.renata.view.activity.login.LoginActivity

class ResetPassActivity : AppCompatActivity() {

    private lateinit var resetPassBinding: ActivityResetPassBinding
    private lateinit var resetPassViewModel: ResetPassViewModel
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resetPassBinding = ActivityResetPassBinding.inflate(layoutInflater)
        setContentView(resetPassBinding.root)
        showLoading(false)
        email = intent.getStringExtra("email").toString()
        resetPassViewModel = obtainViewModel(this as AppCompatActivity)
        setupView()
        setupAnimation()
        passwordET()
        confirmPasswordET()
        resetPassBinding.confirmButton.setOnClickListener { confirmPass() }
    }

    private fun obtainViewModel(activity: AppCompatActivity): ResetPassViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[ResetPassViewModel::class.java]
    }

    private fun confirmPasswordET() {
        val myRegisterConPassET = resetPassBinding.edResetConfirmPassword
        myRegisterConPassET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val password = resetPassBinding.edResetPassword.text.toString()
                val conPass = resetPassBinding.edResetConfirmPassword.text.toString()
                if (conPass.isEmpty()) {
                    resetPassBinding.errorConfirmPass.visibility = View.GONE
                } else {
                    if (conPass == password) {
                        resetPassBinding.errorConfirmPass.visibility = View.GONE
                    } else {
                        resetPassBinding.errorConfirmPass.visibility = View.VISIBLE
                        resetPassBinding.errorConfirmPass.text =
                            getString(R.string.error_confirm_password)
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }

    private fun passwordET() {
        val myRegisterPasswordET = resetPassBinding.edResetPassword
        myRegisterPasswordET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val pass = resetPassBinding.edResetPassword.text.toString()
                if (pass.isEmpty()) {
                    resetPassBinding.errorPass.visibility = View.GONE
                } else {
                    if (passwordValidation(pass)) {
                        resetPassBinding.errorPass.visibility = View.GONE
                    } else {
                        resetPassBinding.errorPass.visibility = View.VISIBLE
                        resetPassBinding.errorPass.text = getString(R.string.error_password)
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }

    private fun confirmPass() {
        showLoading(true)
        val confirmPass = resetPassBinding.edResetConfirmPassword.text.toString()
        val password = resetPassBinding.edResetPassword.text.toString()
        when {
            confirmPass.isEmpty() && password.isEmpty() -> {
                showLoading(false)
                insertPass()
                insertConfirmPAss()
            }
            confirmPass.isEmpty() -> {
                showLoading(false)
                insertConfirmPAss()
            }
            password.isEmpty() -> {
                showLoading(false)
                insertPass()
            }
            else -> {
                if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPass)
                ) {
                    if (passwordValidation(password) && passwordValidation(confirmPass)) {
                        if (confirmPass == password) {
                            showLoading(false)
                            resetPassword(email, password, confirmPass)
//                            showAlert(
//                                getString(R.string.reset_success),
//                                getString(R.string.reset_to_login)
//                            ) { login() }
                        } else {
                            showLoading(false)
                            showAlert(
                                getString(R.string.reset_fail),
                                getString(R.string.reset_fail_cause3)
                            ) { }
                        }
                    } else {
                        showLoading(false)
                        showAlert(
                            getString(R.string.reset_fail),
                            getString(R.string.reset_fail_cause2)
                        ) { }
                    }
                } else {
                    showLoading(false)
                    showAlert(
                        getString(R.string.reset_fail),
                        getString(R.string.reset_fail_cause1)
                    ) { finish() }
                }
            }
        }
    }

    private fun resetPassword(email: String, password: String, confirmPassword: String) {
        resetPassViewModel.userResetPass(email, password, confirmPassword)
            .observe(this@ResetPassActivity) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            showLoading(true)
                        }
                        is Result.Error -> {
                            showLoading(false)
                            showAlert(
                                getString(R.string.reset_fail),
                                getString(R.string.reset_fail_cause3)
                            ) { }
                        }
                        is Result.Success -> {
                            showLoading(false)
                            showAlert(
                                getString(R.string.reset_success),
                                getString(R.string.reset_to_login)
                            ) { login() }
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
        resetPassBinding.errorConfirmPass.visibility = View.GONE
        resetPassBinding.errorPass.visibility = View.GONE
    }

    private fun setupAnimation() {
        val title =
            ObjectAnimator.ofFloat(resetPassBinding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val title2 =
            ObjectAnimator.ofFloat(resetPassBinding.titleTextView2, View.ALPHA, 1f).setDuration(500)
        val confirmPass =
            ObjectAnimator.ofFloat(resetPassBinding.confirmPasswordTextView, View.ALPHA, 1f)
                .setDuration(500)
        val confirmPassInput =
            ObjectAnimator.ofFloat(resetPassBinding.confirmPasswordEditTextLayout, View.ALPHA, 1f)
                .setDuration(500)
        val password =
            ObjectAnimator.ofFloat(resetPassBinding.passwordTextView, View.ALPHA, 1f)
                .setDuration(500)
        val passwordInput =
            ObjectAnimator.ofFloat(resetPassBinding.passwordEditTextLayout, View.ALPHA, 1f)
                .setDuration(500)
        val register =
            ObjectAnimator.ofFloat(resetPassBinding.confirmButton, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                title,
                title2,
                password,
                passwordInput,
                confirmPass,
                confirmPassInput,
                register
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
            setPositiveButton("OK") { dialog, _ ->
                positiveAction.invoke(dialog)
            }
            setCancelable(false)
            create()
            show()
        }
    }

    private fun insertPass() {
        resetPassBinding.errorPass.visibility = View.VISIBLE
        resetPassBinding.errorPass.text = getString(R.string.insert_pass)
    }

    private fun insertConfirmPAss() {
        resetPassBinding.errorConfirmPass.visibility = View.VISIBLE
        resetPassBinding.errorConfirmPass.text = getString(R.string.insert_pass)
    }

    private fun login() {
        val moveToLogin = Intent(this@ResetPassActivity, LoginActivity::class.java)
        startActivity(moveToLogin)
        overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom)
        resetPassBinding.edResetPassword.text = null
        resetPassBinding.edResetConfirmPassword.text = null
        finishAffinity()
    }

    private fun showLoading(isLoading: Boolean) {
        resetPassBinding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}