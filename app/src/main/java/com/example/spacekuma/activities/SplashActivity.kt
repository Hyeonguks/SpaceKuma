package com.example.spacekuma.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.spacekuma.R
import com.example.spacekuma.data.Check_Model
import com.example.spacekuma.data.Login_Model
import com.example.spacekuma.data.MyInfo_Model
import com.example.spacekuma.db.LoginDB
import com.example.spacekuma.retrofit.ApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashActivity : AppCompatActivity() {

    /*
    1. 앱이 실행되면 제일 처음 보이게되는 스플래쉬 액티비티입니다.
    2. SharedPreferences 에서 로그인 기록(유저 고유번호 : Num)이 있는지 확인합니다.
    3. 로그인 기록이 있다면, SharedPreferences 에 저장된 Num 을 기준으로 Room DataBase 에 저장되어있는 ID 와 PassWord 가 존재하는지 확인.
    4. 존재하지 않는다면 Login_And_SignUP_Activity(SignUP_Login_Navigator_Fragment)로 이동.
    5. 존재한다면, 저장되어있는 값을 서버 DB 의 값과 비교하여 일치하면 로그인 그리고 MainActivity 로 이동.
    6. 로그인 기록이 없다면, Login_And_SignUP_Activity 로 이동합니다. (회원가입 or 로그인이 가능합니다.)

    문제 사항
    1. FCM 토큰 이슈 : 앱 실행시 FirebaseInstanceId.getInstance().instanceId 로 FCM 토큰 값을 SharedPreferences 에 저장중이다.
    문제는 첫 실행 시에 문제인데, 첫 실행하고 서버와 SharedPreferences 에 토큰을 저장. 상대방 측에서 나에게 FCM 보내면 심지어 노드서버가 죽고 FCM 이 오지 않음.
    찾아보니 유효하지 않은 토큰으로 메세지를 보내려하니까 죽음.
    도대체 왜??? 토큰 값도 로그에 잘찍혀서 해당 토큰을 로컬 및 서버에 저장했는데??????현재 방안으로는 앱을 두번정도 재실행해야 토큰이 유효해서 죽지 않더라.
    첫 실행에 유효한 토큰을 얻을수있는 방법은 무엇인가???????

     */

    private var loginDB: LoginDB? = null
    var Num :Int = 0

    var disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            Log.d("onCreate()", "isSuccessful ")
            // Get new Instance ID token
            val token = it.token
            val pref : SharedPreferences = getSharedPreferences("LoginInfo",0)
            Num = pref.getInt("Num",0)

            ApiClient.getClient.UpdateFcm(Num,token).enqueue(object :Callback<Check_Model> {
                override fun onFailure(call: Call<Check_Model>, t: Throwable) {
                    GO_TO_LOGIN_ACTIVITY()
                    Log.d("onCreate()", "onFailure : $t")

                }
                override fun onResponse(call: Call<Check_Model>,response: Response<Check_Model>) {
                    if (response.isSuccessful) {
                        if (response.body()!!.Success) {
                            val editor: SharedPreferences.Editor = pref.edit()
                            Log.d("onCreate()", "onResponse isSuccessful")

                            Log.d("onCreate()", ": Num -> $Num")
                            Log.d("onCreate()", ": Token -> $token")

                            editor.putString("Token",token)
                            editor.apply()

                            // Num 이 0 이라면 로그인 기록이 없기때문에 로그인 페이지로 이동.
                            when (Num) {
                                0 -> GO_TO_LOGIN_ACTIVITY()
                                else -> GO_TO_MAIN_ACTIVITY ()
                            }
                            Log.d("SplashActivity", "onCreate() :$token")
                        } else {
                            Log.d("onCreate()", "onResponse !isSuccessful")
                            GO_TO_LOGIN_ACTIVITY()
                        }

                    } else {

                    }
                }

            })
        }


    }

    fun GO_TO_MAIN_ACTIVITY() {
        // 로그인 기록이 있는지 판별합니다.
        var Check_Ob : Disposable = LoginDB
            .getInstance(this)!!
            .loginDao()
            .getLoginInfo(Num)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                // 구독시 사용할 함수
                Log.e("GO_TO_MAIN"," doOnSubscribe -> 구독 시작")
            }
            .doOnTerminate {
                // 구독이 끝날 때 사용할 함수
                Log.e("GO_TO_MAIN"," doOnTerminate -> 구독 종료")
            }
            .subscribe({
                when (it) {
                    null -> GO_TO_LOGIN_ACTIVITY()
                    else -> CHECK_USER_INFO(it)
                }
            },{
                Log.e("GO_TO_MAIN"," Throwable -> "+ it.message)
            })

        disposable.add(Check_Ob)
    }

    fun GO_TO_LOGIN_ACTIVITY () {
        dispose()
        startActivity(Intent(this@SplashActivity,Login_And_SignUP_Activity::class.java))
        this@SplashActivity.finish()
    }

    fun CHECK_USER_INFO (userInfo : MyInfo_Model) {
        ApiClient.getClient.Login(userInfo.ID, userInfo.PassWord,userInfo.Token).enqueue(object :Callback<Login_Model> {
            override fun onFailure(call: Call<Login_Model>, t: Throwable) {
                Log.d("SplashActivity", "GO_TO_MAIN -> onFailure -> $t.")
                Toast.makeText(this@SplashActivity, "관리자에게 문의해주세요.", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Login_Model>,response: Response<Login_Model>) {
                if (response.isSuccessful) {
                    if (response.body()?.checkModel!!.Success) {
                        Log.d("SplashActivity","response.body()!!.checkModel.Message : " + response.body()?.checkModel!!.Message)
                        Log.d("SplashActivity","response.body()!!.checkModel.로그인 성공: " + response.body()?.myinfoModel.toString())

                        var Login_Ob : Disposable = Observable.just(response.body()?.myinfoModel!!)
                            .subscribeOn(Schedulers.io())
                            .subscribe( {
                                LoginDB
                                    .getInstance(this@SplashActivity)!!
                                    .loginDao()
                                    .insert(response.body()?.myinfoModel!!)
                            }, {
                                Log.e("MyTag", it.message!!)
                            })
                        disposable.add(Login_Ob)

                        val pref : SharedPreferences = this@SplashActivity.getSharedPreferences("LoginInfo",0)
                        val editor: SharedPreferences.Editor = pref.edit()
                        editor.putInt("Num", response.body()?.myinfoModel!!.Num)
                        editor.putString("Token", response.body()?.myinfoModel!!.Token)
                        editor.apply()

                        Log.d("startMain", " -> : "+ response.body()?.myinfoModel!!.Num)

                        dispose()
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        this@SplashActivity.finish()

                    } else {
                        Toast.makeText(this@SplashActivity, "???", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@SplashActivity,Login_And_SignUP_Activity::class.java))
                        dispose()
                        finish()
                        Log.d("SplashActivity","response.body()!!.checkModel.Message : " + response.body()?.checkModel!!.Message)
                    }
                } else {

                }
            }

        })
    }


    override fun onDestroy() {
        dispose()
        super.onDestroy()
    }

    fun dispose () {
        // 메모리 누수를 막기위한 함수입니다. 모든 Observable 을 삭제 및 디비 인스턴스도 삭제.
        if (!disposable.isDisposed) {
            disposable.dispose()
            Log.d("OnDestroy", "CompositeDisposable() ->: Disposed")
        }
        loginDB?.destroyInstance()
    }

}
