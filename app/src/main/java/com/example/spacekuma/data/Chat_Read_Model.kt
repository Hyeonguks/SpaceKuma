package com.example.spacekuma.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_read")
data class Chat_Read_Model(
    @PrimaryKey val Num : Int,
    val Who_Read : Int,
    val Room_Num : Int,
    var What_Message : Int
)