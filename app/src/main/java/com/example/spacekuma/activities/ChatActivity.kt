package com.example.spacekuma.activities

import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.spacekuma.BR
import com.example.spacekuma.Chat_Adapter
import com.example.spacekuma.R
import com.example.spacekuma.data.Chat_Message_Model
import com.example.spacekuma.data.Chat_Room_Model
import com.example.spacekuma.data.MyInfo_Model
import com.example.spacekuma.data.User_Model
import com.example.spacekuma.databinding.ActivityChatBinding
import com.example.spacekuma.databinding.ActivityMainBinding
import com.example.spacekuma.db.LoginDB
import com.example.spacekuma.view_models.chat.Chat_ViewModel
import com.example.spacekuma.view_models.main.MainViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_edit__profile.*
import org.json.JSONArray
import org.json.JSONObject

//https://dev.to/medaymentn/creating-a-realtime-chat-app-with-android--nodejs-and-socketio-4o55

class ChatActivity : AppCompatActivity() {
    companion object {
        // 방 번호는 메세지 FCM 을 받을 때 현재 방번호를 확인하기 위해서다.
        // 근데 룸 디비에 넣기만하고 액티비티에서 옵저버로 데이터를 가져오면되는데 저게 필요한 방법인가;

//        var chatList = arrayListOf<Chat_Message_Model>()
        var Room_Num : Int = 0
    }

    var chatList = ObservableArrayList<Chat_Message_Model>()
    lateinit var chatViewModel : Chat_ViewModel

    lateinit var bind : ActivityChatBinding
    lateinit var mAdapter : Chat_Adapter

    lateinit var Check_Ob : Disposable
    private var loginDB: LoginDB? = null
    var disposable = CompositeDisposable()

    var User_Num : Int = 0
    lateinit var User_Name : String
    lateinit var User_ID : String
    lateinit var User_Pic : String
    lateinit var User_Date : String
    lateinit var User_Token : String
    lateinit var MyinfoModel: MyInfo_Model

    val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "newMessage" -> {
                    chatList.add(intent.getParcelableExtra<Chat_Message_Model>("newItem"))
                    mAdapter.notifyItemInserted(chatList.size -1)
                    recyclerView_chat.scrollToPosition(mAdapter.itemCount -1)
                    Log.e("ChatActivity"," onReceive -> newMessage")
                }
                "newChatRoom" -> {
                    var newItem = intent.getParcelableExtra<Chat_Room_Model>("newChatRoomItem")!!

                    if (Room_Num == 0 && newItem.ChatList[0].User_Num == MyinfoModel.Num) {
                        Room_Num = newItem.Room_Num
                        mAdapter = Chat_Adapter(MyinfoModel.Num,newItem.Member, this@ChatActivity, chatList)
                        recyclerView_chat.adapter = mAdapter
                        val lm = LinearLayoutManager(this@ChatActivity)
                        // 이게 스크롤 밑으로 고정해줌
                        lm.stackFromEnd = true
                        recyclerView_chat.layoutManager = lm
                        recyclerView_chat.setHasFixedSize(true)
                        chatList.add(newItem.ChatList[0])
                        mAdapter.notifyItemInserted(0)
                        Test_editText.text.clear()
                    } else {

                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = DataBindingUtil.setContentView(this@ChatActivity,R.layout.activity_chat)

        chatViewModel = ViewModelProviders.of(this@ChatActivity).get(Chat_ViewModel::class.java)
        bind.setVariable(BR.chat,chatViewModel)

        Check_Ob = LoginDB
            .getInstance(this)!!
            .loginDao()
            .getLoginInfo(Get_My_Num())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                // 구독시 사용할 함수
                Log.e("ChatActivity"," doOnSubscribe -> 구독 시작")
            }
            .doOnTerminate {
                // 구독이 끝날 때 사용할 함수
                Log.e("ChatActivity"," doOnTerminate -> 구독 종료")
            }
            .subscribe({
                when (it) {
                    null -> finish()
                    else -> Init(it)
                }
            },{
                Log.e("ChatActivity"," Throwable -> "+ it.message)
            })
        disposable.add(Check_Ob)
    }

