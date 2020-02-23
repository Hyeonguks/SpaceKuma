package com.example.spacekuma.data

data class FeedDetail_Model(
    val Feed_Num : Int,
    val Writer_Num : Int,
    val Like_Count : Int,
    val Comment_Count : Int,
    val Feed_Text : String,
    val Uploaded_Date : String,
    val Writer_ID : String,
    val Writer_Name : String,
    val Writer_Pic : String,
    val Writer_Token : String,

    val Message : String,
    val Success : Boolean
)