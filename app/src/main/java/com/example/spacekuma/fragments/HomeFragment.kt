package com.example.spacekuma.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spacekuma.BR
import com.example.spacekuma.R
import com.example.spacekuma.activities.*

import com.example.spacekuma.data.NewsFeed_Model
import com.example.spacekuma.databinding.FragmentHomeBinding
import com.example.spacekuma.recycler_view_adapters.NewsFeed_Adapter
import com.example.spacekuma.retrofit.ApiClient
import com.example.spacekuma.util.PaginationScrollListener
import com.example.spacekuma.view_models.main.MainViewModel

import kotlinx.android.synthetic.main.fragment_home.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Main_Base_Fragment<FragmentHomeBinding>() {
    lateinit var newsfeedAdapter : NewsFeed_Adapter
    lateinit var manager : LinearLayoutManager

    var mList = arrayListOf<NewsFeed_Model>()
    var isLastPage: Boolean = false
    var isLoading: Boolean = false

    override val layoutID: Int = R.layout.fragment_home
    override val BRName: Int = BR.home

    override fun Init() {
        (activity as AppCompatActivity).setSupportActionBar(bind.root.Home_Toolbar)
        (activity as AppCompatActivity).supportActionBar?.title = "SpaceKuma"
        bind.root.shimmer_view_container.visibility = View.VISIBLE
        bind.root.shimmer_view_container.startShimmer()
        // https://stackoverflow.com/a/51047037
        setHasOptionsMenu(true)

        newsfeedAdapter = NewsFeed_Adapter(viewModel.Num.get()!!,viewModel.ID.get()!!,viewModel.Pic.get()!!,activity!!,mList)
        manager = LinearLayoutManager(activity!!)
        bind.root.NewsFeed_RecyclerView.adapter = newsfeedAdapter
        bind.root.NewsFeed_RecyclerView.layoutManager = manager

        bind.root.NewsFeed_RecyclerView.addOnScrollListener(object : PaginationScrollListener(manager) {
            override fun isLastPage(): Boolean { return isLastPage }
            override fun isLoading(): Boolean { return isLoading }
            override fun loadMoreItems() { Toast.makeText(activity!!,"End!",Toast.LENGTH_SHORT).show() }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (!recyclerView.canScrollVertically(-1)) {
                    Log.e("onScrollStateChanged", "TOP!")
                } else if (!recyclerView.canScrollVertically(1) && !isLoading()) {
                    isLoading = true
                    newsfeedAdapter.addLoadingItem()
                    newsfeedAdapter.notifyItemInserted(mList.size - 1)
                    GET_NEWSFEED(mList[mList.size-2].Feed_Num,false,viewModel.Num.get()!!)
                    Log.e("onScrollStateChanged", "END!")
                } else {
                    Log.e("onScrollStateChanged", "???")
                }
            }
        })

        bind.root.Swipe_Layout.setOnRefreshListener {
            GET_NEWSFEED(0,true,viewModel.Num.get()!!)
        }

        GET_NEWSFEED(0,null,viewModel.Num.get()!!)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 777 && resultCode == Activity.RESULT_OK) {
            Log.e("onActivityResult", "Home : 777")
            GET_NEWSFEED(0,true,viewModel.Num.get()!!)
        } else if (requestCode == 888 && resultCode == Activity.RESULT_OK){
            Log.e("onActivityResult", "Home : 888")
            var position = data!!.getIntExtra("Position",0)
            var feed_item = data.getParcelableExtra<NewsFeed_Model>("Feed_Item")
            Log.e("onActivityResult", "Home : 888 item : $feed_item")
            Log.e("onActivityResult", "Home : 888 : position :$position")
            mList[position] = feed_item!!
            newsfeedAdapter.notifyItemChanged(position)

        } else {
            Log.e("onActivityResult", "else")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_search -> startActivity(Intent(activity!!,SearchActivity::class.java))
            R.id.menu_account -> startActivity(Intent(activity!!,SearchActivity::class.java))
            else -> null
        }

        return super.onOptionsItemSelected(item)
    }

    fun GET_NEWSFEED(Last_Feed_Num : Int, Refresh : Boolean?,User_Num : Int) {
        ApiClient.getClient.Get_More_Feed(Last_Feed_Num,User_Num).enqueue(object :Callback<ArrayList<NewsFeed_Model>> {
            override fun onFailure(call: Call<ArrayList<NewsFeed_Model>>, t: Throwable) {
                bind.root.Swipe_Layout.isRefreshing = false
                Log.e("GET_NEWSFEED", "onFailure : $t")
            }

            override fun onResponse(call: Call<ArrayList<NewsFeed_Model>>, response: Response<ArrayList<NewsFeed_Model>>) {
                if (response.isSuccessful && response.body()!!.isNotEmpty()) {
                    when (Refresh) {
                        true -> {
                            // 새로고침해서 아이템 불러올 때.
                            newsfeedAdapter.clear()
                            newsfeedAdapter.addHeaderItem()
                            newsfeedAdapter.addNextItem(response.body()!!)
                            newsfeedAdapter.notifyDataSetChanged()
                            isLoading = false
                            bind.root.Swipe_Layout.isRefreshing = false
                        }
                        false -> {
                            // 스크롤 맨 마지막에서 아이템 더 불러올 때.
                            newsfeedAdapter.removeLoadingItem()
                            newsfeedAdapter.addNextItem(response.body()!!)
                            newsfeedAdapter.notifyDataSetChanged()
                            isLoading = false
                            bind.root.Swipe_Layout.isRefreshing = false
                        }
                        else -> {
                            Log.e("GET_NEWSFEED","Message : "+response.body())
                            Log.e("GET_NEWSFEED","Message : "+response.body()!![0].Message)
                            // onCreate 에서 처음 뉴스피드 아이템 불러올 때.
                            newsfeedAdapter.addHeaderItem()
                            newsfeedAdapter.addNextItem(response.body()!!)
                            newsfeedAdapter.notifyDataSetChanged()

                            bind.root.shimmer_view_container.visibility = View.GONE
                            bind.root.shimmer_view_container.stopShimmer()
                            bind.root.Swipe_Layout.visibility = View.VISIBLE
                        }
                    }
                } else {
                    if (isLoading) {
                        newsfeedAdapter.removeLoadingItem()
                        newsfeedAdapter.notifyItemRemoved(mList.size-1)
                    } else {
                        newsfeedAdapter.clear()
                        newsfeedAdapter.addHeaderItem()
                        newsfeedAdapter.notifyDataSetChanged()
                        bind.root.shimmer_view_container.visibility = View.GONE
                        bind.root.shimmer_view_container.stopShimmer()
                        bind.root.Swipe_Layout.visibility = View.VISIBLE
                    }
                    isLoading = false
                    bind.root.Swipe_Layout.isRefreshing = false
                    Log.e("GET_NEWSFEED","is Not Success Message : $response")
                    Log.e("GET_NEWSFEED","is Not Success Message : "+response.isSuccessful.toString())
                    Log.e("GET_NEWSFEED","is Not Success Message : "+response.body().toString())

                }
            }

        })
    }

}