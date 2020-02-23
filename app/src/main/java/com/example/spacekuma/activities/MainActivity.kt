package com.example.spacekuma.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.spacekuma.BR
import com.example.spacekuma.R
import com.example.spacekuma.data.MyInfo_Model
import com.example.spacekuma.databinding.ActivityMainBinding
import com.example.spacekuma.db.LoginDB
import com.example.spacekuma.fragments.ChatFragment
import com.example.spacekuma.fragments.HomeFragment
import com.example.spacekuma.fragments.MyPageFragment
import com.example.spacekuma.view_models.main.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.net.URISyntaxException

class MainActivity : AppCompatActivity() {
    private val HOME = "HOME"
    private val CHAT = "CHAT"
    private val NOTIFICATIONS = "NOTIFICATIONS"
    private val MYPAGE = "MYPAGE"

    var Num : Int? = null
    var ID : String? = null
    var Name : String? = null
    var Pic : String? = null
    var Date : String? = null
    var Token : String? = null

    var homeFragment : HomeFragment? = null
    var chatFragment : ChatFragment? = null
    var myPageFragment: MyPageFragment? = null

    var CurrentFragment :Fragment? = null

    lateinit var Check_Ob : Disposable
    private var loginDB: LoginDB? = null
    var disposable = CompositeDisposable()

    lateinit var mainViewModel : MainViewModel
    lateinit var bind : ActivityMainBinding

    companion object {
        var mSocket: Socket = IO.socket("https://devwook.me:3000")
        var mSocketName : String = ""
    }

    fun CONNECT(myinfoModel: MyInfo_Model) {
        try {
            mSocket.connect()

            // emit 이벤트 발생시키기
            // on 구독할 이벤트
            mSocket.on("login", onConnect)
            mSocket.on("userjoinedthechat", onNewUser)
            mSocket.io()

        } catch (e : URISyntaxException) {
            Log.e("node" , e.reason)
        }
    }

    val onNewUser: Emitter.Listener = Emitter.Listener {

        var data = it[0] //String으로 넘어옵니다. JSONArray로 넘어오지 않도록 서버에서 코딩한 것 기억나시죠?
        if (data is String) {
            Log.e("onNewUser",data)
//            chatList.add(Chat_Model(2,"",data,"",""))
//            mAdapter.notifyItemInserted(chatList.size -1)
        } else {
            Log.d("error", "Something went wrong")
        }
    }

    val onConnect: Emitter.Listener = Emitter.Listener {
        //여기서 다시 "login" 이벤트를 서버쪽으로 username 과 함께 보냅니다.
        //서버 측에서는 이 username을 whoIsON Array 에 추가를 할 것입니다.
//        mSocket.emit("login", username)
        var data : JSONObject = (it[0] as JSONObject)
        mSocketName = data["ID"].toString()
        Log.d("onConnect", "Socket is connected with ${data["ID"]}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = DataBindingUtil.setContentView(this,R.layout.activity_main)

        mainViewModel = ViewModelProviders.of(this@MainActivity).get(MainViewModel::class.java)
        bind.setVariable(BR.main,mainViewModel)

        // Shared Preference 에서 저장된 Num 을 이용해서 Room DB 에 저장된 정보를 가져옴.
        Check_Ob = LoginDB
            .getInstance(this)!!
            .loginDao()
            .getLoginInfo(Get_My_Num())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                // 구독시 사용할 함수
                Log.e("MainActivity"," doOnSubscribe -> 구독 시작")
            }
            .doOnTerminate {
                // 구독이 끝날 때 사용할 함수
                Log.e("MainActivity"," doOnTerminate -> 구독 종료")
            }
            .subscribe({
                when (it) {
                    null -> GO_TO_LOGIN_ACTIVITY()
                    else -> Init_Fragment(nav_view,it)
                }
            },{
                Log.e("MainActivity"," Throwable -> "+ it.message)
            })
        disposable.add(Check_Ob)
    }

    fun Get_My_Num () : Int{
        val pref : SharedPreferences = getSharedPreferences("LoginInfo",0)
        return pref.getInt("Num",0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 777) {
            supportFragmentManager.findFragmentByTag(HOME)!!.onActivityResult(requestCode, resultCode, data)
        } else if (requestCode == 888) {
            supportFragmentManager.findFragmentByTag(HOME)!!.onActivityResult(requestCode, resultCode, data)
        } else if (requestCode == 156) {
            supportFragmentManager.findFragmentByTag(CHAT)!!.onActivityResult(requestCode, resultCode, data)
        } else {

        }
    }

