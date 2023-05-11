package com.renata.view.activity.login

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
import com.renata.databinding.ActivityLoginBinding
import com.renata.utils.emailValidation
import com.renata.utils.passwordValidation
import com.renata.view.activity.main.MainActivity
import com.renata.view.activity.register.RegisterActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var loginBinding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        showLoading(false)
        setupView()
        setupAnimation()
        emailET()
        passwordET()
        registerET()
        loginButton()
    }

    private fun emailET() {
        val myLoginEmailET = loginBinding.edLoginEmail
        myLoginEmailET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val email = loginBinding.edLoginEmail.text.toString()
                if (email.isEmpty()) {
                    loginBinding.errorEmail.visibility = View.GONE
                } else {
                    if (emailValidation(email)) {
                        loginBinding.errorEmail.visibility = View.GONE
                    } else {
                        loginBinding.errorEmail.visibility = View.VISIBLE
                        loginBinding.errorEmail.text = getString(R.string.error_email)
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

    }

    private fun passwordET() {
        val myLoginPasswordET = loginBinding.edLoginPassword
        myLoginPasswordET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val pass = loginBinding.edLoginPassword.text.toString()
                if (pass.isEmpty()) {
                    loginBinding.errorPass.visibility = View.GONE
                } else {
                    if (passwordValidation(pass)) {
                        loginBinding.errorPass.visibility = View.GONE
                    } else {
                        loginBinding.errorPass.visibility = View.VISIBLE
                        loginBinding.errorPass.text = getString(R.string.error_password)
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
        loginBinding.errorPass.visibility = View.GONE
        loginBinding.errorEmail.visibility = View.GONE
    }

    private fun setupAnimation() {
        val title =
            ObjectAnimator.ofFloat(loginBinding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val title2 =
            ObjectAnimator.ofFloat(loginBinding.titleTextView2, View.ALPHA, 1f).setDuration(500)
        val email =
            ObjectAnimator.ofFloat(loginBinding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailInput = ObjectAnimator.ofFloat(loginBinding.emailEditTextLayout, View.ALPHA, 1f)
            .setDuration(500)
        val password =
            ObjectAnimator.ofFloat(loginBinding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordInput =
            ObjectAnimator.ofFloat(loginBinding.passwordEditTextLayout, View.ALPHA, 1f)
                .setDuration(500)
        val login =
            ObjectAnimator.ofFloat(loginBinding.loginButton, View.ALPHA, 1f).setDuration(500)
        val create =
            ObjectAnimator.ofFloat(loginBinding.goToRegister, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                title,
                title2,
                email,
                emailInput,
                password,
                passwordInput,
                login,
                create
            )
            start()
        }
    }

    private fun registerET() {
        loginBinding.createAccount.setOnClickListener {
            val moveToRegister = Intent(this, RegisterActivity::class.java)
            startActivity(moveToRegister)
        }
    }

    private fun loginButton() {
        loginBinding.loginButton.setOnClickListener {
            val email = loginBinding.edLoginEmail.text.toString()
            val password = loginBinding.edLoginPassword.text.toString()
            when {
                email.isEmpty() && password.isEmpty() -> {
                    insertEmail()
                    insertPass()
                }
                email.isEmpty() -> {
                    insertEmail()
                }
                password.isEmpty() -> {
                    insertPass()
                }
                else -> {
                    if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                        if (passwordValidation(password) && emailValidation(email)) {
                            val moveToMain = Intent(this, MainActivity::class.java)
                            startActivity(moveToMain)
                        } else {
                            showAlert(
                                getString(R.string.login_fail),
                                getString(R.string.login_fail_cause1)
                            )
                            { }
                        }
                    } else {
                        showAlert(
                            getString(R.string.login_fail),
                            getString(R.string.login_fail_cause2)
                        )
                        { finish() }
                    }
                }
            }
        }
    }

    private fun insertEmail() {
        loginBinding.errorEmail.visibility = View.VISIBLE
        loginBinding.errorEmail.text = getString(R.string.insert_email)
    }

    private fun insertPass() {
        loginBinding.errorPass.visibility = View.VISIBLE
        loginBinding.errorPass.text = getString(R.string.insert_pass)
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
        loginBinding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}