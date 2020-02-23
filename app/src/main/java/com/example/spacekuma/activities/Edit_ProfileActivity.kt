package com.example.spacekuma.activities

import android.Manifest.permission.*

import android.app.Activity

import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore

import android.text.Editable
import android.util.Log
import android.view.*

import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.spacekuma.GlideApp

import com.example.spacekuma.R
import com.example.spacekuma.data.Check_Model
import com.example.spacekuma.retrofit.ApiClient
import kotlinx.android.synthetic.main.activity_edit__profile.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import java.io.File
import java.io.IOException

import java.text.SimpleDateFormat

class Edit_ProfileActivity : AppCompatActivity() {

    var Num :Int = 0
    var ID : String = ""
    var Name : String = ""
    var Pic : String = ""
    var Date : String = ""

    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_TAKE_PHOTO = 2
    private val PERMISSION_REQUEST_CODE: Int = 101

    var currentPhotoPath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit__profile)

        setSupportActionBar(Edit_Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "프로필 수정"

        User_Pic.background = ShapeDrawable(OvalShape())
        User_Pic.clipToOutline = true

        if (intent.hasExtra("Num")) {
            Num = intent.getIntExtra("Num",0)
            ID = intent.getStringExtra("ID")!!
            Name = intent.getStringExtra("Name")!!
            Pic = intent.getStringExtra("Pic")!!
            Date = intent.getStringExtra("Date")!!

            if (Pic == "0") {
                User_Pic.setImageResource(R.drawable.ic_0)
            } else {
                Glide.with(this@Edit_ProfileActivity)
                    .load(getString(R.string.address)+Pic)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(User_Pic)
            }

            Edittext_Name.text = Editable.Factory.getInstance().newEditable(Name)
            Text_Date.text = Date
            Text_User_ID.text = ID


            Con_Edit_Pic.setOnClickListener { if (checkPermission()) Show_Dialog() else requestPermission() }

            Log.d("newMainActivity", "Edit_ProfileActivity : -> OnCreate -> Intent Num: "+ intent.getIntExtra("Num",0))
            Log.d("newMainActivity", "Edit_ProfileActivity : -> OnCreate -> Intent ID: "+ intent.getStringExtra("ID"))
            Log.d("newMainActivity", "Edit_ProfileActivity : -> OnCreate -> Intent Name: "+ intent.getStringExtra("Name"))
            Log.d("newMainActivity", "Edit_ProfileActivity : -> OnCreate -> Intent Pic: "+ intent.getStringExtra("Pic"))
            Log.d("newMainActivity", "Edit_ProfileActivity : -> OnCreate -> Intent Date: "+ intent.getStringExtra("Date"))

        } else {
            this@Edit_ProfileActivity.finish()
        }

    }

    // 다이얼로그 생성
    fun Show_Dialog() {
        val items = arrayOf("카메라로 직접 촬영","앨범에서 선택하기")
        val builder = AlertDialog.Builder(this)
        with(builder) {
            setTitle("프로필 사진 변경")
            setItems(items) { dialog, which ->
                when (which) {
                    0 -> Camera_Intent()
                    1 -> Gallery_Intent()
                }
            }
            show()
        }

    }

    // 카메라와 저장소 퍼미션을 확인하는 메소드
    private fun checkPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(this, CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }

    // 사용자에게 권한 설정을 요청합니다.
    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE, CAMERA), PERMISSION_REQUEST_CODE)
    }

    // 퍼미션 체크 결과
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)&&grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Show_Dialog()

                } else {
                    Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show()
                }
                return
            }

            else -> {

            }
        }
    }

    // 갤러리에서 프로필 사진으로 설정할 이미지를 가져옵니다.
    private fun Gallery_Intent() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_TAKE_PHOTO)
    }

    /*
    카메라로 프로필 사진으로 설정할 이미지를 촬영합니다.
    촬영한 사진은 갤러리에 저장됩니다.
     */
    private fun Camera_Intent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File

                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.spacekuma.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    fun getRealPath (contentUri : Uri) : String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor : Cursor = contentResolver.query(contentUri, proj, null, null, null)!!
        cursor.moveToNext()
        val path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))
        var uri : Uri = Uri.fromFile(File(path))
        cursor.close()
        Log.d("getRealPath", ": -> $path")
        return path
    }

    // 촬영한 사진을 갤러리에 저장하는 메소드
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(java.util.Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            GlideApp.with(this@Edit_ProfileActivity)
                .load(currentPhotoPath)
                .fitCenter()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(User_Pic)

        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            Log.d("onActivityResult", ": -> "+data?.data)

            currentPhotoPath = getRealPath(data?.data!!).toString()

            GlideApp.with(this@Edit_ProfileActivity)
                .load(currentPhotoPath)
                .fitCenter()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(User_Pic)
        }
    }


    // 툴바 메뉴 인플레이터
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.profile_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    // 툴바 메뉴 클릭 이벤트 정의
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_done -> {
                if (currentPhotoPath == "" && Name == Edittext_Name.text.toString()) {
                    finish()
                } else if (currentPhotoPath != "") {
                    Edit_Profile(currentPhotoPath,Edittext_Name.text.toString())
                } else {
                    Edit_Name(Edittext_Name.text.toString())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 툴바 뒤로가기 버튼
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun Edit_Profile(Image_Uri :String,Edit_Name : String) {
        Log.d("Test_Uri", ": $Image_Uri")
        val file = File(Image_Uri)

        var requestBody : RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        var body : MultipartBody.Part = MultipartBody.Part.createFormData("uploaded_file",file.name,requestBody)

        ApiClient.getClient.Edit_Profile(Num,ID,Edit_Name,body).enqueue(object : Callback<Check_Model> {
            override fun onFailure(call: Call<Check_Model>, t: Throwable) {
                Log.d("Edit_ProfileActivity", "Edit_Profile -> onFailure -> Test -> $t.")
            }

            override fun onResponse(call: Call<Check_Model>, response: Response<Check_Model>) {
                if (response.isSuccessful) {
                    if (response.body()!!.Success) {
                        Log.d("onResponse", ": 프로필 사진 변경 성공 -> :"+ response.body()!!.Message)
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Log.d("onResponse", ": Test1 -> :"+ response.body()!!.Message)
                        Toast.makeText(this@Edit_ProfileActivity, ""+response.body()!!.Message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d("onResponse", ": Test2 -> :"+ response.body()!!.Message)
                    Toast.makeText(this@Edit_ProfileActivity, ""+response.body()!!.Message, Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

    fun Edit_Name(Edit_Name : String) {
        ApiClient.getClient.Edit_Name(Num,ID,Edit_Name).enqueue(object : Callback<Check_Model> {
            override fun onFailure(call: Call<Check_Model>, t: Throwable) {
                Log.d("Edit_ProfileActivity", "Edit_Profile -> onFailure -> Test -> $t.")
            }

            override fun onResponse(call: Call<Check_Model>, response: Response<Check_Model>) {
                if (response.isSuccessful) {
                    if (response.body()!!.Success) {
                        // 프로필 사진도 변경하면 현재 home 에 있는 프로필 사진은 변경되지 않음.
                        setResult(RESULT_OK,intent.putExtra("Name",Edit_Name))
                        finish()
                    } else {
                        Log.d("onResponse", ": Test1 -> :"+ response.body()!!.Message)
                        Toast.makeText(this@Edit_ProfileActivity, ""+response.body()!!.Message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d("onResponse", ": Test2 -> :"+ response.body()!!.Message)
                    Toast.makeText(this@Edit_ProfileActivity, ""+response.body()!!.Message, Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

}
