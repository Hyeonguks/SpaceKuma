package com.example.spacekuma.activities

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.spacekuma.GlideApp
import com.example.spacekuma.R
import kotlinx.android.synthetic.main.activity_invited.*
import kotlinx.android.synthetic.main.activity_invited.TextView_Message
import kotlinx.android.synthetic.main.activity_invited.TextView_UserName
import kotlinx.android.synthetic.main.activity_invited.User_Image

class InvitedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invited)

        MainActivity.mSocket.connected()

        MainActivity.mSocket.id()

        //소켓 연결이 안되어있다면 연결하고 상대방에게 소켓정보 전달
        intent.getIntExtra("Inviter_Num", 0)
        intent.getStringExtra("Inviter_Socket")
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
                .load(getString(R.string.address) + intent.getStringExtra("Inviter_Pic"))
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(User_Image)
        }
        TextView_UserName.text = intent.getStringExtra("Inviter_Name")

        TextView_Message.text = intent.getStringExtra("Inviter_Name") + " 님으로부터 영상통화 요청"

        Btn_Accept.setOnClickListener {

        }
        Btn_Deny.setOnClickListener {

        }
    }
}
