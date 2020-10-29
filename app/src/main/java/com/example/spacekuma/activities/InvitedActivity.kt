package com.example.spacekuma.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.spacekuma.Chat_Adapter
import com.example.spacekuma.GlideApp
import com.example.spacekuma.R
import com.example.spacekuma.data.Chat_Message_Model
import com.example.spacekuma.data.Chat_Room_Model
import com.example.spacekuma.webrtc.VideoCallActivity
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_invited.*
import kotlinx.android.synthetic.main.activity_invited.TextView_Message
import kotlinx.android.synthetic.main.activity_invited.TextView_UserName
import kotlinx.android.synthetic.main.activity_invited.User_Image
import org.json.JSONObject

class InvitedActivity : AppCompatActivity() {

    lateinit var inviter_socket : String

    val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "video_CallCancel" -> {
                    Log.e("InvitedActivity"," onReceive -> video_CallCancel")
                    intent.getBooleanExtra("cancel",false)
                    if (intent.getBooleanExtra("cancel",false)) {
                        dispose()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invited)

        MainActivity.mSocket.connected()

        MainActivity.mSocket.id()

        //소켓 연결이 안되어있다면 연결하고 상대방에게 소켓정보 전달
        intent.getIntExtra("Inviter_Num", 0)
        intent.getIntExtra("Inviter_Room_Num", 0)
        inviter_socket = intent.getStringExtra("Inviter_Socket")!!
        intent.getStringExtra("Inviter_ID")
        intent.getStringExtra("Inviter_Name")
        intent.getStringExtra("Inviter_Pic")
        intent.getStringExtra("Inviter_Token")

        Log.d("Inviter_Socket",intent.getStringExtra("Inviter_Socket")!!)

        User_Image.background = ShapeDrawable(OvalShape())
        User_Image.clipToOutline = true

        if (intent.getStringExtra("Inviter_Pic") == "0") {
            User_Image.setImageResource(R.drawable.ic_0)
        } else {
            GlideApp.with(this@InvitedActivity)
                .load(getString(R.string.address_media) + intent.getStringExtra("Inviter_Pic"))
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(User_Image)
        }
        TextView_UserName.text = intent.getStringExtra("Inviter_Name")

        TextView_Message.text = intent.getStringExtra("Inviter_Name") + " 님으로부터 영상통화 요청"

        var filter = IntentFilter()
        filter.addAction("video_CallCancel")
        LocalBroadcastManager.getInstance(this).registerReceiver(broadCastReceiver, filter)

        Btn_Accept.setOnClickListener {
            var info = JSONObject()
            info.put("Inviter_Token",intent.getStringExtra("Inviter_Token"))
            MainActivity.mSocket.emit("Do_Call_Accept",info)

            startActivity(Intent(this@InvitedActivity, VideoCallActivity::class.java).putExtra("Room",inviter_socket))
            dispose()
        }

        Btn_Deny.setOnClickListener {
            var info = JSONObject()
            info.put("Inviter_Token",intent.getStringExtra("Inviter_Token"))
            MainActivity.mSocket.emit("Do_Call_Deny",info)
            dispose()
        }
    }

    override fun onBackPressed() {

    }

    override fun onDestroy() {
        dispose()
        super.onDestroy()
    }

    fun dispose () {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadCastReceiver)
        finish()
    }
}
