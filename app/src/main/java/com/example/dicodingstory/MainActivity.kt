package com.example.dicodingstory

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.example.dicodingstory.databinding.ActivityMainBinding
import com.example.dicodingstory.ui.auth.SignInActivity
import com.example.dicodingstory.ui.home.HomeFragment
import com.example.dicodingstory.utils.UserPreferences
import com.example.dicodingstory.utils.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize UserPreferences
        userPreferences = UserPreferences.getInstance(dataStore)

        // Check login state
        lifecycleScope.launch {
            val userData = userPreferences.getUser().first()

            if (!userData.isLoggedIn) {
                // If not logged in, redirect to SignIn
                val intent = Intent(this@MainActivity, SignInActivity::class.java)
                startActivity(intent)
                finish()
                return@launch
            }

            // If logged in, proceed with setting up the main activity
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            if (savedInstanceState == null) {
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    add(R.id.fragment_container_view, HomeFragment())
                }
            }
        }
    }
}