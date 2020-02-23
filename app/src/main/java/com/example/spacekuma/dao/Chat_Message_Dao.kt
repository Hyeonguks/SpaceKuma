package com.example.spacekuma.dao

import androidx.room.*
import com.example.spacekuma.data.Chat_Message_Model
import io.reactivex.Flowable

@Dao
interface Chat_Message_Dao {
    @Query("SELECT * FROM chat_message WHERE Room_Num")
    fun getChatList(): Flowable<Chat_Message_Model>

    @Query("DELETE FROM chat_message")
    fun Clear_All_Chat_List()

    @Query("DELETE FROM chat_message WHERE Room_Num = :Room_Num")
    fun Delete_Room_Message(Room_Num : Int)

    //해당 데이터를 추가합니다.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg chatMessageModel: Chat_Message_Model)

    //해당 데이터를 업데이트 합니다.
    @Update
    fun update(vararg chatMessageModel: Chat_Message_Model)

    //해당 데이터를 삭제합니다.
    @Delete
    fun delete(vararg chatMessageModel: Chat_Message_Model)

}