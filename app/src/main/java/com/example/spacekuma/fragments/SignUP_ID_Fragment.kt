package com.example.spacekuma.fragments

import android.os.Bundle
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
import com.example.spacekuma.databinding.SignUpIdFragmentBinding
import com.example.spacekuma.view_models.signup.SignUPViewModel

import kotlinx.android.synthetic.main.sign_up__id__fragment.view.*

class SignUP_ID_Fragment: Fragment() {

    /* 참고 사이트 : Navigation + Fragment + Toolbar + 뒤로가기 버튼 적용
        https://stackoverflow.com/questions/26651602/display-back-arrow-on-toolbar
        http://susemi99.kr/5438/
         */

    /*
    1. Login_And_SignUP_Activity 에 필요한 플래그먼트 4개 중 1개입니다.
    2. 이동 순서 SignUP_Login_Navigator_Fragment -> **SignUP_ID_Fragment** -> SignUP_PW_Fragment -> SignUP_Name_Fragment -> 가입완료 ->  SignUP_Login_Navigator_Fragment
    3. (1).툴바 셋업, (2).소프트키보드 완료 버튼 클릭 이벤트 처리, (3).화면이동
     */

    private lateinit var signUPViewModel: SignUPViewModel
    lateinit var bind : SignUpIdFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = DataBindingUtil.inflate(inflater, R.layout.sign_up__id__fragment,container,false)

        signUPViewModel = ViewModelProviders.of(activity!!).get(SignUPViewModel::class.java)
        bind.setVariable(BR.id,signUPViewModel)

//        (1).툴바 셋업
        (activity as AppCompatActivity).setSupportActionBar(bind.root.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        bind.root.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(bind.root).navigateUp()
        }

//        (2).소프프키보드 완료버튼 클릭 이벤트 처리
        bind.root.editText_id.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (signUPViewModel.ID_Boolean.get()!!) {
                    Navigation.findNavController(bind.root).navigate(R.id.action_navigation_id_to_navigation_pw)
                } else {

                }
                return@OnEditorActionListener true
            } else {
                false
            }
        })

//        (3).화면이동
        bind.root.Btn_Next.setOnClickListener {
            Navigation.findNavController(bind.root).navigate(R.id.action_navigation_id_to_navigation_pw)
        }

        return bind.root
    }

}

