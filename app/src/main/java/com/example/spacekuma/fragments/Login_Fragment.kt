package com.example.spacekuma.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.example.spacekuma.BR
import com.example.spacekuma.R
import com.example.spacekuma.activities.MainActivity
import com.example.spacekuma.data.Login_Model
import com.example.spacekuma.databinding.LoginFragmentBinding
import com.example.spacekuma.db.LoginDB
import com.example.spacekuma.retrofit.ApiClient
import com.example.spacekuma.view_models.login.LoginViewModel

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.login_fragment.view.*

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Login_Fragment: Fragment() {

    private lateinit var loginViewModel: LoginViewModel
    lateinit var bind : LoginFragmentBinding

    var disposable = CompositeDisposable()
    private var loginDB: LoginDB? = null

    var token : String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = DataBindingUtil.inflate(inflater, R.layout.login_fragment,container,false)

        loginViewModel = ViewModelProviders.of(activity!!).get(LoginViewModel::class.java)
        bind.setVariable(BR.login,loginViewModel)

//        (1).툴바 셋업
        (activity as AppCompatActivity).setSupportActionBar(bind.root.home_toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        bind.root.Btn_Back.setOnClickListener { Navigation.findNavController(bind.root).navigateUp() }

//        (2).소프트 키보드 완료버튼 클릭 이벤트 처리
        bind.root.editText_PassWord.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                return@OnEditorActionListener true
            } else {
                false
            }
        })


//        Navigation.findNavController(bind.root).navigate(R.id.action_navigation_id_to_navigation_pw)


//        (3).화면이동 (로그인 성공 시 메인액티비티로 이동.)
        bind.root.Btn_Login.setOnClickListener {
            Log.d("Login_Fragment","ID : " + bind.root.editText_id.text.toString())
            Log.d("Login_Fragment","PW : " + bind.root.editText_PassWord.text.toString())
            Log.d("Login_Fragment","Token : " + ReturnToken())

            ApiClient.getClient.Login(bind.root.editText_id.text.toString(),bind.root.editText_PassWord.text.toString(),ReturnToken()).enqueue(object :Callback<Login_Model> {
                override fun onFailure(call: Call<Login_Model>, t: Throwable) {
                    Log.d("Login_Fragment", "Btn_Login -> onClick-> onFailure -> $t.")
                    Toast.makeText(activity, "관리자에게 문의해주세요.", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<Login_Model>,response: Response<Login_Model>) {
                    if (response.isSuccessful) {
                        if (response.body()?.checkModel!!.Success) {
                            Log.d("Login_Fragment","response.body()!!.checkModel.Message : " + response.body()?.checkModel!!.Message)
                            Log.d("Login_Fragment","response.body()!!.checkModel.로그인 성공: " + response.body()?.myinfoModel.toString())

                            var Login_Ob : Disposable = Observable.just(response.body()?.myinfoModel!!)
                                .subscribeOn(Schedulers.io())
                                .subscribe( {
                                    LoginDB
                                        .getInstance(activity!!)!!
                                        .loginDao()
                                        .insert(response.body()?.myinfoModel!!)
                                }, {
                                    Log.e("MyTag", it.message)
                                })
                            disposable.add(Login_Ob)

                            val pref : SharedPreferences = activity!!.getSharedPreferences("LoginInfo",0)
                            val editor: SharedPreferences.Editor = pref.edit()
                            editor.putInt("Num", response.body()?.myinfoModel!!.Num)
                            editor.apply()

                            Log.d("Login_Fragment", " -> : "+ response.body()?.myinfoModel!!.Num)

                            startActivity(
                                Intent(activity, MainActivity::class.java)
                                    .putExtra("Num",response.body()?.myinfoModel?.Num)
                                    .putExtra("ID",response.body()?.myinfoModel?.ID)
                                    .putExtra("Name",response.body()?.myinfoModel?.Name)
                                    .putExtra("Pic",response.body()?.myinfoModel?.Pic)
                                    .putExtra("Date",response.body()?.myinfoModel?.Date)
                                    .putExtra("Token",response.body()?.myinfoModel?.Token)
                            )
                            dispose()
                            activity!!.finish()

                        } else {
                            Toast.makeText(activity, response.body()?.checkModel!!.Message, Toast.LENGTH_SHORT).show()
                            Log.d("Login_Fragment","response.body()!!.checkModel.Message : " + response.body()?.checkModel!!.Message)
                        }
                    } else {

                    }
                }

            })

        }

        return bind.root
    }

    override fun onDetach() {
        dispose()
        super.onDetach()
    }

    fun dispose () {
        // 메모리 누수를 막기위한 함수입니다. 모든 Observable 을 삭제 및 디비 인스턴스도 삭제.
        if (!disposable.isDisposed) {
            disposable.dispose()
            Log.d("OnDestroy", "CompositeDisposable() ->: Disposed")
        }
        loginDB?.destroyInstance()
    }

    fun ReturnToken () : String {
        val pref : SharedPreferences = activity!!.getSharedPreferences("LoginInfo",0)
        return pref.getString("Token","")!!
    }

}