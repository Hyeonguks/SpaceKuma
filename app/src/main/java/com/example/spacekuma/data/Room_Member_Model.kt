package com.example.spacekuma.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "room_member")
data class Room_Member_Model(
    @PrimaryKey val Num : Int,
    val User_Num : Int,
    val Room_Num : Int,
    val Joined_Date : String
)