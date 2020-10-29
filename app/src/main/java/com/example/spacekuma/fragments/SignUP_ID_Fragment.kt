package com.example.spacekuma.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.example.spacekuma.BR
import com.example.spacekuma.R
import com.example.spacekuma.databinding.SignUpIdFragmentBinding
import com.example.spacekuma.view_models.signup.SignUPViewModel

import kotlinx.android.synthetic.main.sign_up__id__fragment.view.*

class SignUP_ID_Fragment : SignUPBaseFragment<SignUpIdFragmentBinding>(), View.OnClickListener {
    /*
    참고 사이트 : Navigation + Fragment + Toolbar + 뒤로가기 버튼 적용
    https://stackoverflow.com/questions/26651602/display-back-arrow-on-toolbar
    http://susemi99.kr/5438/

    1. 프로그먼트 이동순서 : Login_Fragment <-> (메인플래그먼트)SignUP_Login_Navigator_Fragment <-> *SignUP_ID_Fragment* <-> SignUP_PW_Fragment <-> SignUP_Name_Fragment
     */

    override val layoutID: Int = R.layout.sign_up__id__fragment
    override val BRName: Int = BR.id

    override fun Init() {
        (activity as AppCompatActivity).run {
            setSupportActionBar(bind.root.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        bind.root.Btn_Next.setOnClickListener(this)

        bind.root.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(bind.root).navigateUp()
        }

        bind.root.editText_id.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (viewModel.ID_Boolean.get()!!) {
                    Navigation.findNavController(bind.root).navigate(R.id.action_navigation_id_to_navigation_pw)
                }
                return@OnEditorActionListener true
            } else {
                false
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.Btn_Next -> { Navigation.findNavController(bind.root).navigate(R.id.action_navigation_id_to_navigation_pw) }
        }
    }

}

