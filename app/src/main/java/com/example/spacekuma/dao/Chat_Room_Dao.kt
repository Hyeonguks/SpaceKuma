package com.example.spacekuma.dao

import androidx.room.*
import com.example.spacekuma.data.Chat_Message_Model
import com.example.spacekuma.data.Chat_Room_Model
import com.example.spacekuma.data.User_Model
import io.reactivex.Flowable

@Dao
abstract class Chat_Room_Dao {
    @Query("SELECT * FROM chat_room WHERE Room_Num")
    abstract fun getRoom_List(): Flowable<Chat_Room_Model>

    @Query("DELETE FROM chat_room")
    abstract fun Clear_All_Room_List()

    @Query("DELETE FROM chat_room WHERE Room_Num = :Room_Num")
    abstract fun Room_Leave(Room_Num : Int)

    //해당 데이터를 추가합니다.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(vararg chatRoomModel: Chat_Room_Model)

    //해당 데이터를 업데이트 합니다.
    @Update
    abstract fun update(vararg chatRoomModel: Chat_Room_Model)

    //해당 데이터를 삭제합니다.
    @Delete
    abstract fun delete(vararg chatRoomModel: Chat_Room_Model)

//    @Transaction
//    open fun updateData(roomList: List<Chat_Room_Model>) {
//        Clear_All_Room_List()
//        for (i in roomList.indices) {
//            insert(roomList[i])
//            insert_member(roomList[i].Member)
//            insert_chat(roomList[i].ChatList)
//        }
//    }
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    abstract fun insert_member(vararg userList: List<User_Model>)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    abstract fun insert_chat(vararg chatList: List<Chat_Message_Model>)

}