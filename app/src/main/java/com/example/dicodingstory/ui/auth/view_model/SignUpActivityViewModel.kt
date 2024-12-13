package com.example.dicodingstory.ui.auth.view_model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingstory.ui.auth.data.SignUpBody
import com.example.dicodingstory.ui.auth.data.User
import com.example.dicodingstory.ui.auth.repository.AuthRepository
import com.example.dicodingstory.utils.RequestStatus
import kotlinx.coroutines.launch

class SignUpActivityViewModel(
    private val authRepository: AuthRepository,
    private val application: Application
) : ViewModel() {

    private val isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    private val errorMessage: MutableLiveData<HashMap<String, String>> = MutableLiveData()
    private val user: MutableLiveData<User> = MutableLiveData()

    private val _navigateToSignIn: MutableLiveData<Boolean> = MutableLiveData()
    val navigateToSignIn: LiveData<Boolean> get() = _navigateToSignIn

    fun getIsLoading(): LiveData<Boolean> = isLoading
    fun getErrorMessage(): LiveData<HashMap<String, String>> = errorMessage
    fun getUser(): LiveData<User> = user

    fun registerUser(body: SignUpBody) {
        viewModelScope.launch {
            authRepository.registerUser(body).collect {
                when (it) {
                    is RequestStatus.Waiting -> {
                        isLoading.value = true
                    }
                    is RequestStatus.Success -> {
                        isLoading.value = false
                        user.value = it.data.loginResult
                        _navigateToSignIn.value = true // Trigger navigation
                    }
                    is RequestStatus.Error -> {
                        isLoading.value = false
                        errorMessage.value = it.message
                    }
                }
            }
        }
    }

    fun onNavigationHandled() {
        _navigateToSignIn.value = false
    }
}