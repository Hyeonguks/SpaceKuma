package com.example.spacekuma.fragments

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spacekuma.BR
import com.example.spacekuma.R
import com.example.spacekuma.activities.ChatActivity
import com.example.spacekuma.data.Chat_Message_Model
import com.example.spacekuma.data.Chat_Room_Model
import com.example.spacekuma.data.NewsFeed_Model
import com.example.spacekuma.databinding.FragmentChatBinding
import com.example.spacekuma.recycler_view_adapters.Chat_Room_Adapter
import com.example.spacekuma.retrofit.ApiClient
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.fragment_chat.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatFragment : Main_Base_Fragment<FragmentChatBinding>() {
    lateinit var chatRoomAdapter: Chat_Room_Adapter
    lateinit var manager : LinearLayoutManager

    var mList = arrayListOf<Chat_Room_Model>()

    override val layoutID: Int = R.layout.fragment_chat
    override val BRName: Int = BR.chat_fragment

    val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "newMessage" -> {
                    intent.getParcelableExtra<Chat_Message_Model>("newItem")
                    for (i in 0.. mList.size) {
                        if (mList[i].Room_Num == intent.getIntExtra("Room_Num",0)) {
                            mList[i].ChatList.add(intent.getParcelableExtra<Chat_Message_Model>("newItem")!!)
                            chatRoomAdapter.notifyItemChanged(i)
                            break
                        } else {

                        }
                    }
                    Log.e("Chat_Fragment"," onReceive -> newMessage")
                }

                "newChatRoom" -> {
                    mList.add(0,intent.getParcelableExtra<Chat_Room_Model>("newChatRoomItem")!!)
                    chatRoomAdapter.notifyItemInserted(0)
                }
            }
        }
    }

    override fun Init() {
        (activity as AppCompatActivity).setSupportActionBar(bind.root.Chat_Fragment_Toolbar)
        (activity as AppCompatActivity).supportActionBar?.title = "채팅"

        // https://stackoverflow.com/a/51047037
        setHasOptionsMenu(true)

        bind.root.Chat_Room_RecyclerView

        chatRoomAdapter = Chat_Room_Adapter(viewModel.Num.get()!!,activity!!,mList)
        manager = LinearLayoutManager(activity!!)
        bind.root.Chat_Room_RecyclerView.adapter = chatRoomAdapter
        bind.root.Chat_Room_RecyclerView.layoutManager = manager
        getRoomList(viewModel.Num.get()!!)
    }

    fun getRoomList(Num : Int) {
        // 메세지도 저장을 해야지 옵저버로 매세지확인가능.
        ApiClient.getClient.Get_Room_List(Num).enqueue(object : Callback<ArrayList<Chat_Room_Model>> {
            override fun onFailure(call: Call<ArrayList<Chat_Room_Model>>, t: Throwable) {
                Log.e("getRoomList", "onFailure : $t")
            }

            override fun onResponse(call: Call<ArrayList<Chat_Room_Model>>,response: Response<ArrayList<Chat_Room_Model>>) {
                if (response.isSuccessful && response.body()!!.isNotEmpty()) {
                    chatRoomAdapter.addNextItem(response.body()!!)
                    chatRoomAdapter.notifyDataSetChanged()
                    var filter = IntentFilter()
                    filter.addAction("newMessage")
                    filter.addAction("newChatRoom")
                    LocalBroadcastManager.getInstance(activity!!).registerReceiver(broadCastReceiver, filter)
                } else {
                    Log.e("getRoomList","is Not Success Message : $response")
                }
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 156 && resultCode == Activity.RESULT_OK) {
//            Log.e("onActivityResult", "Chat : 156")
//            for (i in 0 until mList.size) {
//                if (0 != data!!.getIntExtra("roomNum",0) && mList[i].Room_Num == data.getIntExtra("roomNum",0)) {
//                    mList[i].ChatList.clear()
//                    mList[i].ChatList.addAll(data.getParcelableArrayListExtra<Chat_Message_Model>("chatList")!!)
//                    chatRoomAdapter.notifyItemChanged(i)
//                    break
//                }
//            }
//        } else {
//            Log.e("onActivityResult", "Chat : else")
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.chatroomlist_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
//            R.id.menu_search -> startActivity(Intent(activity!!, SearchActivity::class.java))
//            R.id.menu_create -> startActivity(Intent(activity!!, SearchActivity::class.java))
            else -> null
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(broadCastReceiver)
        super.onDestroy()
    }
}