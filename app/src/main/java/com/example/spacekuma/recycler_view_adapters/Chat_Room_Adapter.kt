package com.example.spacekuma.recycler_view_adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.spacekuma.GlideApp
import com.example.spacekuma.R
import com.example.spacekuma.activities.ChatActivity
import com.example.spacekuma.activities.Comment_Activity
import com.example.spacekuma.data.Chat_Room_Model
import com.example.spacekuma.data.NewsFeed_Model
import kotlinx.android.synthetic.main.activity_edit__profile.*
import kotlinx.android.synthetic.main.chat_room_private_item.view.*


class Chat_Room_Adapter(val myNum : Int,var context: Context ,var roomList : ArrayList<Chat_Room_Model>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val TYPE_ONE = 1
        const val TYPE_TWO = 2
        const val TYPE_HEADER = 999
        const val TYPE_LOADING = 777

        const val FROM_ROOM_LIST = 222
    }

    inner class Private_Chat_ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(chatRoomModel: Chat_Room_Model, context: Context) {
            var room_Name = ""
            itemView.User_Pic.background = ShapeDrawable(OvalShape())
            itemView.User_Pic.clipToOutline = true

            for (i in chatRoomModel.Member) {
                if (i.Num != myNum) {
                    room_Name = i.Name
                    itemView.User_Name.text = i.Name
                    if (i.Pic == "0") {
                        itemView.User_Pic.setImageResource(R.drawable.ic_0)
                    } else {
                        GlideApp.with(context)
                            .load(context.getString(R.string.address)+i.Pic)
                            .centerCrop()
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(itemView.User_Pic)
                    }
                    break
                }
            }
            itemView.Not_Read_Message_Count.text = chatRoomModel.ChatList.size.toString()
            itemView.Last_Message.text = chatRoomModel.ChatList[chatRoomModel.ChatList.size - 1].Message
            itemView.Last_Message_Date.text = chatRoomModel.ChatList[chatRoomModel.ChatList.size - 1].Uploaded_Date

            itemView.Con_Root.setOnClickListener {
                (context as Activity).startActivityForResult(Intent(context,ChatActivity::class.java)
                    .putExtra("RequestCode",FROM_ROOM_LIST)
                    .putExtra("Room_Name",room_Name)
                    .putExtra("Room_Num",chatRoomModel.Room_Num)
                    .putExtra("Chat_List",chatRoomModel.ChatList)
                    .putExtra("Member",chatRoomModel.Member),156)
            }

        }

    }

    inner class Group_Chat_ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(chatRoomModel: Chat_Room_Model, context: Context) {
            itemView.User_Pic
            itemView.User_Name
            itemView.Last_Message
            itemView.Last_Message_Date
            itemView.Not_Read_Message_Count

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ONE-> {
                Private_Chat_ViewHolder(
                    LayoutInflater.from(context)
                        .inflate(R.layout.chat_room_private_item, parent, false)
                )
            }

            TYPE_TWO -> {
                Group_Chat_ViewHolder(
                    LayoutInflater.from(context)
                        .inflate(R.layout.chat_room_group_item, parent, false)
                )
            }

            else -> throw IllegalArgumentException("Invalid view type")

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is Private_Chat_ViewHolder -> {
                holder.itemView.run {
                    holder.bind(roomList[position],context)
                }
            }

            is Group_Chat_ViewHolder -> {
                holder.itemView.run {
                    holder.bind(roomList[position],context)
                }
            }

        }
    }

    fun addNextItem (NextItem: ArrayList<Chat_Room_Model>) {
        roomList.addAll(NextItem)
    }

    override fun getItemCount(): Int {
        return roomList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (roomList[position].View_Type) {
            1 -> TYPE_ONE
            2 -> TYPE_TWO
            999 -> TYPE_HEADER
            777 -> TYPE_LOADING
            else -> TYPE_ONE
        }
    }
}