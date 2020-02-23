package com.example.spacekuma.recycler_view_adapters

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.spacekuma.GlideApp
import com.example.spacekuma.R
import com.example.spacekuma.activities.User_ProfileActivity

import com.example.spacekuma.data.User_Model

class Search_Adapter(val context: Context, val userList: ArrayList<User_Model>) :
    RecyclerView.Adapter<Search_Adapter.Text_ViewHolder>() {

    inner class Text_ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val User_Layout = itemView.findViewById<ConstraintLayout>(R.id.User_Layout)
        val User_Name = itemView.findViewById<TextView>(R.id.User_Name)
        val User_ID = itemView.findViewById<TextView>(R.id.User_ID)
        val User_Pic = itemView.findViewById<ImageView>(R.id.User_Pic)

        fun bind(userModel: User_Model, context: Context) {
            User_Name.text = userModel.Name
            User_ID.text = "@"+userModel.ID

            if (userModel.Pic == "0") {
                User_Pic.setImageResource(R.drawable.ic_0)
            } else {
                GlideApp.with(context)
                    .load(context.getString(R.string.address)+userModel.Pic)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(User_Pic)
            }

            User_Pic.background = ShapeDrawable(OvalShape())
            User_Pic.clipToOutline = true
            User_Layout.setOnClickListener {
                context.startActivity(Intent(context,User_ProfileActivity::class.java)
                    .putExtra("Num",userModel.Num)
                    .putExtra("ID",userModel.ID)
                    .putExtra("Name",userModel.Name)
                    .putExtra("Pic",userModel.Pic)
                    .putExtra("Date",userModel.Date)
                    .putExtra("Token",userModel.Token)
                )
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Text_ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.search_item, parent, false)
        return Text_ViewHolder(v)

    }

    override fun onBindViewHolder(holder: Text_ViewHolder, position: Int) {
        holder.bind(userList[position],context)
    }

    override fun getItemCount(): Int {
        return userList.size
    }


}