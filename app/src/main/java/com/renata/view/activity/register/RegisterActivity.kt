package com.renata.view.activity.register

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
import com.renata.databinding.ActivityRegisterBinding
import com.renata.utils.ViewModelFactory
import com.renata.utils.emailValidation
import com.renata.utils.passwordValidation
import com.renata.view.activity.authentication.AuthenticationActivity
import com.renata.view.activity.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerBinding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(registerBinding.root)
        registerViewModel = obtainViewModel(this as AppCompatActivity)
        showLoading(false)
        setupView()
        setupAnimation()
        emailET()
        passwordET()
        confirmPasswordET()
        registerBinding.loginAccount.setOnClickListener { loginET() }
        registerBinding.registerButton.setOnClickListener { registerButton() }
    }

    private fun obtainViewModel(activity: AppCompatActivity): RegisterViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[RegisterViewModel::class.java]
    }

    private fun confirmPasswordET() {
        val myRegisterConPassET = registerBinding.edRegisterConfirmPassword
        myRegisterConPassET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val password = registerBinding.edRegisterPassword.text.toString()
                val conPass = registerBinding.edRegisterConfirmPassword.text.toString()
                if (conPass.isEmpty()) {
                    registerBinding.errorConfirmPass.visibility = View.GONE
                } else {
                    if (conPass == password) {
                        registerBinding.errorConfirmPass.visibility = View.GONE
                    } else {
                        registerBinding.errorConfirmPass.visibility = View.VISIBLE
                        registerBinding.errorConfirmPass.text =
                            getString(R.string.error_confirm_password)
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }

    private fun emailET() {
        val myRegisterEmailET = registerBinding.edRegisterEmail
        myRegisterEmailET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val email = registerBinding.edRegisterEmail.text.toString()
                if (email.isEmpty()) {
                    registerBinding.errorEmail.visibility = View.GONE
                } else {
                    if (emailValidation(email)) {
                        registerBinding.errorEmail.visibility = View.GONE
                    } else {
                        registerBinding.errorEmail.visibility = View.VISIBLE
                        registerBinding.errorEmail.text = getString(R.string.error_email)
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }

    private fun passwordET() {
        val myRegisterPasswordET = registerBinding.edRegisterPassword
        myRegisterPasswordET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val pass = registerBinding.edRegisterPassword.text.toString()
                if (pass.isEmpty()) {
                    registerBinding.errorPass.visibility = View.GONE
                } else {
                    if (passwordValidation(pass)) {
                        registerBinding.errorPass.visibility = View.GONE
                    } else {
                        registerBinding.errorPass.visibility = View.VISIBLE
                        registerBinding.errorPass.text = getString(R.string.error_password)
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }

    private fun registerButton() {
        showLoading(true)
        val confirmPass = registerBinding.edRegisterConfirmPassword.text.toString()
        val email = registerBinding.edRegisterEmail.text.toString()
        val password = registerBinding.edRegisterPassword.text.toString()
        when {
            confirmPass.isEmpty() && email.isEmpty() && password.isEmpty() -> {
                showLoading(false)
                insertEmail()
                insertPass()
                insertConfirmPAss()
            }
            confirmPass.isEmpty() -> {
                showLoading(false)
                insertConfirmPAss()
            }
            email.isEmpty() -> {
                showLoading(false)
                insertEmail()
            }
            password.isEmpty() -> {
                showLoading(false)
                insertPass()
            }
            else -> {
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(
                        confirmPass
                    )
                ) {
                    if (passwordValidation(password) && emailValidation(email)) {
                        if (confirmPass == password) {
                            showLoading(false)
                            register(email, password, confirmPass)
//                            showAlert(
//                                getString(R.string.regis_success),
//                                getString(R.string.regis_to_auth)
//                            ) {
//                                val moveToAuth = Intent(
//                                    this@RegisterActivity,
//                                    AuthenticationActivity::class.java
//                                )
//                                moveToAuth.putExtra("email", email)
//                                startActivity(moveToAuth)
//                                finish()
//                            }
                        } else {
                            showLoading(false)
                            showAlert(
                                getString(R.string.regis_fail),
                                getString(R.string.regis_fail_cause3)
                            ) { }
                        }
                    } else {
                        showLoading(false)
                        showAlert(
                            getString(R.string.regis_fail),
                            getString(R.string.regis_fail_cause2)
                        ) { }
                    }
                } else {
                    showLoading(false)
                    showAlert(
                        getString(R.string.regis_fail),
                        getString(R.string.regis_fail_cause1)
                    ) { finish() }
                }
            }
        }
    }

    private fun register(email: String, password: String, confirmPassword: String) {
        registerViewModel.userRegister(email, password, confirmPassword)
            .observe(this@RegisterActivity) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            showLoading(true)
                        }
                        is Result.Error -> {
                            showLoading(false)
                            val errorMessage = result.data
                            showAlert(
                                getString(R.string.regis_fail),
                                errorMessage
                            ) { }
                        }
                        is Result.Success -> {
                            showLoading(false)
                            val idValue = result.data.data.id
                            showAlert(
                                getString(R.string.regis_success),
                                getString(R.string.regis_to_auth)
                            ) {
                                val moveToAuth = Intent(
                                    this@RegisterActivity,
                                    AuthenticationActivity::class.java
                                )
                                moveToAuth.putExtra("email", email)
                                moveToAuth.putExtra("id", idValue)
                                startActivity(moveToAuth)
                                finish()
                            }
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
        registerBinding.errorConfirmPass.visibility = View.GONE
        registerBinding.errorEmail.visibility = View.GONE
        registerBinding.errorPass.visibility = View.GONE
    }

    private fun setupAnimation() {
        val title =
            ObjectAnimator.ofFloat(registerBinding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val title2 =
            ObjectAnimator.ofFloat(registerBinding.titleTextView2, View.ALPHA, 1f).setDuration(500)
        val confirmPass =
            ObjectAnimator.ofFloat(registerBinding.confirmPasswordTextView, View.ALPHA, 1f)
                .setDuration(500)
        val confirmPassInput =
            ObjectAnimator.ofFloat(registerBinding.confirmPasswordEditTextLayout, View.ALPHA, 1f)
                .setDuration(500)
        val email =
            ObjectAnimator.ofFloat(registerBinding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailInput = ObjectAnimator.ofFloat(registerBinding.emailEditTextLayout, View.ALPHA, 1f)
            .setDuration(500)
        val password =
            ObjectAnimator.ofFloat(registerBinding.passwordTextView, View.ALPHA, 1f)
                .setDuration(500)
        val passwordInput =
            ObjectAnimator.ofFloat(registerBinding.passwordEditTextLayout, View.ALPHA, 1f)
                .setDuration(500)
        val register =
            ObjectAnimator.ofFloat(registerBinding.registerButton, View.ALPHA, 1f).setDuration(500)
        val login =
            ObjectAnimator.ofFloat(registerBinding.goToLogin, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                title,
                title2,
                email,
                emailInput,
                password,
                passwordInput,
                confirmPass,
                confirmPassInput,
                register,
                login
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

    private fun insertEmail() {
        registerBinding.errorEmail.visibility = View.VISIBLE
        registerBinding.errorEmail.text = getString(R.string.insert_email)
    }

    private fun insertPass() {
        registerBinding.errorPass.visibility = View.VISIBLE
        registerBinding.errorPass.text = getString(R.string.insert_pass)
    }

    private fun insertConfirmPAss() {
        registerBinding.errorConfirmPass.visibility = View.VISIBLE
        registerBinding.errorConfirmPass.text = getString(R.string.insert_pass)
    }

    private fun loginET() {
        val moveToLogin = Intent(this@RegisterActivity, LoginActivity::class.java)
        startActivity(moveToLogin)
        overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom)
        registerBinding.edRegisterEmail.text = null
        registerBinding.edRegisterPassword.text = null
        registerBinding.edRegisterConfirmPassword.text = null
        finishAffinity()
    }

    private fun showLoading(isLoading: Boolean) {
        registerBinding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}