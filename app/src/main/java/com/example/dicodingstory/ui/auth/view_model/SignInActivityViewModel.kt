package com.example.dicodingstory.ui.auth.view_model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.content.Context

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingstory.ui.auth.data.SignInBody
import com.example.dicodingstory.ui.auth.data.User
import com.example.dicodingstory.ui.auth.repository.AuthRepository
import com.example.dicodingstory.utils.RequestStatus
import com.example.dicodingstory.utils.UserPreferences
import com.example.dicodingstory.utils.dataStore
import kotlinx.coroutines.launch


class SignInActivityViewModel(
    val authRepository: AuthRepository,
    val application: Application
) : ViewModel() {
    private var isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    private var errorMessage: MutableLiveData<HashMap<String, String>> = MutableLiveData()
    private var user: MutableLiveData<User> = MutableLiveData()
    private var loginSuccess: MutableLiveData<Boolean> = MutableLiveData()

    fun getIsLoading(): LiveData<Boolean> = isLoading
    fun getErrorMessage(): LiveData<HashMap<String, String>> = errorMessage
    fun getUser(): LiveData<User> = user


    fun loginUser(body: SignInBody, context: Context) {
        viewModelScope.launch {
            authRepository.loginUser(body).collect { result ->
                when (result) {
                    is RequestStatus.Waiting -> {
                        isLoading.value = true
                    }
                    is RequestStatus.Success -> {
                        isLoading.value = false
                        val userData = result.data.loginResult

                        // Save user data using DataStore
                        val userPreferences = UserPreferences.getInstance(context.dataStore)
                        userPreferences.apply {
                            saveIsLoggedIn(true)
                            userData.token.let { saveAccessToken(it) }
                            userData.userId.let { saveUserId(it) }
                            userData.name.let { saveUserName(it) }
                            userData.email.let { saveUserEmail(it) }
                        }

                        // Update ViewModel state
                        user.value = userData
                        loginSuccess.value = true

                        // Log saved preferences for debugging

                        showToast(application, "Login Berhasil")
                    }
                    is RequestStatus.Error -> {
                        isLoading.value = false
                        errorMessage.value = result.message
                        loginSuccess.value = false
                    }
                }
            }
        }
    }

    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}