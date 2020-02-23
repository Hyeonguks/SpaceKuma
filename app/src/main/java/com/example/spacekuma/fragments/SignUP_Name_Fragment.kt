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
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.example.spacekuma.BR
import com.example.spacekuma.R
import com.example.spacekuma.activities.MainActivity
import com.example.spacekuma.data.Login_Model
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUP_Name_Fragment : Fragment() {

    /*
    1. Login_And_SignUP_Activity 에 필요한 플래그먼트 4개 중 1개입니다.
    2. 이동 순서 SignUP_Login_Navigator_Fragment -> SignUP_ID_Fragment -> SignUP_PW_Fragment -> **SignUP_Name_Fragment** -> 가입완료 ->  SignUP_Login_Navigator_Fragment
    3. (1).툴바 셋업, (2).소프트키보드 완료 버튼 클릭 이벤트 처리, (3).화면이동

    *etc
    1. (2).와 DataBinding 의 이벤트 처리로 ViewModel 에서 회원가입을 시작
    2. 회원가입이 성공하면 LiveData 인 start 라는 변수가 true 로 바뀜.
    3. 그렇다면 (3).에 해당하는 로직이 실행됩니다.
     */

    private lateinit var signUPViewModel: SignUPViewModel
    lateinit var bind : SignUpNameFragmentBinding

    private var loginDB : LoginDB? = null

    var disposable = CompositeDisposable()

    var Token : String = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = DataBindingUtil.inflate(inflater, R.layout.sign_up__name__fragment,container,false)

        signUPViewModel = ViewModelProviders.of(activity!!).get(SignUPViewModel::class.java)
        bind.setVariable(BR.name,signUPViewModel)

//        (1).툴바 셋업
        (activity as AppCompatActivity).setSupportActionBar(bind.root.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        bind.root.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(bind.root).navigateUp()
        }

//        (2).소프트키보드 완료 버튼 클릭 이벤트 처리
        bind.root.editText_name.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (signUPViewModel.Name_Boolean.get()!!) {
//                    signUPViewModel.SignUP()
                    SignUP()
                } else {

                }
                return@OnEditorActionListener true
            } else {
                false
            }
        })

        bind.root.Btn_Sign.setOnClickListener { SignUP() }

        return bind.root
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
        ApiClient.getClient.SignUP(signUPViewModel.ID.get().toString(),signUPViewModel.PassWord.get().toString(),signUPViewModel.Name.get().toString(),ReturnToken()).enqueue(object :
            Callback<Login_Model> {
            override fun onFailure(call: Call<Login_Model>, t: Throwable) {
                Log.d("SignUP_ViewModel", "SignUP -> onFailure -> $t.")
                signUPViewModel.Name_Boolean.set(false)
                signUPViewModel.setMessage(4,signUPViewModel.Name_Message)
            }

            override fun onResponse(call: Call<Login_Model>, response: Response<Login_Model>) {
                if (response.isSuccessful) {
                    if (response.body()!!.checkModel.Success) {
                        Log.d("SignUP_ViewModel","response.body()!!.checkModel.Success : true ↓")
                        Log.d("SignUP_ViewModel","response.body()!!.checkModel.Message : " + response.body()?.checkModel!!.Success)
                        Log.d("SignUP_ViewModel","response.body()!!.myinfomodel : " + response.body()?.myinfoModel)

                        signUPViewModel.loginModel = response.body()!!.myinfoModel
                        signUPViewModel.setMessage(5,signUPViewModel.Name_Message)
//                        signUPViewModel._start.value = true
                        signUPViewModel.Name_Boolean.set(true)
                        signUPViewModel.Name_Message.set("진행 중....")
                        var insert_Ob = Observable.just(signUPViewModel.loginModel!!)
                            .subscribeOn(Schedulers.io())
                            .subscribe( {
                                LoginDB
                                    .getInstance(activity!!)!!
                                    .loginDao()
                                    .insert(signUPViewModel.loginModel!!)
                            }, {
                                Log.e("MyTag", it.message)
                            })
                        disposable.add(insert_Ob)

                        val pref : SharedPreferences = activity!!.getSharedPreferences("LoginInfo",0)
                        val editor: SharedPreferences.Editor = pref.edit()
                        editor.putInt("Num",signUPViewModel.loginModel!!.Num)
                        editor.putString("Token", response.body()?.myinfoModel!!.Token)
                        editor.apply()

                        Log.d("startMain", " -> : "+ signUPViewModel.loginModel!!.Num)

                        startActivity(Intent(activity!!, MainActivity::class.java))
                        dispose()
                        activity!!.finish()
                    } else {
                        Log.d("SignUP_ViewModel","response.body()!!.Success : false. :  중복된 아이디")
                        signUPViewModel.setMessage(3,signUPViewModel.Name_Message)
                        signUPViewModel.Name_Boolean.set(false)
                    }
                } else {
                    Log.d("SignUP_ViewModel", "response.isSuccessful : false.")
                    signUPViewModel.setMessage(0,signUPViewModel.Name_Message)
                    signUPViewModel.Name_Boolean.set(false)

                }
            }

        })
    }

    fun ReturnToken () : String {
        val pref : SharedPreferences = activity!!.getSharedPreferences("LoginInfo",0)
        return pref.getString("Token","")!!
    }
}

