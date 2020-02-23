package com.example.spacekuma.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.spacekuma.data.MyInfo_Model
import com.example.spacekuma.view_models.main.MainViewModel

abstract class Main_Base_Fragment<T : ViewDataBinding>: Fragment() {
    lateinit var bind: T

    abstract val layoutID : Int
    abstract val BRName : Int
    lateinit var viewModel : MainViewModel

    abstract fun Init()

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        bind = DataBindingUtil.inflate(inflater,layoutID,container,false)

        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
        bind.setVariable(BRName,viewModel)

        Init()

        return bind.root
    }

}