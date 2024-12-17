package com.nabilnazar.videoplayerapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VideoViewModel : ViewModel() {

    // List of Video URLs
    private val _videoList = MutableStateFlow<List<String>>(emptyList())
    val videoList: StateFlow<List<String>> = _videoList

    // Currently Selected Video URL
    private val _selectedVideoUrl = MutableStateFlow<String?>(null)
    val selectedVideoUrl: StateFlow<String?> = _selectedVideoUrl

    init {
        loadVideos()
    }

    private fun loadVideos() {
        viewModelScope.launch {
            // Simulate loading data
            _videoList.value = listOf(
                "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4",
                "https://www.w3schools.com/html/mov_bbb.mp4",
                "https://media.w3.org/2010/05/sintel/trailer.mp4",
                "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/720/Big_Buck_Bunny_720_10s_1MB.mp4"
            )
        }
    }

    fun selectVideo(url: String?) {
        _selectedVideoUrl.value = url
    }
}