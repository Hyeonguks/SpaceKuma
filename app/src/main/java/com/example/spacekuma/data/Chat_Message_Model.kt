package com.example.spacekuma.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "chat_message")
data class Chat_Message_Model(
    @PrimaryKey val Num : Int?,
    val View_Type : Int,
    val Room_Num : Int,
    val User_Num : Int,
    var Read_Count : Int,
    var User_Name : String,
    val User_Pic : String,
    val Message : String,
    val Uploaded_Date : String
) : Parcelable