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
import com.renata.R
import com.renata.databinding.ActivityRegisterBinding
import com.renata.utils.emailValidation
import com.renata.utils.passwordValidation
import com.renata.view.activity.authentication.AuthenticationActivity
import com.renata.view.activity.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerBinding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(registerBinding.root)

        showLoading(false)
        setupView()
        setupAnimation()
        nameET()
        emailET()
        passwordET()
        loginET()
        registerButton()
    }

    private fun nameET() {
        val myRegisterNameET = registerBinding.edRegisterName
        myRegisterNameET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val name = registerBinding.edRegisterName.text.toString()
                if (name.isNotEmpty()) {
                    registerBinding.errorName.visibility = View.GONE
                } else {
                    registerBinding.errorName.visibility = View.VISIBLE
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

    private fun registerButton() {
        registerBinding.registerButton.setOnClickListener {
            showLoading(true)
            val name = registerBinding.edRegisterName.text.toString()
            val email = registerBinding.edRegisterEmail.text.toString()
            val password = registerBinding.edRegisterPassword.text.toString()
            when {
                name.isEmpty() && email.isEmpty() && password.isEmpty() -> {
                    showLoading(false)
                    insertName()
                    insertEmail()
                    insertPass()
                }
                name.isEmpty() -> {
                    showLoading(false)
                    insertName()
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
                            name
                        )
                    ) {
                        if (passwordValidation(password) && emailValidation(email)) {
                            showLoading(false)
                            showAlert(
                                getString(R.string.regis_success),
                                getString(R.string.regis_to_auth)
                            )
                            { login() }
                        } else {
                            showLoading(false)
                            showAlert(
                                getString(R.string.regis_fail),
                                getString(R.string.regis_fail_cause2)
                            )
                            { }
                        }
                    } else {
                        showLoading(false)
                        showAlert(
                            getString(R.string.regis_fail),
                            getString(R.string.regis_fail_cause1)
                        )
                        { finish() }
                    }
                }
            }
        }
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
        registerBinding.errorName.visibility = View.GONE
        registerBinding.errorEmail.visibility = View.GONE
        registerBinding.errorPass.visibility = View.GONE
    }

    private fun setupAnimation() {
        val title =
            ObjectAnimator.ofFloat(registerBinding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val title2 =
            ObjectAnimator.ofFloat(registerBinding.titleTextView2, View.ALPHA, 1f).setDuration(500)
        val name =
            ObjectAnimator.ofFloat(registerBinding.nameTextView, View.ALPHA, 1f).setDuration(500)
        val nameInput =
            ObjectAnimator.ofFloat(registerBinding.nameEditTextLayout, View.ALPHA, 1f)
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
                name,
                nameInput,
                email,
                emailInput,
                password,
                passwordInput,
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

    private fun insertName() {
        registerBinding.errorName.visibility = View.VISIBLE
        registerBinding.errorName.text = getString(R.string.insert_name)
    }

    private fun login() {
        val moveToAuth = Intent(this@RegisterActivity, AuthenticationActivity::class.java)
        startActivity(moveToAuth)
        finish()
    }

    private fun loginET() {
        registerBinding.loginAccount.setOnClickListener {
            val moveToLogin = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(moveToLogin)
            finish()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        registerBinding.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}