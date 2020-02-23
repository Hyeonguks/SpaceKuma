package com.example.spacekuma.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.spacekuma.dao.Chat_Room_Dao
import com.example.spacekuma.data.Chat_Room_Model

@Database(entities = [Chat_Room_Model::class],version = 1,exportSchema = false)
abstract class Chat_Room_DB : RoomDatabase(){

    abstract fun chat_room_dao() : Chat_Room_Dao

    companion object {
        private var INSTANCE : Chat_Room_DB? = null

        fun getInstance(context : Context) : Chat_Room_DB? {
            if (INSTANCE == null) {
                synchronized(Chat_Room_DB::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        Chat_Room_DB::class.java,"chat_room.db")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE
        }

    }

    fun destroyInstance() {
        INSTANCE = null
    }


}