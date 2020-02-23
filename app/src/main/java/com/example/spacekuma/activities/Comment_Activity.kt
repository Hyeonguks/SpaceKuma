package com.example.spacekuma.activities

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.spacekuma.GlideApp
import com.example.spacekuma.R
import com.example.spacekuma.data.Check_Model
import com.example.spacekuma.data.Comment_Model
import com.example.spacekuma.data.FeedDetail_Model
import com.example.spacekuma.recycler_view_adapters.Comment_Adapter
import com.example.spacekuma.retrofit.ApiClient
import com.example.spacekuma.util.PaginationScrollListener
import com.example.spacekuma.util.TimeString
import kotlinx.android.synthetic.main.activity_comment_.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Comment_Activity : AppCompatActivity() {
    var isLoading :Boolean = true
    var isLastPage: Boolean = false

    var Num :Int = 0
    var Feed_Num :Int = 0
    var Writer_Num :Int = 0
    var Parent_Num :Int = 0

    lateinit var commentAdapter: Comment_Adapter
    lateinit var manager : LinearLayoutManager

    var mList = arrayListOf<Comment_Model>()

    var reply_ : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment_)

        setSupportActionBar(Comment_Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "댓글"

        Num = intent.getIntExtra("Num",0)
        Feed_Num = intent.getIntExtra("Feed_Num",0)
        Writer_Num = intent.getIntExtra("Writer_Num",0)

//        Get_Feed_Info(intent.getIntExtra("Feed_Num",0))

        commentAdapter = Comment_Adapter(Feed_Num,Num,this@Comment_Activity,mList)
        manager = LinearLayoutManager(this@Comment_Activity)

        RecyclerView_Comment.adapter = commentAdapter
        RecyclerView_Comment.layoutManager = manager

        Get_Comment_List(Feed_Num,0,Num,null)

        Swipe_Layout.setOnRefreshListener {
            Get_Comment_List(Feed_Num,0,Num,true)
        }

        RecyclerView_Comment.addOnScrollListener(object : PaginationScrollListener(manager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
//                isLoading = true
//
//                mList.add(NewsFeed_Model(0,777,0,0,0,false,false,false,"","","",null,"","","",""))
//                root.NewsFeed_RecyclerView.adapter!!.notifyItemInserted(mList.size - 1)
                Toast.makeText(this@Comment_Activity,"End!",Toast.LENGTH_SHORT).show()
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (!recyclerView.canScrollVertically(-1)) {
                    Log.e("onScrollStateChanged", "TOP!")
                } else if (!recyclerView.canScrollVertically(1) && !isLoading()) {
                    isLoading = true
                    commentAdapter.addLoadingItem()
                    commentAdapter.notifyItemInserted(mList.size - 1)
                    Get_Comment_List(Feed_Num,mList[mList.size-2].Comment_Num,Num,false)
                    Log.e("onScrollStateChanged", "Last_Comment_Num : "+mList[mList.size-2].Comment_Num)
                } else {
                    Log.e("onScrollStateChanged", "???")
                }
            }
        })

        commentAdapter.itemClick = object : Comment_Adapter.ItemClick{
            override fun onReplyClick(view: View,feedNum: Int,Comment_Num: Int,Writer_Name: String,Comment_Text: String) {
                reply_ = true
                Parent_Num = Comment_Num
                Log.e("Comment_Activity","전송 버튼 클릭 $Comment_Num")
                Con_Recomment.visibility = View.VISIBLE
                Con_Cancle.visibility = View.VISIBLE

                TextView_Comment_Writer_Name.text = Writer_Name
                TextView_Parent_Comment.text = Comment_Text

            }
        }

        Con_Cancle.setOnClickListener {
            reply_ = false
            Con_Recomment.visibility = View.GONE
            Con_Cancle.visibility = View.GONE
        }

        TextView_Send.setOnClickListener {
            if (reply_) {
                Upload_Comment(Feed_Num,2,Num,Parent_Num,EditText_Comment.text.toString())
            } else {
                Upload_Comment(Feed_Num,1,Num,0,EditText_Comment.text.toString())
            }
        }
    }

    // 툴바 뒤로가기 버튼
    override fun onSupportNavigateUp(): Boolean {
        if (isLoading) {

        } else {
            onBackPressed()
        }
        return isLoading
    }

    override fun onBackPressed() {
        if (isLoading) {

        } else {
            finish()
        }
    }

    fun Get_Comment_List(Feed_Num: Int,Last_Comment_Num : Int, User_Num : Int,Refresh : Boolean?) {
        ApiClient.getClient.Get_Comment_List(Feed_Num,User_Num,Last_Comment_Num).enqueue(object :Callback<ArrayList<Comment_Model>> {
            override fun onFailure(call: Call<ArrayList<Comment_Model>>, t: Throwable) {
                Log.e("Get_Comment_List", "onFailure : $t")
            }

            override fun onResponse(call: Call<ArrayList<Comment_Model>>,response: Response<ArrayList<Comment_Model>>) {
                if (response.isSuccessful && response.body()!!.isNotEmpty()) {
                    when (Refresh) {
                        true -> {
                            // 새로고침해서 아이템 불러올 때.
                            mList.clear()
                            commentAdapter.addHeaderItem()
                            commentAdapter.addNextItem(response.body()!!)
                            commentAdapter.notifyDataSetChanged()
                            isLoading = false
                            Swipe_Layout.isRefreshing = false
                        }
                        false -> {
                            // 스크롤 맨 마지막에서 아이템 더 불러올 때.
                            commentAdapter.removeLoadingItem()
                            commentAdapter.addNextItem(response.body()!!)
                            commentAdapter.notifyDataSetChanged()
                            isLoading = false
                            Swipe_Layout.isRefreshing = false
                        }
                        else -> {
                            // onCreate 에서 처음 뉴스피드 아이템 불러올 때.
                            commentAdapter.addHeaderItem()
                            commentAdapter.addNextItem(response.body()!!)
                            commentAdapter.notifyDataSetChanged()
                            isLoading = false
                            Swipe_Layout.isRefreshing = false
                        }
                    }
                } else {
                    if (isLoading && !mList.isNullOrEmpty()) {
                        commentAdapter.removeLoadingItem()
                        commentAdapter.notifyItemRemoved(mList.size-1)
                    } else {
                        commentAdapter.clear()
                        commentAdapter.addHeaderItem()
                        commentAdapter.notifyDataSetChanged()
                    }
                    isLoading = false
                    Swipe_Layout.isRefreshing = false
                }
            }

        })
    }

    fun Upload_Comment(Feed_Num : Int,View_Type : Int,Writer_Num : Int,Comment_Numm : Int,Comment_Text : String) {
        ApiClient.getClient.Upload_Comment(Feed_Num,View_Type,Writer_Num,Comment_Numm,Comment_Text).enqueue(object :Callback<Check_Model> {
            override fun onFailure(call: Call<Check_Model>, t: Throwable) {
                Log.e("onFailure","Upload_Comment : $t")
            }

            override fun onResponse(call: Call<Check_Model>, response: Response<Check_Model>) {
                if (response.isSuccessful) {
                    if (response.body()!!.Success) {
                        reply_ = false
                        Con_Recomment.visibility = View.GONE
                        Con_Cancle.visibility = View.GONE
                        EditText_Comment.text.clear()
                        Get_Comment_List(Feed_Num,0,Num,true)
                    } else {
                        Log.e("onResponse","Upload_Comment : "+response.body()!!.Message)
                    }
                } else {
                    Log.e("onResponse","Upload_Comment : Fail " + response.body()!!.Message)
                }
            }

        })
    }

}
