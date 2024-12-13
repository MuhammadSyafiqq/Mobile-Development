package com.example.dicodingstory.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingstory.data.ApiResult
import com.example.dicodingstory.data.ListStoryItem
import com.example.dicodingstory.data.StoryRepository
import kotlinx.coroutines.launch

class HomeFragmentViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _listStory = MutableLiveData<List<ListStoryItem>>()
    val listStory: LiveData<List<ListStoryItem>> = _listStory

    private val _storyResponse = MutableLiveData<ApiResult<Any>>()
    val storyResponse: LiveData<ApiResult<Any>> = _storyResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getStories(token: String) {
        _isLoading.value = true
        viewModelScope.launch {
            repository.getStories(token).collect { result ->
                _isLoading.value = false
                when (result) {
                    is ApiResult.Success -> {
                        _listStory.postValue(result.data.listStory)
                        _storyResponse.postValue(ApiResult.Success(result.data.listStory))
                        Log.d("HomeFragmentViewModel", "List size: ${result.data.listStory.size}")
                    }
                    is ApiResult.Error -> {
                        _storyResponse.postValue(result)
                    }
                    is ApiResult.Loading -> {
                        _storyResponse.postValue(ApiResult.Loading)
                    }
                }
                Log.d("HomeFragmentViewModel", "Fetched stories: $result")
            }
        }
    }
}