    override fun onDestroy() {
        dispose()
        mSocket.disconnect()
        mSocket.off("login",onConnect)
        mSocket.off("userjoinedthechat",onNewUser)
        super.onDestroy()
    }

    fun dispose () {
        // 메모리 누수를 막기위한 함수입니다. 모든 Observable 을 삭제 및 디비 인스턴스도 삭제.
        if (!disposable.isDisposed) {
            disposable.dispose()
            Log.d("OnDestroy", "Main_Ac : CompositeDisposable() ->: Disposed")
        }
        loginDB?.destroyInstance()
    }

    fun GO_TO_LOGIN_ACTIVITY () {
        dispose()
        startActivity(Intent(this@MainActivity,Login_And_SignUP_Activity::class.java))
        this@MainActivity.finish()
    }

    fun Init_Fragment(navView: BottomNavigationView, myinfoModel: MyInfo_Model) {
        mainViewModel.Num.set(myinfoModel.Num)
        mainViewModel.ID.set(myinfoModel.ID)
        mainViewModel.Name.set(myinfoModel.Name)
        mainViewModel.Date.set(myinfoModel.Date)
        mainViewModel.Pic.set(myinfoModel.Pic)
        mainViewModel.Token.set(myinfoModel.Token)

        CONNECT(myinfoModel)
        homeFragment = HomeFragment()
        supportFragmentManager.beginTransaction().add(R.id.nav_host_fragment, homeFragment!!, HOME).commit()
        CurrentFragment = homeFragment

        navView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    if (supportFragmentManager.findFragmentByTag(HOME) != null) { // 해당 프래그먼트가 존재한다면 !
                        if (CurrentFragment == homeFragment) {
                            Log.d("newMainActivity", "newMainActivity : -> Home -> true ")

                        } else {
                            Log.d("newMainActivity", "newMainActivity : -> Home -> false ")
                            supportFragmentManager.beginTransaction().show(homeFragment!!).hide(CurrentFragment!!).commit()
                        }
                    } else {
                        homeFragment = HomeFragment()
                        supportFragmentManager.beginTransaction().add(R.id.nav_host_fragment, homeFragment!!, HOME).hide(CurrentFragment!!).commit()
                    }
                    CurrentFragment = homeFragment
                    return@setOnNavigationItemSelectedListener true
                }
//                ____________________________________________________________________________________________________________________________________________________________

                R.id.navigation_dashboard -> {
                    if (supportFragmentManager.findFragmentByTag(CHAT) != null) { // 해당 프래그먼트가 존재한다면 !
                        if (CurrentFragment == chatFragment) {

                        } else {
                            supportFragmentManager.beginTransaction().show(chatFragment!!).hide(CurrentFragment!!).commit()
                        }
                    } else {
                        chatFragment = ChatFragment()
                        supportFragmentManager.beginTransaction().add(R.id.nav_host_fragment, chatFragment!!, CHAT).hide(CurrentFragment!!).hide(CurrentFragment!!).commit()
                    }
                    CurrentFragment = chatFragment
                    return@setOnNavigationItemSelectedListener true
                }
//                ____________________________________________________________________________________________________________________________________________________________

                R.id.navigation_my_page -> {
                    if (supportFragmentManager.findFragmentByTag(MYPAGE) != null) {
                        if (CurrentFragment == myPageFragment) {

                        } else {
                            supportFragmentManager.beginTransaction().show(myPageFragment!!).hide(CurrentFragment!!).commit()
                        }
                    } else {
                        myPageFragment = MyPageFragment()
                        supportFragmentManager.beginTransaction().add(R.id.nav_host_fragment, myPageFragment!!, MYPAGE).hide(CurrentFragment!!).commit()
                    }
                    CurrentFragment = myPageFragment
                    return@setOnNavigationItemSelectedListener true
                }
//                ____________________________________________________________________________________________________________________________________________________________

                else -> { // Note the block
                    return@setOnNavigationItemSelectedListener false
                }
//                ____________________________________________________________________________________________________________________________________________________________

            }
            false
        }
    }
}