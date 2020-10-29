package com.example.spacekuma.activities

import android.content.Intent
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.spacekuma.GlideApp
import com.example.spacekuma.R
import kotlinx.android.synthetic.main.activity_user__profile.*

class User_ProfileActivity : AppCompatActivity() {
    private val ARG_NUM = "Num"
    private val ARG_ID = "ID"
    private val ARG_NAME = "Name"
    private val ARG_PIC = "Pic"
    private val ARG_DATE = "Date"
    private val ARG_TOKEN = "Token"

    private var Num: Int? = null
    private var ID: String? = null
    private var Name: String? = null
    private var Pic: String? = null
    private var Date: String? = null
    private var Token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user__profile)

        Num = intent.getIntExtra(ARG_NUM,0)
        ID = intent.getStringExtra(ARG_ID)
        Name = intent.getStringExtra(ARG_NAME)
        Pic = intent.getStringExtra(ARG_PIC)
        Date = intent.getStringExtra(ARG_DATE)
        Token = intent.getStringExtra(ARG_TOKEN)

        setSupportActionBar(userprofile_toolbar)
        supportActionBar?.title = ID
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        User_ID.text = ID
        User_Name.text = Name

        User_Pic.background = ShapeDrawable(OvalShape())
        User_Pic.clipToOutline = true

        if (Pic == "0") {
            User_Pic.setImageResource(R.drawable.ic_0)
        } else {
            GlideApp.with(this@User_ProfileActivity)
                .load(getString(R.string.address_media)+Pic)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(User_Pic)
        }


        Btn_Chat.setOnClickListener {
            startActivity(
                Intent(this@User_ProfileActivity,ChatActivity::class.java)
                .putExtra("RequestCode",111)
                    .putExtra("User_Num",Num!!)
                    .putExtra("User_ID",ID)
                    .putExtra("User_Name",Name)
                    .putExtra("User_Pic",Pic)
                    .putExtra("User_Date",Date)
                    .putExtra("User_Token",Token)

            )

        }

//        User_Feed_RecyclerView.adapter = Search_Adapter(this@User_ProfileActivity,mList)
//        User_Feed_RecyclerView.layoutManager = LinearLayoutManager(this@User_ProfileActivity)!!

    }

    // 툴바 뒤로가기 버튼
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
