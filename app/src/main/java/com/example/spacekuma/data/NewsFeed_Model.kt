package com.example.spacekuma.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NewsFeed_Model(
    val Feed_Num: Int, // feed
    val View_Type: Int, // feed
    val Writer_Num: Int, // user_account
    var Like_Count: Int, // count
    val Comment_Count: Int, //count
    var Liked: Int,

    var Limit_Comment: Boolean,
    val Success: Boolean,

    val Writer_ID: String, // user_account
    val Writer_Name: String, // user_account //feed
    val Writer_Pic: String, // user_account
    val Feed_Media_Uri: ArrayList<Feed_Media_Uri_Model>?, // feed_media
    val Uploaded_Date: String, // feed
    val Feed_Text: String, // feed
    val Message: String

) : Parcelable
/*
_________  NewsFeed  _________

1. Num = int -> 게시물 번호
2. View_Type = int -> 뷰타입
3. Like_Count = Int -> 좋아요 개수
4. Comment_Count = Int -> 댓글 개수
__________   Int   ___________

5. Stop_Comment = Boolean -> 댓글 막기
__________ Boolean ___________

6. User_ID = String -> 게시자 ID
7. User_Pic = String -> 게시자 프로필 사진
8. feed_Media_Uri_Image_Uri = <String> -> 게시물 사진
9. Uploaded_Date = String -> 게시물 업로드 날짜
10. Feed_Text = String -> 게시글 내용
__________ String ___________
 */
