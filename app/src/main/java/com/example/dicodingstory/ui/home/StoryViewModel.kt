package com.example.dicodingstory.ui.home

import androidx.lifecycle.*
import com.example.dicodingstory.data.ApiResult
import com.example.dicodingstory.data.StoryRepository
import com.example.dicodingstory.data.StoryResponse
import kotlinx.coroutines.launch

class StoryViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _stories = MutableLiveData<ApiResult<StoryResponse>>()
    val stories: LiveData<ApiResult<StoryResponse>> = _stories

    fun fetchStories(token: String) {
        viewModelScope.launch {
            repository.getStories(token).collect { result ->
                _stories.value = result
            }
        }
    }
}
