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
import com.example.dicodingstory.databinding.ActivitySignUpBinding
import com.example.dicodingstory.ui.auth.data.SignUpBody
import com.example.dicodingstory.ui.auth.repository.AuthRepository
import com.example.dicodingstory.ui.auth.utils.ApiService
import com.example.dicodingstory.ui.auth.view_model.SignUpActivityViewModel
import com.example.dicodingstory.ui.auth.view_model.SignUpActivityViewModelFactory

@Suppress("DEPRECATION")
class SignUpActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySignUpBinding
    private val mViewModel: SignUpActivityViewModel by viewModels {
        SignUpActivityViewModelFactory(AuthRepository(ApiService.getService()), application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        // Apply slide-in animation
        val slideInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_left)
        binding.etEmail.startAnimation(slideInAnimation)
        binding.etName.startAnimation(slideInAnimation)
        binding.etPassword.startAnimation(slideInAnimation)
        binding.etConfirmPassword.startAnimation(slideInAnimation)

        // Setup click listeners
        setupClickListeners()

        // Setup ViewModel observers
        setupObservers()
    }

    private fun setupClickListeners() {
        binding.apply {
            btnSignUp.setOnClickListener(this@SignUpActivity)
            btnMasuk.setOnClickListener(this@SignUpActivity)
            btnBack.setOnClickListener(this@SignUpActivity)
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
                    "name" -> binding.etName.error = value
                    "email" -> binding.etEmail.error = value
                    "password" -> binding.etPassword.error = value
                    "confirmPassword" -> binding.etConfirmPassword.error = value
                    else -> showGeneralErrorDialog(value)
                }
            }
        }

        // Observe user registration
        mViewModel.getUser().observe(this) { user ->
            if (user != null) navigateToSignIn()
        }
    }

    private fun navigateToSignIn() {
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun submitForm() {
        val name = binding.etName.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        // Basic validation
        val isNameValid = name.isNotBlank()
        val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordValid = password.length >= 8
        val isConfirmPasswordValid = confirmPassword == password

        if (isNameValid && isEmailValid && isPasswordValid && isConfirmPasswordValid) {
            mViewModel.registerUser(SignUpBody(name, email, password))
        } else {
            if (!isNameValid) binding.etName.error = getString(R.string.name_error)
            if (!isEmailValid) binding.etEmail.error = getString(R.string.email_error)
            if (!isPasswordValid) binding.etPassword.error = getString(R.string.password_error)
            if (!isConfirmPasswordValid) binding.etConfirmPassword.error = getString(R.string.confirm_password_error)

        }
    }

    private fun showGeneralErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("SIGN UP ERROR")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnSignUp -> submitForm()
            R.id.btnMasuk -> startActivity(Intent(this, SignInActivity::class.java))
            R.id.btn_back -> navigateToMainActivity()
        }
    }

    @Deprecated("Deprecated in favor of OnBackPressedDispatcher", replaceWith = ReplaceWith("onBackPressedDispatcher"))
    override fun onBackPressed() {
        super.onBackPressed()
        navigateToMainActivity()
    }
}