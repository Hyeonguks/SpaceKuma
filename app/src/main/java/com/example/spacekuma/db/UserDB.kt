package com.example.spacekuma.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.newsfeed.dao.UserDao
import com.example.spacekuma.data.User_Model

@Database(entities = [User_Model::class],version = 1,exportSchema = false)
abstract class UserDB : RoomDatabase(){

    abstract fun userDao() : UserDao

    companion object {
        private var INSTANCE : UserDB? = null

        fun getInstance(context : Context) : UserDB? {
            if (INSTANCE == null) {
                synchronized(UserDB::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        UserDB::class.java,"user_account.db")
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