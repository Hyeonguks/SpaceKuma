package com.example.spacekuma.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "user_account")
data class User_Model(
    @PrimaryKey val Num :Int,
    val ID : String,
    val Name :String,
    val Pic : String,
    val Date : String,
    val Token : String
) : Parcelable


/*
친구 목록및 게시글을 올린 사람의 정보 볼때 유용할듯.
 */