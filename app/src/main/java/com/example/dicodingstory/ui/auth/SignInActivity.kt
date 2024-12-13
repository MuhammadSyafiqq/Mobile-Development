package com.example.dicodingstory.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.dicodingstory.MainActivity
import com.example.dicodingstory.R
import com.example.dicodingstory.databinding.ActivitySignInBinding
import com.example.dicodingstory.ui.auth.data.SignInBody
import com.example.dicodingstory.ui.auth.repository.AuthRepository
import com.example.dicodingstory.ui.auth.utils.ApiService
import com.example.dicodingstory.ui.auth.view_model.SignInActivityViewModelFactory
import com.example.dicodingstory.ui.auth.view_model.SignInActivityViewModel

@Suppress("DEPRECATION")
class SignInActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySignInBinding
    private val mViewModel: SignInActivityViewModel by viewModels {
        SignInActivityViewModelFactory(AuthRepository(ApiService.getService()), application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        // Apply slide-in animation
        val slideInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_left)
        binding.etEmail.startAnimation(slideInAnimation)
        binding.etPassword.startAnimation(slideInAnimation)

        // Setup click listeners
        setupClickListeners()

        // Setup ViewModel observers
        setupObservers()
    }

    private fun setupClickListeners() {
        binding.apply {
            btnMasuk.setOnClickListener(this@SignInActivity)
            btnDaftar.setOnClickListener(this@SignInActivity)
            btnSignIn.setOnClickListener(this@SignInActivity)
            btnBack.setOnClickListener(this@SignInActivity)
        }
    }

    private fun setupObservers() {
        // Observe loading state
        mViewModel.getIsLoading().observe(this) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }

        // Observe error messages
        mViewModel.getErrorMessage().observe(this) { errors ->
            errors.forEach { (key, value) ->
                when (key) {
                    "email" -> binding.etEmail.error = value
                    "password" -> binding.etPassword.error = value
                    else -> showGeneralErrorDialog(value)
                }
            }
        }

        // Observe user login
        mViewModel.getUser().observe(this) { user ->
            if (user != null) navigateToHome()
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showGeneralErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("LOGIN ERROR")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun submitForm() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        // Validate and attempt login
        mViewModel.loginUser(SignInBody(email, password), this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnMasuk -> submitForm()
            R.id.btnDaftar -> startActivity(Intent(this, SignUpActivity::class.java))
            R.id.btn_back -> navigateToMainActivity()
            R.id.btnSignIn -> startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    @Deprecated("Deprecated in favor of OnBackPressedDispatcher", replaceWith = ReplaceWith("onBackPressedDispatcher"))
    override fun onBackPressed() {
        super.onBackPressed()
        navigateToMainActivity()
    }
}