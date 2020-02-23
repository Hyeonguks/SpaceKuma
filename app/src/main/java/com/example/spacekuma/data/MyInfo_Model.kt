package com.example.spacekuma.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


@Entity(tableName = "login_info")
data class MyInfo_Model(
    @PrimaryKey val Num :Int,
    val ID : String,
    val PassWord :String,
    val Name :String,
    val Pic : String,
    val Date : String,
    val Token : String
)

/*

*Login_Model 에 필요한 모델
여러가지 계정으로 로그인할수있으니 냅두자.

val AutoLogin : Boolean,
    val Success: Boolean,
    val Message: String
 */