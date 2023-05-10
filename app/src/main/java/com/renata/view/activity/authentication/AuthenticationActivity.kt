package com.renata.view.activity.authentication

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.renata.R
import com.renata.databinding.ActivityAuthenticationBinding
import com.renata.view.activity.login.LoginActivity

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var authenticationBinding: ActivityAuthenticationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authenticationBinding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(authenticationBinding.root)

        showLoading(false)
        verifyButton()
        resendOTP()
    }

    private fun resendOTP() {
        authenticationBinding.resendOTP.setOnClickListener {
            showLoading(false)
            showAlert(
                getString(R.string.resend_otp_req),
                getString(R.string.resend_otp_res)
            )
            { }
        }
    }

    private fun verifyButton() {
        authenticationBinding.verifyButton.setOnClickListener {
            showLoading(false)
            showAlert(
                getString(R.string.auth_success),
                getString(R.string.auth_to_login)
            )
            { moveToLogin() }
        }
    }

    private fun moveToLogin() {
        val moveToLogin = Intent(this, LoginActivity::class.java)
        startActivity(moveToLogin)
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