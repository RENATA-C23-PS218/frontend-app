package com.renata.view.activity.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.renata.R
import com.renata.databinding.ActivityLoginBinding
import com.renata.utils.emailValidation
import com.renata.utils.passwordValidation
import com.renata.view.activity.authentication.AuthenticationActivity
import com.renata.view.activity.main.MainActivity
import com.renata.view.activity.register.RegisterActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        showLoading(false)
        googleLoginConfig()
        setupView()
        setupAnimation()
        emailET()
        passwordET()
        registerET()
        loginButton()
        loginGoogle()
    }

    private fun loginGoogle() {
        loginBinding.signInButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            resultLauncher.launch(signInIntent)
        }
    }

    private fun googleLoginConfig() {
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = Firebase.auth
    }

    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
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
//            val moveToHome = Intent(this, HomeActivity::class.java)
//            startActivity(moveToHome)
            val moveToAuthentication = Intent(this, AuthenticationActivity::class.java)
            startActivity(moveToAuthentication)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        loginBinding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private const val TAG = "LoginActivity"
    }

}