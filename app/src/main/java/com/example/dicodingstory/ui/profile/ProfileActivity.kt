package com.example.dicodingstory.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.dicodingstory.R
import com.example.dicodingstory.databinding.ActivityProfileBinding
import com.example.dicodingstory.ui.auth.SignInActivity
import com.example.dicodingstory.MainActivity
import com.example.dicodingstory.utils.UserPreferences
import com.example.dicodingstory.utils.dataStore
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        displayUserInfo()

        binding.logoutButton.setOnClickListener {
            logout()
        }

        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun displayUserInfo() {
        val userPreferences = UserPreferences.getInstance(dataStore)
        lifecycleScope.launch {
            userPreferences.getUser().collect { userData ->
                binding.tvName.text = userData.userName
                binding.tvEmail.text = userData.userEmail
            }
        }
    }

    private fun logout() {
        val userPreferences = UserPreferences.getInstance(dataStore)
        lifecycleScope.launch {
            userPreferences.logout()
            val intent = Intent(this@ProfileActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Navigate back to HomeFragment
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
