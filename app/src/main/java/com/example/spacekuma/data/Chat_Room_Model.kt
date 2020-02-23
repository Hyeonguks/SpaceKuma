package com.example.spacekuma.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "chat_room")
data class Chat_Room_Model(
    @PrimaryKey var Room_Num : Int,
    var View_Type : Int,
    var Joined_date : String,
    var Last_updated_date : String,
    @Ignore var Member : ArrayList<User_Model>,
    @Ignore var ChatList: ArrayList<Chat_Message_Model>,
    @Ignore val Success: Boolean,
    @Ignore val Message: String
) : Parcelable {
    constructor() : this(0, 0, "", "", ArrayList<User_Model>(),ArrayList<Chat_Message_Model>(),true,"")
}