package com.example.spacekuma.activities

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.spacekuma.GlideApp
import com.example.spacekuma.R
import com.example.spacekuma.data.Chat_Message_Model
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_wait.*
import kotlinx.android.synthetic.main.chat_text_item.view.*
import org.json.JSONArray
import org.json.JSONObject

class WaitActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wait)

        if (intent.getIntExtra("Room_Num",0) == 0) {
            finish()
        }

        var obj : JSONObject = JSONObject()
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

        MainActivity.mSocket.emit("do_call",obj)

        User_Image.background = ShapeDrawable(OvalShape())
        User_Image.clipToOutline = true

        if (intent.getStringExtra("User_Pic") == "0") {
            User_Image.setImageResource(R.drawable.ic_0)
        } else {
            GlideApp.with(this@WaitActivity)
                .load(getString(R.string.address)+intent.getStringExtra("User_Pic"))
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(User_Image)
        }

        TextView_UserName.text = intent.getStringExtra("User_Name")

        Btn_Cancle.setOnClickListener {
            finish()
//            MainActivity.mSocket.emit("do_call_cancle",obj)
        }

        TextView_Message
    }

    val onCallEvent: Emitter.Listener = Emitter.Listener {

    }

    override fun onDestroy() {
        MainActivity.mSocket.off("call_event",onCallEvent)
//        dispose()
        super.onDestroy()
    }

    override fun onBackPressed() {

    }
}
