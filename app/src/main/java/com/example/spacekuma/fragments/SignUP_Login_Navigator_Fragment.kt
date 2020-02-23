package com.example.spacekuma.fragments

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.example.spacekuma.R
import com.example.spacekuma.view_models.signup.SignUPViewModel

import kotlinx.android.synthetic.main.sign_up__login_navigator_fragment.view.*

class  SignUP_Login_Navigator_Fragment : Fragment() {

    private lateinit var signUPViewModel: SignUPViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        signUPViewModel =
            ViewModelProviders.of(activity!!).get(SignUPViewModel::class.java)
        val root = inflater.inflate(R.layout.sign_up__login_navigator_fragment, container, false)
        signUPViewModel.start.observe(activity!!, Observer {
//            it + "Login Class"
        })

        root.Btn_Sign.setOnClickListener {
            Navigation.findNavController(root).navigate(R.id.action_navigation_login_to_navigation_id)
        }

        root.Btn_Login.setOnClickListener {
            Navigation.findNavController(root).navigate(R.id.action_navigation_signup_login_navigator_to_navigation_login)
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // TODO: Use the ViewModel
    }

}
