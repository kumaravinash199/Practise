package com.avinash.b_task.dashboard.ui.Annotate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AnnotateViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "movable view"
    }
    val text: LiveData<String> = _text
}