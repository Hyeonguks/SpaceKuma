package com.example.spacekuma.view_models.login

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Where is it? -> "
    }
    val text: LiveData<String> = _text

    var ID_Boolean = ObservableField<Boolean>()
    var PassWord_Boolean = ObservableField<Boolean>()
    var Name_Boolean = ObservableField<Boolean>()

    var ID_Text = ObservableField<String>()
    var PassWord_Text = ObservableField<String>()
    var Name_Text = ObservableField<String>()

    init {
        ID_Boolean.set(false)
        PassWord_Boolean.set(false)
        Name_Boolean.set(false)
    }

    fun Check_ID (s : String) {
        Log.d("ViewModel","Check_ID -> "+s)
        if (s.length > 5) {
            ID_Boolean.set(true)
        } else {
            ID_Boolean.set(false)
        }
    }

//    fun onTextChanged(s : CharSequence, start : Int , before : Int , count : Int ) {
//        Log.w("tag", "onTextChanged " + s);
//    }

}