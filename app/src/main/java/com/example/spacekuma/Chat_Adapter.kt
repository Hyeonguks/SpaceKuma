package com.example.spacekuma

import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableArrayList
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.spacekuma.data.Chat_Message_Model
import com.example.spacekuma.data.User_Model
import kotlinx.android.synthetic.main.chat_joined_item.view.*
import kotlinx.android.synthetic.main.chat_my_text_item.view.*

import kotlinx.android.synthetic.main.chat_text_item.view.*
import kotlinx.android.synthetic.main.chat_text_item.view.TextView_Message

class Chat_Adapter(val Num : Int, var Member : ArrayList<User_Model>, val context: Context, val chatList: ObservableArrayList<Chat_Message_Model>) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val TYPE_ONE = 1
        const val TYPE_TWO = 2
        const val TYPE_THREE = 3
        const val TYPE_HEADER = 999
        const val TYPE_LOADING = 777
    }

    inner class Text_ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(chatModel: Chat_Message_Model, context: Context) {
            Log.d("Chat_Adapter","Text_ViewHolder -> Uploaded_Date : "+chatModel.Uploaded_Date)
            itemView.TextView_User_Name.text = chatModel.User_Name
            itemView.TextView_Message.text = chatModel.Message
            itemView.TextView_Read_Count.text = (Member.size - chatModel.Read_Count).toString()

            itemView.User_Pic.background = ShapeDrawable(OvalShape())
            itemView.User_Pic.clipToOutline = true

            if (chatModel.User_Pic == "0") {
                itemView.User_Pic.setImageResource(R.drawable.ic_0)
            } else {
                GlideApp.with(context)
                    .load(context.getString(R.string.address)+chatModel.User_Pic)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(itemView.User_Pic)
            }
        }
    }

    inner class My_Text_ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(chatModel: Chat_Message_Model, context: Context) {
            Log.d("Chat_Adapter","My_Text_ViewHolder -> Uploaded_Date : "+chatModel.Uploaded_Date)
            itemView.TextView_Message.text = chatModel.Message
            itemView.TextView_MyMessage_Read_Count.text = (Member.size - chatModel.Read_Count).toString()
        }
    }

    inner class Join_ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(chatModel: Chat_Message_Model, context: Context) {
            itemView.TextView_Who_Joined.text = chatModel.Message
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ONE -> {
                Text_ViewHolder(
                    LayoutInflater.from(context)
                        .inflate(R.layout.chat_text_item, parent, false)
                )
            }

            TYPE_TWO -> {
                Join_ViewHolder(
                    LayoutInflater.from(context)
                        .inflate(R.layout.chat_joined_item, parent, false)
                )
            }

            TYPE_THREE -> {
                My_Text_ViewHolder(
                    LayoutInflater.from(context)
                        .inflate(R.layout.chat_my_text_item, parent, false)
                )
            }

            else -> throw IllegalArgumentException("Invalid view type")

        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is Join_ViewHolder -> {
                holder.itemView.run {
                    holder.bind(chatList[position],context)
                }
            }

            is Text_ViewHolder -> {
                holder.itemView.run {
                    holder.bind(chatList[position],context)
                }
            }

            is My_Text_ViewHolder -> {
                holder.itemView.run {
                    holder.bind(chatList[position],context)
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun getItemViewType(position: Int): Int {
        if (chatList[position].User_Num == Num) {
            return 3
        } else {
            return 1
        }
//        return when (chatList[position].View_Type) {
//            1 -> TYPE_ONE
//            2 -> TYPE_TWO
//            3 -> TYPE_THREE
//            999 -> TYPE_HEADER
//            777 -> TYPE_LOADING
//            else -> TYPE_ONE
//        }
    }


}