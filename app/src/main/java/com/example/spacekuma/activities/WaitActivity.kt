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
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.spacekuma.GlideApp
import com.example.spacekuma.R
import com.example.spacekuma.data.Chat_Message_Model
import com.example.spacekuma.webrtc.VideoCallActivity
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_wait.*
import kotlinx.android.synthetic.main.chat_text_item.view.*
import org.json.JSONArray
import org.json.JSONObject

class WaitActivity : AppCompatActivity() {
    var obj : JSONObject = JSONObject()

    val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "video_CallDeny" -> {
                    Log.e("WaitActivity"," onReceive -> video_CallDeny")
                    if (intent.getBooleanExtra("deny",false)) {
                        dispose()
                    }
                }

                "video_CallAccept" -> {
                    Log.e("WaitActivity"," onReceive -> video_CallAccept")
                    if (intent.getBooleanExtra("accept",false)) {
                        startActivity(Intent(this@WaitActivity,VideoCallActivity::class.java).putExtra("Room",obj["Opener_Socket"].toString()))
                        dispose()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wait)

        if (intent.getIntExtra("Room_Num",0) == 0) {
            finish()
        }
        obj.put("Room_Num",intent.getIntExtra("Room_Num",0))

        obj.put("Opener_Num",intent.getIntExtra("Opener_Num",0))
        obj.put("Opener_Socket",MainActivity.mSocketName)
        obj.put("Opener_Name",intent.getStringExtra("Opener_Name"))
        obj.put("Opener_Token",intent.getStringExtra("Opener_Token"))
        obj.put("Opener_ID",intent.getStringExtra("Opener_ID"))
        obj.put("Opener_Pic",intent.getStringExtra("Opener_Pic"))
        obj.put("User_Pic",intent.getStringExtra("User_Pic"))
        obj.put("User_Token",intent.getStringExtra("User_Token"))
        obj.put("User_Num",intent.getIntExtra("User_Num",0))
        obj.put("User_Name",intent.getStringExtra("User_Name"))
        obj.put("User_ID",intent.getStringExtra("User_ID"))

        Log.d("Opener_Socket",MainActivity.mSocketName)

        MainActivity.mSocket.emit("Do_Call",obj)

        User_Image.background = ShapeDrawable(OvalShape())
        User_Image.clipToOutline = true

        if (intent.getStringExtra("User_Pic") == "0") {
            User_Image.setImageResource(R.drawable.ic_0)
        } else {
            GlideApp.with(this@WaitActivity)
                .load(getString(R.string.address_media)+intent.getStringExtra("User_Pic"))
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(User_Image)
        }

        TextView_UserName.text = intent.getStringExtra("User_Name")

        var filter = IntentFilter()
        filter.addAction("video_CallDeny")
        filter.addAction("video_CallAccept")
        LocalBroadcastManager.getInstance(this).registerReceiver(broadCastReceiver, filter)

        Btn_Cancle.setOnClickListener {
            var info = JSONObject()
            info.put("User_Token",intent.getStringExtra("User_Token"))
            info.put("Opener_Name",intent.getStringExtra("Opener_Name"))
            MainActivity.mSocket.emit("Do_Call_Cancel",info)
            finish()
        }

        TextView_Message
    }

    val onCallEvent: Emitter.Listener = Emitter.Listener {

    }

    override fun onDestroy() {
        dispose()
        MainActivity.mSocket.off("call_event",onCallEvent)
        super.onDestroy()
    }

    override fun onBackPressed() {

    }

    fun dispose () {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadCastReceiver)
        finish()
    }
}
