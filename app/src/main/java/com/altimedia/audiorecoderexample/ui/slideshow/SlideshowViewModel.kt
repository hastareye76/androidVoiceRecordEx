package com.altimedia.audiorecoderexample.ui.slideshow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

//Activity 나 Fragment 와 같은 UI에서 출력할 데이타를 처리하는 곳이다.
//안드로이드 이를 위해서 ViewModel 이라는 클래스를 제공하고 SlideshowViewModel 이를 구현하고 있다.
//자세히 읽어봐야 겠다.
class SlideshowViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is slideshow Fragment"
    }
//
    val text: LiveData<String> = _text
}