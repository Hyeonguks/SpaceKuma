package com.example.spacekuma.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spacekuma.BR
import com.example.spacekuma.R
import com.example.spacekuma.data.Search_Model
import com.example.spacekuma.data.User_Model
import com.example.spacekuma.databinding.ActivitySerachBinding
import com.example.spacekuma.recycler_view_adapters.Search_Adapter
import com.example.spacekuma.retrofit.ApiClient
import kotlinx.android.synthetic.main.activity_serach.view.*

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//https://stackoverflow.com/a/46112897
//툴바 패딩처리
class SearchActivity : AppCompatActivity() {

    lateinit var bind : ActivitySerachBinding

    var mList = arrayListOf<User_Model>()
//    private lateinit var signUPViewModel: SignUPViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        signUPViewModel = ViewModelProviders.of(this@SearchActivity).get(SignUPViewModel::class.java)
        bind = DataBindingUtil.setContentView(this, R.layout.activity_serach)

        bind.setVariable(BR.search,this)

        setSupportActionBar(bind.root.search_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        bind.search.isActivated = true
        bind.search.onActionViewExpanded()
        bind.search.isIconified = false

        bind.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Search_User(query!!,mList.size)
                Log.d("onQueryTextSubmit", ": OnClick")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText == "") {
                    mList.clear()
                    bind.SearchRecyclerView.adapter?.notifyDataSetChanged()
                } else {
                    Search_User(newText!!,mList.size)
                }
                Log.d("onQueryTextSubmit", ": $newText")
                return true
            }

        })

        bind.SearchRecyclerView.adapter = Search_Adapter(this@SearchActivity,mList)
        bind.SearchRecyclerView.layoutManager = LinearLayoutManager(this@SearchActivity)

    }

    fun Search_User(User : String, Size: Int) {
        ApiClient.getClient.Search_User(User,Size).enqueue(object : Callback<Search_Model> {
            override fun onFailure(call: Call<Search_Model>, t: Throwable) {
                Log.d("SearchActivity", "Search_User -> onFailure -> $t.")
            }

            override fun onResponse(call: Call<Search_Model>, response: Response<Search_Model>) {
                if (response.isSuccessful) {
                    if (response.body()!!.checkModel.Success) {
                        if (response.body()!!.userModel.isEmpty()) {
                            bind.TextNotFound.visibility = View.VISIBLE
                            bind.SearchRecyclerView.visibility = View.GONE
                        } else {
                            bind.TextNotFound.visibility = View.GONE
                            bind.SearchRecyclerView.visibility = View.VISIBLE
                        }
                        mList.clear()
                        mList.addAll(response.body()!!.userModel )
                        bind.SearchRecyclerView.adapter?.notifyDataSetChanged()
                        Log.d("onResponse", ": 검색결과 -> :"+ response.body()!!.userModel)
                    } else {
                        Toast.makeText(this@SearchActivity, ""+response.body()?.checkModel!!.Message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@SearchActivity, ""+response.body()?.checkModel!!.Message, Toast.LENGTH_SHORT).show()
                }


            }

        })

    }

    // 툴바 뒤로가기 버튼
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


}
