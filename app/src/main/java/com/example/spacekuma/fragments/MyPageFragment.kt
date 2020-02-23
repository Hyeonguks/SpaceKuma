package com.example.spacekuma.fragments

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.spacekuma.BR
import com.example.spacekuma.R
import com.example.spacekuma.activities.Edit_ProfileActivity
import com.example.spacekuma.activities.Login_And_SignUP_Activity
import com.example.spacekuma.databinding.FragmentMypageBinding
import com.example.spacekuma.view_models.main.MainViewModel

import kotlinx.android.synthetic.main.fragment_mypage.view.*


class MyPageFragment : Main_Base_Fragment<FragmentMypageBinding>() {
    override val layoutID: Int = R.layout.fragment_mypage
    override val BRName: Int = BR.mypage

    override fun Init() {
        bind.root.User_Image.background = ShapeDrawable(OvalShape())
        bind.root.User_Image.clipToOutline = true

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(bind.root.MyPage_toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_clear_black_24dp)
        (activity as AppCompatActivity).supportActionBar?.title = viewModel.ID.get()

        if (viewModel.Pic.get() == "0") {
            bind.root.User_Image.setImageResource(R.drawable.ic_0)
        } else {
            Glide.with(this@MyPageFragment)
                .load(getString(R.string.address)+viewModel.Pic.get())
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(bind.root.User_Image)
        }


        bind.root.Btn_Edit.setOnClickListener {
            startActivityForResult(Intent(activity!!, Edit_ProfileActivity::class.java)
                .putExtra("Num",viewModel.Num.get())
                .putExtra("ID",viewModel.ID.get())
                .putExtra("Name",viewModel.Name.get())
                .putExtra("Pic",viewModel.Pic.get())
                .putExtra("Date",viewModel.Date.get()),999)
        }

//        val textView: TextView = root.findViewById(R.id.text_mypage)
//        myPageViewModel.start.observe(this, Observer {
//            textView.text = it
//        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.mypage_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_account -> Toast.makeText(activity!!, "??", Toast.LENGTH_SHORT).show()
            R.id.menu_logout -> {
                val pref : SharedPreferences = activity!!.getSharedPreferences("LoginInfo",0)
                val editor: SharedPreferences.Editor = pref.edit()

                editor.clear()
                editor.apply()

                startActivity(Intent(activity!!, Login_And_SignUP_Activity::class.java))
                activity!!.finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 999 && resultCode == Activity.RESULT_OK) {
            viewModel.Name.set(data!!.getStringExtra("Name"))
        } else {

        }
    }
}