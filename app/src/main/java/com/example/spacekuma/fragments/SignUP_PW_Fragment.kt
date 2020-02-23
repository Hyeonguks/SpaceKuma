package com.example.spacekuma.fragments

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation

import com.example.spacekuma.BR
import com.example.spacekuma.R
import com.example.spacekuma.databinding.SignUpPwFragmentBinding
import com.example.spacekuma.view_models.signup.SignUPViewModel
import kotlinx.android.synthetic.main.sign_up__pw__fragment.view.*
import kotlinx.android.synthetic.main.sign_up__pw__fragment.view.Btn_Next

class SignUP_PW_Fragment : Fragment() {

    /*
    1. Login_And_SignUP_Activity 에 필요한 플래그먼트 4개 중 1개입니다.
    2. 이동 순서 SignUP_Login_Navigator_Fragment -> SignUP_ID_Fragment -> **SignUP_PW_Fragment** -> SignUP_Name_Fragment -> 가입완료 ->  SignUP_Login_Navigator_Fragment
    3. (1).툴바 셋업, (2).소프트키보드 완료 버튼 클릭 이벤트 처리, (3).화면이동, (4).비밀번호 보기 CheckBox 이벤트 처리
     */

    private lateinit var signUPViewModel: SignUPViewModel
    lateinit var bind : SignUpPwFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = DataBindingUtil.inflate(inflater, R.layout.sign_up__pw__fragment,container,false)

        signUPViewModel = ViewModelProviders.of(activity!!).get(SignUPViewModel::class.java)
        bind.setVariable(BR.pw,signUPViewModel)

//        (1).툴바 셋업
        (activity as AppCompatActivity).setSupportActionBar(bind.root.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        bind.root.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(bind.root).navigateUp()
        }

//        (2).소프트키보드 완료 버튼 클릭 이벤트 처리
        bind.root.editText_pw.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (signUPViewModel.PassWord_Boolean.get()!!) {
                    Navigation.findNavController(bind.root).navigate(R.id.action_navigation_pw_to_navigation_name)
                } else {

                }
                return@OnEditorActionListener true
            } else {
                false
            }
        })

//        (3).화면이동
        bind.root.Btn_Next.setOnClickListener {
            Navigation.findNavController(bind.root).navigate(R.id.action_navigation_pw_to_navigation_name)
        }

//        (4).비밀번호 보기 CheckBox 이벤트 처리
        bind.root.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                bind.root.editText_pw.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                bind.root.editText_pw.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            bind.root.editText_pw.setSelection(bind.root.editText_pw.text.length)
        }

        return bind.root
    }

}

