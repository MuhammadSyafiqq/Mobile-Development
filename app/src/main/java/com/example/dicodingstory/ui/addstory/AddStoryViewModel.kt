package com.example.dicodingstory.ui.addstory

import androidx.lifecycle.*
import com.example.dicodingstory.data.ApiResult
import com.example.dicodingstory.data.StoryRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val repository: StoryRepository) : ViewModel() {
    private val _uploadResult = MutableLiveData<ApiResult<Any>>()
    val uploadResult: LiveData<ApiResult<Any>> = _uploadResult

    fun uploadStory(
        token: String,
        description: RequestBody,
        photo: MultipartBody.Part,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ) {
        _uploadResult.postValue(ApiResult.Loading)
        viewModelScope.launch {
            try {
                val result = repository.addStory(token, description, photo, lat, lon)
                _uploadResult.postValue(result)
            } catch (e: Exception) {
                _uploadResult.postValue(ApiResult.Error(e.message ?: "Unknown error"))
            }
        }
    }

    fun uploadStoryAsGuest(
        description: RequestBody,
        photo: MultipartBody.Part,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ) {
        _uploadResult.postValue(ApiResult.Loading)
        viewModelScope.launch {
            try {
                val result = repository.addStoryGuest(description, photo, lat, lon)
                _uploadResult.postValue(result)
            } catch (e: Exception) {
                _uploadResult.postValue(ApiResult.Error(e.message ?: "Unknown error"))
            }
        }
    }
}
