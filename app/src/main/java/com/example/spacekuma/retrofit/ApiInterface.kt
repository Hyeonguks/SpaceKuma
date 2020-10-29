package com.example.spacekuma.retrofit

import com.example.spacekuma.data.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*
interface ApiInterface {
    @FormUrlEncoded
    @POST("test.php")
    fun TEST (
        @Field("test") User_Num : Boolean
    ) : Call<Check_Model>

    @FormUrlEncoded
    @POST("check_id.php")
    fun Check_ID (
        @Field("id") id : String
    ) : Call<Check_Model>

    @FormUrlEncoded
    @POST("sign_up.php")
    fun SignUP (
        @Field("id") id : String,
        @Field("pw") pw : String,
        @Field("name") name : String,
        @Field("token") token : String
    ) : Call<Login_Model>

    @FormUrlEncoded
    @POST("update_fcm_token.php")
    fun UpdateFcm (
        @Field("num") num : Int,
        @Field("token") token : String
    ) : Call<Check_Model>

    @FormUrlEncoded
    @POST("login.php")
    fun Login (
        @Field("id") id : String,
        @Field("pw") pw : String,
        @Field("token") token : String
    ) : Call<Login_Model>

    @FormUrlEncoded
    @POST("get_my_feed.php")
    fun Get_More_Feed (
        @Field("Last_Feed_Num") ListSize : Int,
        @Field("User_Num") User_Num : Int
    ) : Call<ArrayList<NewsFeed_Model>>


    @FormUrlEncoded
    @POST("search_user.php")
    fun Search_User (
        @Field("Name") Name : String,
        @Field("ListSize") ListSize : Int
    ) : Call<Search_Model>

    @FormUrlEncoded
    @POST("update_profile.php")
    fun Edit_Name (
        @Field("User_Num") Num : Int,
        @Field("User_ID") ID : String,
        @Field("User_Name") Name : String
    ) : Call<Check_Model>

    @Multipart
    @POST("update_profile.php")
    fun Edit_Profile (
        @Part("User_Num") Num : Int,
        @Part("User_ID") ID : String,
        @Part("User_Name") Name : String,
        @Part Image : MultipartBody.Part?
    ) : Call<Check_Model>

    @Multipart
    @POST("upload_feed_item.php")
    fun Upload_Feed (
//        feed detail 부분도 전달해야함.
        @Part("Type") Feed_Type : Int,
        @Part("Media_Size") Media_Size : Int,
        @Part("Writer_Num") Writer : Int,
        @Part("Feed_Text") Text : String,
        @PartMap Media_Uri_List : HashMap<String,Feed_Media_Uri_Model>,
        @Part Feed_Media : ArrayList<MultipartBody.Part>
    ) : Call<Check_Model>

    @FormUrlEncoded
    @POST("update_feed_like.php")
    fun Update_Feed_Like (
        @Field("Feed_Num") Feed_Num : Int,
        @Field("Who_Clicked") Who_Clicked : Int
    ) : Call<Check_Model>

    @FormUrlEncoded
    @POST("update_feed_unlike.php")
    fun Update_Feed_UnLike (
        @Field("Feed_Num") Feed_Num : Int,
        @Field("Who_Clicked") Who_Clicked : Int
    ) : Call<Check_Model>

    @FormUrlEncoded
    @POST("get_feed_detail.php")
    fun Get_Feed_Detail_And_Writer_Info (
        @Field("Feed_Num") Feed_Num : Int
    ) : Call<FeedDetail_Model>

    @FormUrlEncoded
    @POST("upload_feed_comment.php")
    fun Upload_Comment (
        @Field("Feed_Num") Feed_Num : Int,
        @Field("View_Type") View_Type : Int,
        @Field("Writer_Num") Writer_Num : Int,
        @Field("ReComment_Num") Comment_Num : Int,
        @Field("Comment_Text") Comment_Text : String
    ) : Call<Check_Model>

    @FormUrlEncoded
    @POST("get_comment_list.php")
    fun Get_Comment_List (
        @Field("Feed_Num") Feed_Num : Int,
        @Field("User_Num") User_Num : Int,
        @Field("Last_Comment_Num") Last_Comment_Num : Int
    ) : Call<ArrayList<Comment_Model>>

    @FormUrlEncoded
    @POST("get_recomment_list.php")
    fun Get_ReComment_List (
        @Field("Feed_Num") Feed_Num : Int,
        @Field("User_Num") User_Num : Int,
        @Field("Parent_Comment_Num") Parent_Comment_Num : Int,
        @Field("Last_ReComment_Num") Last_Comment_Num : Int
    ) : Call<ArrayList<Comment_Model>>

    @FormUrlEncoded
    @POST("update_comment_like.php")
    fun Update_Comment_Like (
        @Field("Comment_Num") Comment_Num : Int,
        @Field("Who_Clicked") Who_Clicked : Int
    ) : Call<Check_Model>

    @FormUrlEncoded
    @POST("update_comment_unlike.php")
    fun Update_Comment_UnLike (
        @Field("Comment_Num") Comment_Num : Int,
        @Field("Who_Clicked") Who_Clicked : Int
    ) : Call<Check_Model>

    @FormUrlEncoded
    @POST("get_room_list.php")
    fun Get_Room_List (
        @Field("Num") user_Num: Int
    ) : Call<ArrayList<Chat_Room_Model>>
//
//    @Headers("Authorization: key=AAAAPo3Af2Y:APA91bFuScy8_Ng3rkVnvBnIFzj3aIhjqMzJce-bTSQQd6DLxD2GlbMB8NQJT8Nv5__HnTWbD4klj0E_Karcxrw-oNwZwyKK67Rto4R04AyQEwZQWwehaTk6WB_KuL9Z8QlmE0ybX3ip",
//            "Content-Type:application/json")
//    @POST("fcm/send")
//    fun Send_FCM (
//        @Body body:NotificationRequest
//    ) : Call<ResponseBody>

    @FormUrlEncoded
    @POST("update_feed_item.php")
    fun Update_Feed_Item (
        @Field("Feed_Num") feedNum: Int,
        @Field("Feed_Text") feedText: String
    ) : Call<Check_Model>

    @FormUrlEncoded
    @POST("delete_feed_item.php")
    fun Delete_Feed_Item (
        @Field("Feed_Num") feedNum: Int
    ) : Call<Check_Model>

    @FormUrlEncoded
    @POST("update_comment_item.php")
    fun Update_Comment_Item (
        @Field("Comment_Num") commentNum: Int,
        @Field("Comment_Text") commentText: String
    ) : Call<Check_Model>

    @FormUrlEncoded
    @POST("delete_comment_item.php")
    fun Delete_Comment_Item (
        @Field("Comment_Num") feedNum: Int,
        @Field("View_Type") viewType: Int
    ) : Call<Check_Model>



//    @Multipart
//    @FormUrlEncoded
//    @POST("edit_profile.php")
//    fun Edit_Profile (
//        @Field("Num") Num : Int,
//        @Field("Name") Name : String,
//        @Field("Pic") Pic : String,
//        @PartMap Image : HashMap<String,RequestBody>
//    ) : Call<Check_Model>


}

