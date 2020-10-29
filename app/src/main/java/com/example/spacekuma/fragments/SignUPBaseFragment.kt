package com.example.spacekuma.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.example.spacekuma.R
import com.example.spacekuma.view_models.main.MainViewModel
import com.example.spacekuma.view_models.signup.SignUPViewModel

abstract class SignUPBaseFragment<T : ViewDataBinding>: Fragment() {
    lateinit var bind: T

    abstract val layoutID : Int
    abstract val BRName : Int
    lateinit var viewModel : SignUPViewModel

    abstract fun Init()

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        bind = DataBindingUtil.inflate(inflater,layoutID,container,false)
        viewModel = ViewModelProviders.of(activity!!).get(SignUPViewModel::class.java)
        bind.setVariable(BRName,viewModel)
        bind.executePendingBindings()

        Init()
        return bind.root
    }
}