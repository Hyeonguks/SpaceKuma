package com.example.spacekuma.fragments

import android.content.Intent
import android.content.SharedPreferences
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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.example.spacekuma.BR
import com.example.spacekuma.R
import com.example.spacekuma.activities.MainActivity
import com.example.spacekuma.activities.SplashActivity
import com.example.spacekuma.data.Login_Model
import com.example.spacekuma.databinding.SignUpIdFragmentBinding
import com.example.spacekuma.databinding.SignUpNameFragmentBinding
import com.example.spacekuma.db.LoginDB
import com.example.spacekuma.retrofit.ApiClient
import com.example.spacekuma.view_models.signup.SignUPViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.sign_up__name__fragment.view.*
import kotlinx.android.synthetic.main.sign_up__name__fragment.view.toolbar
import kotlinx.android.synthetic.main.sign_up__pw__fragment.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUP_Name_Fragment : SignUPBaseFragment<SignUpNameFragmentBinding>(), View.OnClickListener {

    /*
    1. Login_And_SignUP_Activity 에 필요한 플래그먼트 4개 중 1개입니다.
    2. 이동 순서 SignUP_Login_Navigator_Fragment -> SignUP_ID_Fragment -> SignUP_PW_Fragment -> **SignUP_Name_Fragment** -> 가입완료 ->  SignUP_Login_Navigator_Fragment
    3. (1).툴바 셋업, (2).소프트키보드 완료 버튼 클릭 이벤트 처리, (3).화면이동

    *etc
    1. (2).와 DataBinding 의 이벤트 처리로 ViewModel 에서 회원가입을 시작
    2. 회원가입이 성공하면 LiveData 인 start 라는 변수가 true 로 바뀜.
    3. 그렇다면 (3).에 해당하는 로직이 실행됩니다.
     */

    /*
    참고 사이트 : Navigation + Fragment + Toolbar + 뒤로가기 버튼 적용
    https://stackoverflow.com/questions/26651602/display-back-arrow-on-toolbar
    http://susemi99.kr/5438/

    1. 프로그먼트 이동순서 : Login_Fragment <-> (메인플래그먼트)SignUP_Login_Navigator_Fragment <-> *SignUP_ID_Fragment* <-> SignUP_PW_Fragment <-> SignUP_Name_Fragment
    2. 가입버튼 누를 시, 서버로 회원가입 로직 실행.
     */

    override val layoutID: Int = R.layout.sign_up__name__fragment
    override val BRName: Int = BR.name

    var loginDB : LoginDB? = null
    var disposable = CompositeDisposable()

    override fun Init() {
        (activity as AppCompatActivity).run {
            setSupportActionBar(bind.root.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        bind.root.Btn_Sign.setOnClickListener(this)

        bind.root.editText_name.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (viewModel.Name_Boolean.get()!!) {
                    SignUP()
                }
                return@OnEditorActionListener true
            } else {
                false
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.Btn_Sign -> { SignUP() }
        }
    }

    fun ReturnToken () : String {
        val pref : SharedPreferences = activity!!.getSharedPreferences("LoginInfo",0)
        return pref.getString("Token","")!!
    }

    override fun onDestroy() {
        dispose()
        super.onDestroy()
    }

    fun dispose () {
        // 참고 예제 -> https://beomseok95.tistory.com/60
        // 메모리 누수를 막기위한 함수입니다. 모든 Observable 을 삭제 및 디비 인스턴스도 삭제.
        if (!disposable.isDisposed) {
            disposable.dispose()
            Log.d("OnDestroy", "CompositeDisposable() ->: Disposed")
        }
        loginDB?.destroyInstance()
    }

    fun SignUP() {
        ApiClient.getClient.SignUP(viewModel.ID.get().toString(),viewModel.PassWord.get().toString(),viewModel.Name.get().toString(),ReturnToken()).enqueue(object :
            Callback<Login_Model> {
            override fun onFailure(call: Call<Login_Model>, t: Throwable) {
                Log.d("SignUP_ViewModel", "SignUP -> onFailure -> $t.")
                viewModel.Name_Boolean.set(false)
                viewModel.setMessage(4,viewModel.Name_Message)
            }

            override fun onResponse(call: Call<Login_Model>, response: Response<Login_Model>) {
                if (response.isSuccessful) {
                    if (response.body()!!.checkModel.Success) {
                        Log.d("SignUP_ViewModel","response.body()!!.checkModel.Success : true ↓")
                        Log.d("SignUP_ViewModel","response.body()!!.checkModel.Message : " + response.body()?.checkModel!!.Success)
                        Log.d("SignUP_ViewModel","response.body()!!.myinfomodel : " + response.body()?.myinfoModel)

                        viewModel.loginModel = response.body()!!.myinfoModel
                        viewModel.setMessage(5,viewModel.Name_Message)
                        viewModel.Name_Boolean.set(true)
                        viewModel.Name_Message.set("진행 중....")
                        var insert_Ob = Observable.just(viewModel.loginModel!!)
                            .subscribeOn(Schedulers.io())
                            .subscribe( {
                                LoginDB
                                    .getInstance(activity!!)!!
                                    .loginDao()
                                    .insert(viewModel.loginModel!!)
                            }, {
                                Log.e("MyTag", it.message!!)
                            })
                        disposable.add(insert_Ob)

                        val pref : SharedPreferences = activity!!.getSharedPreferences("LoginInfo",0)
                        val editor: SharedPreferences.Editor = pref.edit()
                        editor.putInt("Num",viewModel.loginModel!!.Num)
                        editor.putString("Token", response.body()?.myinfoModel!!.Token)
                        editor.apply()

                        Log.d("startMain", " -> : "+ viewModel.loginModel!!.Num)

                        startActivity(Intent(activity!!, SplashActivity::class.java))
                        dispose()
                        activity!!.finish()
                    } else {
                        Log.d("SignUP_ViewModel","response.body()!!.Success : false. :  중복된 아이디")
                        viewModel.setMessage(3,viewModel.Name_Message)
                        viewModel.Name_Boolean.set(false)
                    }
                } else {
                    Log.d("SignUP_ViewModel", "response.isSuccessful : false.")
                    viewModel.setMessage(0,viewModel.Name_Message)
                    viewModel.Name_Boolean.set(false)

                }
            }

        })
    }
}

