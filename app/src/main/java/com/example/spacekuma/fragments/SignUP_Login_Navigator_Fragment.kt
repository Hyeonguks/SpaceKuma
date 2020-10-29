package com.example.spacekuma.fragments

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.example.spacekuma.BR
import com.example.spacekuma.R
import com.example.spacekuma.databinding.SignUpIdFragmentBinding
import com.example.spacekuma.databinding.SignUpLoginNavigatorFragmentBinding
import com.example.spacekuma.view_models.signup.SignUPViewModel

import kotlinx.android.synthetic.main.sign_up__login_navigator_fragment.view.*

class  SignUP_Login_Navigator_Fragment : Fragment(),View.OnClickListener {
    /*
    로그인할 것인지 회원가입할지에 대한 클릭 이벤트만 존재함.
     */

    private lateinit var signUPViewModel: SignUPViewModel
    lateinit var bind : SignUpLoginNavigatorFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        signUPViewModel = ViewModelProviders.of(activity!!).get(SignUPViewModel::class.java)
        bind = DataBindingUtil.inflate(inflater, R.layout.sign_up__login_navigator_fragment,container,false)
        bind.setVariable(BR.signorlogin,signUPViewModel)
        bind.root.Btn_Sign.setOnClickListener(this)
        bind.root.Btn_Login.setOnClickListener(this)
        return bind.root
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.Btn_Sign -> { Navigation.findNavController(bind.root).navigate(R.id.action_navigation_login_to_navigation_id) }
            R.id.Btn_Login -> { Navigation.findNavController(bind.root).navigate(R.id.action_navigation_signup_login_navigator_to_navigation_login) }
        }
    }
}
