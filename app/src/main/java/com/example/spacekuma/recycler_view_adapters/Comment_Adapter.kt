package com.example.spacekuma.recycler_view_adapters

import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.spacekuma.GlideApp
import com.example.spacekuma.R
import com.example.spacekuma.data.Check_Model
import com.example.spacekuma.data.Comment_Model
import com.example.spacekuma.data.FeedDetail_Model
import com.example.spacekuma.data.NewsFeed_Model
import com.example.spacekuma.retrofit.ApiClient
import com.example.spacekuma.util.TimeString
import kotlinx.android.synthetic.main.news_feed_comment_header_item.view.*
import kotlinx.android.synthetic.main.news_feed_comment_item.view.*
import kotlinx.android.synthetic.main.news_feed_comment_item.view.ImageView_Writer_Pic
import kotlinx.android.synthetic.main.news_feed_comment_item.view.TextView_Uploaded_Date
import kotlinx.android.synthetic.main.news_feed_comment_item.view.TextView_User_Name
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Comment_Adapter(val Feed_Num : Int,val Num : Int, val context: Context, val commentList: ArrayList<Comment_Model>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val TYPE_ONE = 1
        const val TYPE_TWO = 2
        const val TYPE_HEADER = 999
        const val TYPE_LOADING = 777
    }

    interface ItemClick{
        fun onReplyClick(view : View, feedNum: Int, Comment_Num : Int,Writer_Name : String, Comment_Text : String)
    }

    var itemClick : ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                Header_ViewHolder(
                    LayoutInflater.from(context)
                        .inflate(R.layout.news_feed_comment_header_item, parent, false)
                )
            }

            TYPE_ONE -> {
                Comment_ViewHolder(
                    LayoutInflater.from(context)
                        .inflate(R.layout.news_feed_comment_item, parent, false)
                )
            }

            TYPE_TWO -> {
                ReComment_ViewHolder(
                    LayoutInflater.from(context)
                        .inflate(R.layout.news_feed_recomment_item, parent, false)
                )
            }

            TYPE_LOADING -> {
                Loading_ViewHolder(
                    LayoutInflater.from(context)
                        .inflate(R.layout.news_feed_loading_item, parent, false)
                )
            }

            else -> throw IllegalArgumentException("Invalid view type")

        }
    }

    inner class Header_ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(commentModel: Comment_Model , context: Context) {
            itemView.ImageView_Writer_Pic.background = ShapeDrawable(OvalShape())
            itemView.ImageView_Writer_Pic.clipToOutline = true

            ApiClient.getClient.Get_Feed_Detail_And_Writer_Info(Feed_Num).enqueue(object :Callback<FeedDetail_Model> {
                override fun onFailure(call: Call<FeedDetail_Model>, t: Throwable) {
                    Log.e("onFailure","$t")
                }

                override fun onResponse(call: Call<FeedDetail_Model>,response: Response<FeedDetail_Model>) {
                    if (response.isSuccessful) {
                        if (response.body()!!.Success) {
                            itemView.TextView_User_Name.text = response.body()!!.Writer_Name
                            itemView.TextView_Feed_Text.text = response.body()!!.Feed_Text
                            itemView.TextView_Uploaded_Date.text = TimeString.formatTimeString(response.body()!!.Uploaded_Date)

                            if (response.body()!!.Writer_Pic == "0") {
                                itemView.ImageView_Writer_Pic.setImageResource(R.drawable.ic_0)
                            } else {
                                GlideApp.with(context)
                                    .load(context.getString(R.string.address)+response.body()!!.Writer_Pic)
                                    .centerCrop()
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .into(itemView.ImageView_Writer_Pic)
                            }
                        } else {

                        }
                    } else {

                    }
                }

            })
        }
    }

    inner class Comment_ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(commentModel: Comment_Model, context: Context) {
            Log.e("Comment_ViewHolder",commentModel.Liked.toString())
            itemView.ImageView_Writer_Pic.background = ShapeDrawable(OvalShape())
            itemView.ImageView_Writer_Pic.clipToOutline = true

            itemView.TextView_User_Name.text = commentModel.Writer_Name
            itemView.TextView_Uploaded_Date.text = TimeString.formatTimeString(commentModel.Uploaded_Date)
            itemView.TextView_Like_Count.text = set_LikeCount_Text(commentModel.Like_Count,itemView.TextView_Like_Count)
            itemView.TextView_Recomment_Count.text = ReComment_CountView(commentModel,commentModel.ReComment_Count,itemView.Con_Get_ReComment)
            itemView.TextView_Comment.text = commentModel.Comment_Text

            setUserProfile_Pic(itemView.ImageView_Writer_Pic,context,commentModel.Writer_Pic)
            Was_Liked(itemView.ImageView_Before_Like,itemView.ImageView_After_Like,commentModel.Liked,Num)

            // 답글 가져오기
            itemView.Con_Get_ReComment.setOnClickListener {

            }

            // 답글 달기
            itemView.TextView_ReComment_Btn.setOnClickListener {
                itemClick?.onReplyClick(it,commentModel.Feed_Num,commentModel.Comment_Num,commentModel.Writer_Name,commentModel.Comment_Text)
            }

            // 대댓글 불러오기 클릭하면 2개씩 불러옴. 그렇다면 대댓글 개수도 2씩 빼줘야함.
            itemView.Con_Get_ReComment.setOnClickListener {
                Log.d("Get_ReComment","Feed_Num :" +Feed_Num+" Num :"+Num.toString()+" adapterPosition :"+adapterPosition+ " Comment_Num : "+commentModel.Comment_Num)

                Get_ReComment_Item(Feed_Num,
                    Num,
                    adapterPosition,
                    commentModel.Comment_Num,
                    Check_Last_ReComment(adapterPosition),
                    commentModel)
            }

            itemView.ImageView_Before_Like.setOnClickListener {
                Comment_Like(commentModel.Comment_Num,Num,itemView.ImageView_Before_Like,itemView.ImageView_After_Like,itemView.TextView_Like_Count,commentModel)
            }

            itemView.ImageView_After_Like.setOnClickListener {
                Comment_UnLike(commentModel.Comment_Num,Num,itemView.ImageView_Before_Like,itemView.ImageView_After_Like,itemView.TextView_Like_Count,commentModel)
            }

        }
    }

    inner class ReComment_ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(commentModel: Comment_Model, context: Context) {
            itemView.ImageView_Writer_Pic.background = ShapeDrawable(OvalShape())
            itemView.ImageView_Writer_Pic.clipToOutline = true

            itemView.TextView_User_Name.text = commentModel.Writer_Name
            itemView.TextView_Uploaded_Date.text = TimeString.formatTimeString(commentModel.Uploaded_Date)
            itemView.TextView_Like_Count.text = set_LikeCount_Text(commentModel.Like_Count,itemView.TextView_Like_Count)
            itemView.TextView_Comment.text = commentModel.Comment_Text

            setUserProfile_Pic(itemView.ImageView_Writer_Pic,context,commentModel.Writer_Pic)

            Was_Liked(itemView.ImageView_Before_Like,itemView.ImageView_After_Like,commentModel.Liked,Num)

            // 답글 달기
            itemView.TextView_ReComment_Btn.setOnClickListener {
                itemClick?.onReplyClick(it,commentModel.Feed_Num,commentModel.Parent_Num,commentModel.Writer_Name,commentModel.Comment_Text)
            }

            itemView.ImageView_Before_Like.setOnClickListener {
                Comment_Like(commentModel.Comment_Num,Num,itemView.ImageView_Before_Like,itemView.ImageView_After_Like,itemView.TextView_Like_Count,commentModel)
            }

            itemView.ImageView_After_Like.setOnClickListener {
                Comment_UnLike(commentModel.Comment_Num,Num,itemView.ImageView_Before_Like,itemView.ImageView_After_Like,itemView.TextView_Like_Count,commentModel)
            }
        }
    }

    inner class Loading_ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(commentModel: Comment_Model, context: Context) {

        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is Header_ViewHolder -> {
                holder.itemView.run {
                    holder.bind(commentList[position],context)
                }
            }

            is Comment_ViewHolder -> {
                holder.itemView.run {
                    holder.bind(commentList[position],context)
                }
            }

            is ReComment_ViewHolder -> {
                holder.itemView.run {
                    holder.bind(commentList[position],context)
                }
            }

            is Loading_ViewHolder -> {
                holder.itemView.run {
                    holder.bind(commentList[position],context)
                }
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (commentList[position].View_Type) {
            1 -> TYPE_ONE
            2 -> TYPE_TWO
            999 -> TYPE_HEADER
            777 -> TYPE_LOADING
            else -> TYPE_ONE
        }
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    fun addHeaderItem() {
        commentList.add(Comment_Model(0, TYPE_HEADER,0,
            0,0,0,0,0,
            "","","","",true,""))
    }

    fun addNextItem (NextItem: ArrayList<Comment_Model>) {
        commentList.addAll(NextItem)
    }

    fun addLoadingItem() {
        commentList.add(Comment_Model(0, TYPE_LOADING,0,
            0,0,0,0,0,
            "","","","",true,""))
        notifyItemInserted(commentList.size - 1)
    }

    fun removeLoadingItem() {
        commentList.removeAt(commentList.size-1)
    }

    fun clear() {
        commentList.clear()
    }

    // 해당 댓글에 답글이 몇개가 있는지에 따라 적절한 문자열을 반환해줍니다.
    fun ReComment_CountView(commentModel: Comment_Model,recomment_Count : Int, con : ConstraintLayout) : String {
        if (recomment_Count == 0) {
            con.visibility = View.GONE
            return ""
        } else if (recomment_Count < 0) {
            con.visibility = View.GONE
            commentModel.ReComment_Count = 0
            return ""
        } else {
            con.visibility = View.VISIBLE
            return "답글 보기(${recomment_Count}개)"
        }
    }

    // 대댓글을 더 불러오기 위해서 마지막으로 불러온 대댓글을 확인합니다.
    fun Check_Last_ReComment (position : Int = 0) : Int {
        // 리스트와 포지션의 크기가 같다면 대댓글을 불러온적이없음.
        if (commentList.size == position + 1) {
            Log.d("Check_Last_ReComment","if : ")
            return 0
        } else if (commentList[position + 1].View_Type == TYPE_ONE) {
            Log.d("Check_Last_ReComment","if else if : "+commentList[position + 1].Comment_Num)
            return 0
        } else if (commentList[position + 1].View_Type == TYPE_TWO) {
            Log.d("Check_Last_ReComment","if else if else if : "+commentList[position + 1].Comment_Num)
            return commentList[position + 1].Comment_Num
        } else {
            Log.d("Check_Last_ReComment","if else if else if else : "+commentList[position + 1].Comment_Num)
            return 0
        }
    }

    // 해당하는 댓글의 대댓글을 불러오는 메소
    fun Get_ReComment_Item(feedNum: Int,
                           User_Num: Int,
                           position: Int,
                           Parent_Comment_Num : Int,
                           Last_ReComment_Num : Int,
                           commentModel: Comment_Model) {
        ApiClient.getClient.Get_ReComment_List(feedNum,User_Num,Parent_Comment_Num,Last_ReComment_Num).enqueue(object : Callback<ArrayList<Comment_Model>>{
            override fun onFailure(call: Call<ArrayList<Comment_Model>>, t: Throwable) {
                Log.e("Get_ReComment_Item", "onFailure : $t")
            }

            override fun onResponse(call: Call<ArrayList<Comment_Model>>,response: Response<ArrayList<Comment_Model>>) {
                if (response.isSuccessful && response.body()!!.isNotEmpty()) {
                    commentModel.ReComment_Count = commentModel.ReComment_Count - response.body()!!.size
                    for(i in 0 until response.body()!!.size) {
                        commentList.add(position+1,response.body()!![i])
                    }
                    notifyDataSetChanged()
                } else {
                    Log.e("Get_ReComment_Item", "onResponse : $response")
                }
            }

        })
    }

//    Get_ReComment_Item(Feed_Num,
//    Num,
//    adapterPosition,
//    commentModel.Parent_Num,
//    Check_Last_ReComment(adapterPosition),
//    commentModel)

    // 해당 댓글 또는 대댓글에 좋아요 개수에 대하여 적절한 문자열 반환
    fun set_LikeCount_Text(Like_Count : Int, LikeCount_TextView : TextView) : String {
        if (Like_Count == 0) {
            LikeCount_TextView.visibility = View.GONE
            return ""
        } else {
            LikeCount_TextView.visibility = View.VISIBLE
            return "좋아요 $Like_Count 개"
        }
    }

    // 내가 해당 게시물에 좋아요를 눌렀나 안눌렀나에따라서 뷰를 적절하게 바꿔줍니다.
    fun Was_Liked(Before_Like : ImageView, After_Like : ImageView, Liked : Int, Num : Int) {
        if (Liked == Num) {
            After_Like.visibility = View.VISIBLE
            Before_Like.visibility = View.GONE
        } else {
            After_Like.visibility = View.GONE
            Before_Like.visibility = View.VISIBLE
        }
    }

    // 댓글 작성자의 프로필 사진을 불러오는 메소
    fun setUserProfile_Pic (ImageView : ImageView, context: Context, fileName : String) {
        if (fileName == "0") {
            ImageView.setImageResource(R.drawable.ic_0)
        } else {
            GlideApp.with(context)
                .load(context.getString(R.string.address)+fileName)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ImageView)
        }
    }

    fun Comment_Like (Comment_Num : Int, Who_Liked : Int, Btn_Like : ImageView, Btn_UnLike : ImageView, Like_CountView : TextView,commentModel: Comment_Model) {
        ApiClient.getClient.Update_Comment_Like(Comment_Num,Who_Liked).enqueue(object : Callback<Check_Model> {
            override fun onFailure(call: Call<Check_Model>, t: Throwable) {
                Log.e("Comment_Like", "onFailure : $t")
            }

            override fun onResponse(call: Call<Check_Model>, response: Response<Check_Model>) {
                if (response.isSuccessful) {
                    if (response.body()!!.Success) {
                        Btn_Like.visibility = View.GONE
                        Btn_UnLike.visibility = View.VISIBLE
                        commentModel.Like_Count = commentModel.Like_Count + 1
                        Like_CountView.text = set_LikeCount_Text(commentModel.Like_Count,Like_CountView)
                    } else {
                        Log.e("Comment_Like", "onResponse : ${response.body()!!.Message}")
                    }
                } else {
                    Log.e("Comment_Like", "isNotSuccessful : $response")

                }
            }

        })
    }

    fun Comment_UnLike (Comment_Num : Int, Who_Liked : Int, Btn_Like : ImageView, Btn_UnLike : ImageView, Like_CountView : TextView,commentModel: Comment_Model) {
        ApiClient.getClient.Update_Comment_UnLike(Comment_Num,Who_Liked).enqueue(object : Callback<Check_Model> {
            override fun onFailure(call: Call<Check_Model>, t: Throwable) {
                Log.e("Comment_UnLike", "onFailure : $t")

            }

            override fun onResponse(call: Call<Check_Model>, response: Response<Check_Model>) {
                if (response.isSuccessful) {
                    if (response.body()!!.Success) {
                        Btn_Like.visibility = View.VISIBLE
                        Btn_UnLike.visibility = View.GONE
                        commentModel.Like_Count = commentModel.Like_Count - 1
                        Like_CountView.text = set_LikeCount_Text(commentModel.Like_Count,Like_CountView)
                    } else {
                        Log.e("Comment_UnLike", "onResponse : ${response.body()!!.Message}")
                    }
                } else {
                    Log.e("Comment_UnLike", "isNotSuccessful : $response")
                }
            }

        })
    }

}