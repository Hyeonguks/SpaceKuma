package com.example.newsfeed.dao

import androidx.room.*
import com.example.spacekuma.data.User_Model
import io.reactivex.Flowable

@Dao
interface UserDao {

    @Query("SELECT * FROM user_account")
    fun getAllPerson(): List<User_Model>

    @Query("DELETE FROM user_account")
    fun clearAll()


    //해당 데이터를 추가합니다.

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg user : User_Model)

    //해당 데이터를 업데이트 합니다.
    @Update
    fun update(vararg user : User_Model)

    //해당 데이터를 삭제합니다.
    @Delete
    fun delete(vararg user : User_Model)

}