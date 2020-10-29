package com.example.spacekuma.activities

import android.app.Application
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
    /*
    1. HomeFragment,ChatFragment,MyPageFragment 현재는 3개 프래그먼트만 사용중이지만,
    NotificationsFragment 도 있었지만, 현재는 FCM 쪽 기능 구현이 부족하기때문 제작하지 않음.

    2. Socket.io 로 소켓 연결 중이다. 문제는 소켓아이오는 소켓아이디를 내부적에서 랜덤으로 고유한 아이디를 생성한다고 한다.
    그래서 검색해보니 소켓아이디를 사용자가 지정해줄수있나 찾아봤지만, 제대로 작동하지 않거나 권유하지 않는 방법이라한다.
    그러고보니 자바 소켓서버를 열때도 그랬었나.....?
    아무튼 굳이 소켓아이디를 지정하려던 이유는 소켓통신을 하려면 상대방 소켓을 알아야하기때문에 소켓아이디를 찾는 등
    코드가 길어지니까 사용자 아이디로 직접 정할 수 있다면 참 좋을 것 같다고 생각했는데 방법이 없는건가? 다음에 다시 찾아보자
     */

    private val HOME = "HOME"
    private val CHAT = "CHAT"
    private val NOTIFICATIONS = "NOTIFICATIONS"
    private val MYPAGE = "MYPAGE"

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

    fun CONNECT() {
        try {
            mSocket.connect()
            // emit 이벤트 발생시키기
            // on 구독할 이벤트
        } catch (e : URISyntaxException) {
            Log.e("MainActivity","CONNECT() -> error : ${e.reason}")
        }
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
        if (requestCode == 666) {
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
        mSocket.close()
        mSocket.disconnect()
        super.onDestroy()
    }

    fun dispose () {
        // 메모리 누수를 막기위한 함수입니다. 모든 Observable 을 삭제 및 디비 인스턴스도 삭제.
        if (!disposable.isDisposed) {
            disposable.dispose()
            Log.d("MainActivity", "CompositeDisposable() ->: Disposed")
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
        Log.d("MainActivity", "MainActivity : -> Init_Fragment : ${myinfoModel}")

        CONNECT()
        homeFragment = HomeFragment()
        supportFragmentManager.beginTransaction().add(R.id.nav_host_fragment, homeFragment!!, HOME).commit()
        CurrentFragment = homeFragment

        navView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    if (supportFragmentManager.findFragmentByTag(HOME) != null) { // 해당 프래그먼트가 존재한다면 !
                        if (CurrentFragment == homeFragment) {
                            Log.d("MainActivity", "MainActivity : -> Home -> true ")

                        } else {
                            Log.d("MainActivity", "MainActivity : -> Home -> false ")
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