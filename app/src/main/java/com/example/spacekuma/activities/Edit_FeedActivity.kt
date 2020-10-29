package com.example.spacekuma.activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.spacekuma.R
import com.example.spacekuma.data.Check_Model
import com.example.spacekuma.retrofit.ApiClient
import kotlinx.android.synthetic.main.activity_edit__feed.*
import kotlinx.android.synthetic.main.activity_write_feed.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Edit_FeedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit__feed)

        setSupportActionBar(toolbar2)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "게시글 수정"

        intent.getIntExtra("Feed_Num",0)
        intent.getIntExtra("Position",0)

        Edit_Feed.setText(intent.getStringExtra("Feed_Text"))

    }

    // 툴바 메뉴 인플레이터
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.writefeed_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    // 툴바 메뉴 클릭 이벤트 정의
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_done -> {
                CheckNull()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 툴바 뒤로가기 버튼
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun CheckNull () {
        if (Edit_Feed.text.isEmpty() ) {
            Toast.makeText(this@Edit_FeedActivity,"내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            ApiClient.getClient.Update_Feed_Item(intent.getIntExtra("Feed_Num",0),Edit_Feed.text.toString()).enqueue(object : Callback<Check_Model>{
                override fun onFailure(call: Call<Check_Model>, t: Throwable) {
                    Toast.makeText(this@Edit_FeedActivity,"다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<Check_Model>, response: Response<Check_Model>) {
                    if (response.isSuccessful && response.body()!!.Success) {
                        setResult(Activity.RESULT_OK,intent
                            .putExtra("Position",intent.getIntExtra("Position",0))
                            .putExtra("FeedText",Edit_Feed.text.toString()))
                        finish()
                    } else {
                        Toast.makeText(this@Edit_FeedActivity,"수정할 수 없습니다..", Toast.LENGTH_SHORT).show()
                    }
                }

            })
        }
    }
}