    fun Init (myinfoModel: MyInfo_Model) {
        MyinfoModel = myinfoModel
        setSupportActionBar(Chat_Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // from room list
        if (intent.getIntExtra("RequestCode",0) == 222) {
            supportActionBar?.title = intent.getStringExtra("Room_Name")

            intent.getParcelableArrayListExtra<User_Model>("Member")

            Room_Num = intent.getIntExtra("Room_Num",0)

            Log.d("Chat_Activity", "Room_Num : $Room_Num")

            chatList.addAll(intent.getParcelableArrayListExtra<Chat_Message_Model>("Chat_List")!!)

            mAdapter = Chat_Adapter(MyinfoModel.Num,intent.getParcelableArrayListExtra<User_Model>("Member")!!, this@ChatActivity, chatList)

            recyclerView_chat.adapter = mAdapter
            val lm = LinearLayoutManager(this@ChatActivity)
            // 이게 스크롤 밑으로 고정해줌
            lm.stackFromEnd = true
            recyclerView_chat.layoutManager = lm
            recyclerView_chat.setHasFixedSize(true)

            var filter = IntentFilter()
            filter.addAction("newMessage")
            filter.addAction("newChatRoom")
            LocalBroadcastManager.getInstance(this).registerReceiver(broadCastReceiver, filter)

            // from user profile
        } else if (intent.getIntExtra("RequestCode",0) == 111){
            supportActionBar?.title = intent.getStringExtra("User_Name")

            User_Num = intent.getIntExtra("User_Num",0)
            User_Name = intent.getStringExtra("User_Name")!!
            User_ID = intent.getStringExtra("User_ID")!!
            User_Pic = intent.getStringExtra("User_Pic")!!
            User_Date = intent.getStringExtra("User_Date")!!
            User_Token = intent.getStringExtra("User_Token")!!

            var filter = IntentFilter()
            filter.addAction("newMessage")
            filter.addAction("newChatRoom")
            LocalBroadcastManager.getInstance(this).registerReceiver(broadCastReceiver, filter)
            //from group talk
        } else if (intent.getIntExtra("RequestCode",0) == 333) {

        } else {

        }

        Test_Btn.setOnClickListener {
            if (Room_Num == 0) {
                Create_Room(myinfoModel,Test_editText.text.toString())
            } else {
                val message = JSONObject()
                message.put("Room",Room_Num)
                message.put("Message",Test_editText.text.toString())
                message.put("View_Type",1)
                message.put("User_Num",myinfoModel.Num)
                message.put("User_Name",myinfoModel.Name)
                message.put("User_Pic",myinfoModel.Pic)

                MainActivity.mSocket.emit("NewMessage",message)
                Test_editText.text.clear()
            }
        }
    }

    fun Create_Room(myinfoModel: MyInfo_Model,message : String) {
        var obj : JSONObject = JSONObject()
        var jsonArray : JSONArray = JSONArray()

        jsonArray.put(JSONObject()
            .put("user_num",User_Num)
            .put("user_id",User_ID)
            .put("user_name",User_Name)
            .put("user_pic",User_Pic)
            .put("user_date",User_Date)
            .put("user_token",User_Token))
        jsonArray.put(JSONObject()
            .put("user_num",myinfoModel.Num)
            .put("user_id",myinfoModel.ID)
            .put("user_name",myinfoModel.Name)
            .put("user_pic",myinfoModel.Pic)
            .put("user_date",myinfoModel.Date)
            .put("user_token",myinfoModel.Token))

        obj.put("View_Type",1)
        obj.put("User_Name",User_Name)
        obj.put("Member",jsonArray)
        obj.put("Num",myinfoModel.Num)
        obj.put("Name",myinfoModel.Name)
        obj.put("Pic",myinfoModel.Pic)
        obj.put("Message",message)

        obj.put("Message",message)
        obj.put("View_Type",1)
        obj.put("User_Num",myinfoModel.Num)
        obj.put("User_Name",myinfoModel.Name)
        obj.put("User_Pic",myinfoModel.Pic)
        obj.put("Member",jsonArray)

        MainActivity.mSocket.emit("CreateRoom",obj)

//        MainActivity.mSocket.off("createdroom",onCreateRoom)
    }

    override fun onDestroy() {
        dispose()
        super.onDestroy()
    }

    fun dispose () {
//        setResult(RESULT_OK,intent.putExtra("chatList",chatList).putExtra("roomNum", Room_Num))
        Room_Num = 0
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadCastReceiver)
        // 메모리 누수를 막기위한 함수입니다. 모든 Observable 을 삭제 및 디비 인스턴스도 삭제.
        if (!disposable.isDisposed) {
            disposable.dispose()
            Log.d("OnDestroy", "Chat_Ac : CompositeDisposable() ->: Disposed")
        }
        loginDB?.destroyInstance()
        finish()
    }

    fun Get_My_Num () : Int{
        val pref : SharedPreferences = getSharedPreferences("LoginInfo",0)
        return pref.getInt("Num",0)
    }

    // 툴바 메뉴 인플레이터
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_activity_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    // 툴바 메뉴 클릭 이벤트 정의
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_video_call -> {
                if (Room_Num != 0) {
                    for (i in 0.. mAdapter.Member.size) {
                        if (mAdapter.Member[i].Num != MyinfoModel.Num) {
                            startActivity(Intent(this@ChatActivity,WaitActivity::class.java)
                                .putExtra("Room_Num"     ,Room_Num)
                                .putExtra("Opener_Num"   ,MyinfoModel.Num)
                                .putExtra("Opener_Name"  ,MyinfoModel.Name)
                                .putExtra("Opener_Token" ,MyinfoModel.Token)
                                .putExtra("Opener_ID"    ,MyinfoModel.ID)
                                .putExtra("Opener_Pic"   ,MyinfoModel.Pic)
                                .putExtra("User_Pic"     ,mAdapter.Member[i].Pic)
                                .putExtra("User_Token"   ,mAdapter.Member[i].Token)
                                .putExtra("User_Num"     ,mAdapter.Member[i].Num)
                                .putExtra("User_Name"    ,mAdapter.Member[i].Name)
                                .putExtra("User_Date"    ,mAdapter.Member[i].Date)
                                .putExtra("User_ID"      ,mAdapter.Member[i].ID))
                            break
                        }
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 툴바 뒤로가기 버튼
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        dispose()
    }

}