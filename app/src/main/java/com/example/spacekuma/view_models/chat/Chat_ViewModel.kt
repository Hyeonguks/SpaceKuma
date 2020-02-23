package com.example.spacekuma.view_models.chat

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class Chat_ViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    var Num: ObservableField<Int> = ObservableField(0)
    var ID: ObservableField<String> = ObservableField("")
    var Name: ObservableField<String> = ObservableField("")
    var Pic: ObservableField<String> = ObservableField("")
    var Date: ObservableField<String> = ObservableField("")
    var Token: ObservableField<String> = ObservableField("")
}