package com.example.spacekuma.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.spacekuma.dao.LoginDao
import com.example.spacekuma.data.MyInfo_Model

@Database(entities = [MyInfo_Model::class],version = 1,exportSchema = false)
abstract class LoginDB : RoomDatabase(){

    abstract fun loginDao() : LoginDao

    companion object {

        private var INSTANCE : LoginDB? = null

        fun getInstance(context : Context) : LoginDB? {

            if (INSTANCE == null) {
                synchronized(LoginDB::class) {
                    INSTANCE = Room.databaseBuilder(
                        context,
                        LoginDB::class.java,
                        "login_info.db")
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