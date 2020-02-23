package com.example.spacekuma.dao

import androidx.room.*
import com.example.spacekuma.data.MyInfo_Model
import io.reactivex.Flowable

@Dao
interface LoginDao {

    @Query("SELECT * FROM login_info WHERE Num = :Num")
    fun getLoginInfo(Num : Int): Flowable<MyInfo_Model>

    @Query("DELETE FROM login_info")
    fun clearAll()

    @Query("DELETE FROM login_info WHERE Num = :Num")
    fun LogOut(Num : Int)


    //해당 데이터를 추가합니다.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg loginInfo : MyInfo_Model)

    //해당 데이터를 업데이트 합니다.
    @Update
    fun update(vararg loginInfo : MyInfo_Model)

    //해당 데이터를 삭제합니다.
    @Delete
    fun delete(vararg loginInfo : MyInfo_Model)

}