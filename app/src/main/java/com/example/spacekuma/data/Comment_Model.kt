package com.example.spacekuma.data

data class Comment_Model(
    val Comment_Num : Int,
    val View_Type : Int,
    val Feed_Num : Int,
    val Parent_Num : Int,
    var ReComment_Count : Int,
    val Writer_Num : Int,
    var Like_Count : Int,

    var Liked : Int,
    val Uploaded_Date : String,

    val Comment_Text : String,
    val Writer_Name : String,
    val Writer_Pic : String,

    val Success : Boolean,
    val Message : String
)