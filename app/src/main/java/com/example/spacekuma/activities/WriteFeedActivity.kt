package com.example.spacekuma.activities

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spacekuma.ItemDragListener
import com.example.spacekuma.ItemTouchHelperCallback
import com.example.spacekuma.R
import com.example.spacekuma.data.Check_Model
import com.example.spacekuma.data.Feed_Media_Uri_Model
import com.example.spacekuma.data.NewsFeed_Model
import com.example.spacekuma.recycler_view_adapters.Write_Feed_Adapter
import com.example.spacekuma.retrofit.ApiClient
import kotlinx.android.synthetic.main.activity_edit__profile.*
import kotlinx.android.synthetic.main.activity_write_feed.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

// http://dudmy.net/android/2018/05/02/drag-and-swipe-recyclerview/
// https://github.com/dudmy/blog-sample/blob/master/ItemTouchHelper-Sample/app/src/main/java/net/dudmy/itemtouchhelper/MainActivity.kt
class WriteFeedActivity : AppCompatActivity(),ItemDragListener {
    val REQUEST_TAKE_PHOTO = 222
    val REQUEST_TAKE_VIDEO = 333
    var feedList = arrayListOf<Feed_Media_Uri_Model>()
    val writeFeedAdapter : Write_Feed_Adapter = Write_Feed_Adapter(this@WriteFeedActivity,feedList,this)
    val map : HashMap<String, Feed_Media_Uri_Model> = HashMap()

    private lateinit var itemTouchHelper: ItemTouchHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_feed)

        setSupportActionBar(Write_Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "게시글 작성"

        Btn_getImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(intent,REQUEST_TAKE_PHOTO)
        }

        Btn_getVideo.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "video/*"
            startActivityForResult(intent,REQUEST_TAKE_VIDEO)
        }

        WriteFeed_RecyclerView.adapter = writeFeedAdapter
        WriteFeed_RecyclerView.layoutManager = LinearLayoutManager(this@WriteFeedActivity)
        itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(writeFeedAdapter))
        itemTouchHelper.attachToRecyclerView(WriteFeed_RecyclerView)

    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

    fun getRealImagePath (contentUri : Uri) : String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor : Cursor = contentResolver.query(contentUri, proj, null, null, null)!!
        cursor.moveToNext()
        val path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))
        var uri : Uri = Uri.fromFile(File(path))
        val fileName = File(path).name
        Log.d("getRealImagePath Name", ": -> $fileName")
        cursor.close()
        Log.d("getRealImagePath", ": -> $path")
        return path
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK && null != data) {
            if (data.data != null) {
                val realPath = getRealImagePath(data.data!!)!!
                feedList.add(Feed_Media_Uri_Model(1,File(realPath).name,realPath))
                WriteFeed_RecyclerView.adapter?.notifyDataSetChanged()
            } else if (data.clipData != null) {
                val mClipData = data.clipData

                for(i in 0..mClipData!!.itemCount-1) {
                    val realPath = getRealImagePath(mClipData.getItemAt(i).uri)!!
                    Log.d("onActivityResult", ": "+mClipData.getItemAt(i).uri)
                    feedList.add(Feed_Media_Uri_Model(1,File(realPath).name,realPath))
                }
                WriteFeed_RecyclerView.adapter?.notifyDataSetChanged()
            }
        } else if (requestCode == REQUEST_TAKE_VIDEO && resultCode == RESULT_OK && null != data) {
            val realPath = getRealImagePath(data.data!!)!!
            feedList.add(Feed_Media_Uri_Model(2,File(realPath).name,realPath))
            WriteFeed_RecyclerView.adapter?.notifyDataSetChanged()
        }
    }

    fun return_feed_type() : Int {
        var viewtype = 0
        if (feedList.isEmpty()) {
            viewtype = 0
            return viewtype
            Log.d("onResponse", ": ViewType -> :" +0)
        } else if(feedList[0].View_Type == 1){
            viewtype = 1
            return viewtype
            Log.d("onResponse", ": ViewType -> :"+ 1)
        } else if(feedList[0].View_Type == 2) {
            viewtype = 1
            return viewtype
            Log.d("onResponse", ": ViewType -> :"+ 2)
        } else {
            return 0
        }
    }

    fun return_feed_media(mList : ArrayList<Feed_Media_Uri_Model>) : ArrayList<MultipartBody.Part> {
        val media : ArrayList<MultipartBody.Part> = arrayListOf()

        for (i in 0..mList.size - 1) {
            val file = File(mList[i].Feed_Media_Uri!!)
            if (mList[i].View_Type == 1) {
                var requestBody : RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                var body : MultipartBody.Part = MultipartBody.Part.createFormData("uploaded_file$i",file.name,requestBody)
                media.add(body)
            } else {
                var requestBody : RequestBody = file.asRequestBody("video/*".toMediaTypeOrNull())
                var body : MultipartBody.Part = MultipartBody.Part.createFormData("uploaded_file$i",file.name,requestBody)
                media.add(body)
            }
        }

        return media
    }

    fun Create_Map () : HashMap<String,Feed_Media_Uri_Model> {
        for (i in 0..feedList.size - 1) {
            map[i.toString()] = feedList[i]
        }

        Log.d("HashMap", "HashMap : $map")

        return map
    }

    fun Upload() {
        ApiClient.getClient.Upload_Feed(return_feed_type(),feedList.size,intent.getIntExtra("Num",0),Edittext_Feed.text.toString(),Create_Map(),return_feed_media(feedList)).enqueue(object :
            Callback<Check_Model> {
            override fun onFailure(call: Call<Check_Model>, t: Throwable) {
                Log.d("onResponse", "Upload -> onFailure -> Test -> $t.")
            }

            override fun onResponse(call: Call<Check_Model>, response: Response<Check_Model>) {
                if (response.isSuccessful) {
                    if (response.body()!!.Success) {
                        Log.d("onResponse", ": 업로드 성공! -> :"+ response.body()!!.Message)
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Log.d("onResponse", ": 업로드 실패! -> :"+ response.body()!!.Message)
                        Toast.makeText(this@WriteFeedActivity, ""+response.body()!!.Message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d("onResponse", ": 접근실패! -> :$response")
                    Toast.makeText(this@WriteFeedActivity, ""+response.body()!!.Message, Toast.LENGTH_SHORT).show()
                }
            }

        })
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
                Upload()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 툴바 뒤로가기 버튼
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        CheckNull()
    }

    fun CheckNull () {
        if (feedList.isEmpty() && Edittext_Feed.text.isEmpty() ) {
            finish()
        } else {
            Show_Dialog()
        }
    }

    // 다이얼로그 생성
    fun Show_Dialog() {
        val items = arrayOf("무시","저장")
        val builder = AlertDialog.Builder(this)
        with(builder) {
            setTitle("현재 작성중인 게시물을 저장하실래요?")
            setItems(items) { dialog, which ->
                when (which) {
                    0 -> finish()
                    1 -> Toast.makeText(this@WriteFeedActivity,"저장",Toast.LENGTH_SHORT).show()
                }
            }
            show()
        }
    }

}
