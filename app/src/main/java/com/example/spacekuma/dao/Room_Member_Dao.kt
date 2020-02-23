package com.example.spacekuma.dao

import androidx.room.*
import com.example.spacekuma.data.Room_Member_Model
import io.reactivex.Flowable

@Dao
interface Room_Member_Dao {
    @Query("SELECT * FROM chat_room WHERE Room_Num")
    fun getRoomMember_List(): Flowable<Room_Member_Model>

    @Query("DELETE FROM chat_room")
    fun Clear_All_RoomMember_List()

    @Query("DELETE FROM chat_room WHERE Room_Num = :Room_Num")
    fun MemberRoom_Leave(Room_Num : Int)


    //해당 데이터를 추가합니다.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg roomMemberModel: Room_Member_Model)

    //해당 데이터를 업데이트 합니다.
    @Update
    fun update(vararg roomMemberModel: Room_Member_Model)

    //해당 데이터를 삭제합니다.
    @Delete
    fun delete(vararg roomMemberModel: Room_Member_Model)

}