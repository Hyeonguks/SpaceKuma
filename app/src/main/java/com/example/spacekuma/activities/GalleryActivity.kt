package com.example.spacekuma.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.spacekuma.R
import kotlinx.android.synthetic.main.activity_gallery.*
import java.io.File

class GalleryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

//        Image_RecyclerView

        val ROOT_DIR = Environment.getExternalStorageDirectory().path

        var gpath: String = Environment.getExternalStorageDirectory().absolutePath
        var spath = "pictures"
        var fullpath = File(gpath + File.separator + spath)
        Log.w("fullpath", "" + fullpath)
        imageReaderNew(fullpath)


        getBucketNames()
//        val colors = arrayOf("Red","Green","Blue","Yellow","Black","Crimson","Orange")
        val colors = arrayOf("Red","Green","Blue","Yellow","Black","Crimson","Orange")
        // Initializing an ArrayAdapter
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, colors )

        // Set the drop down view resource
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

            }
        }



    }

    fun getBucketNames() : Array<String>? {
        val file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
//        val file : File = Environment.getExternalStorageDirectory()

        for(i in 0..file.list().size -1) {
            Log.d("getBucketNames", ": "+file.list()[i].toString())
        }
        return file.list()
    }

//    출처: https://altongmon.tistory.com/891 [IOS를 Java]

    fun imageReaderNew(root: File) {
        val fileList: ArrayList<File> = ArrayList()
        val listAllFiles = root.listFiles()

        if (listAllFiles != null && listAllFiles.size > 0) {
            for (currentFile in listAllFiles) {
                if (currentFile.name.endsWith(".jpeg")) {
                    // File absolute path
                    Log.e("downloadFilePath", currentFile.absolutePath)
                    // File Name
                    Log.e("downloadFileName", currentFile.name)
                    fileList.add(currentFile.absoluteFile)
                }
            }
            Log.w("fileList", "" + fileList.size)
        }
    }




}
