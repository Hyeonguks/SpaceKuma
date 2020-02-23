package com.example.spacekuma

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.os.Parcelable
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.room.PrimaryKey
import com.example.spacekuma.activities.ChatActivity
import com.example.spacekuma.activities.InvitedActivity
import com.example.spacekuma.activities.MainActivity
import com.example.spacekuma.activities.WaitActivity
import com.example.spacekuma.data.Chat_Message_Model
import com.example.spacekuma.data.Chat_Room_Model
import com.example.spacekuma.data.Invited_Model
import com.example.spacekuma.fragments.ChatFragment
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.JsonObject

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "FirebaseService"

    /**
     * FirebaseInstanceIdService is deprecated.
     * this is new on firebase-messaging:17.1.0
     */
    override fun onNewToken(token: String) {
        val pref : SharedPreferences = getSharedPreferences("LoginInfo",0)

        val editor: SharedPreferences.Editor = pref.edit()
        editor.putString("Token",token)
        editor.apply()
        Log.d(TAG, "new Token: $token")
    }

    /**
     * this method will be triggered every time there is new FCM Message.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.from)

        if(remoteMessage.notification != null) {
            Log.d(TAG, "Notification Message Body: ${remoteMessage.notification?.body}")
            sendNotification(remoteMessage.notification?.body)

            /*
                * 영상통화 부분 *
                그리고 지금은 푸시 알림에 모든걸 담아서 보내는데 그러지말고 이벤트 코드만 상대방에게 보냄 시나리오는 다음과 같다.
                1. 오프너가 상대방에게 영상통화 FCM 전송 (오프너의 정보와 소켓이름을 전송),(그렇다면 방이름은 초대한 사람의 이름으로 하면 되겠네 ㅋㅋㅋ 개꿀)
                2. 상대방 기기에서 FCM 알림이 오게되면 전화수락거절 화면 시작.
                3. 수락거절 화면에서는 수락 거절 이벤트 처리 (수락하게되면 상대방의 소켓으로 모든 정보 전송하고, 거절하면 거절 이벤트 처리)

                확인 결과 FCM 은 현재 앱이 실행중일때는 오지 않음..
                그리고 실행중이지 않을때는 오더라.

             */

            if (remoteMessage.data["event_code"] == "0") {
                Log.d("FCM_New_Message", "event_code: 0")

            } else if(remoteMessage.data["event_code"] == "new_chatMessage") {
                Log.d("FCM_New_Message", "event_code: new_chatMessage")
                var newChatItem = Gson().fromJson(remoteMessage.data["message"], Chat_Message_Model::class.java)
                var newChatItemIntent = Intent("newMessage")
                newChatItemIntent.putExtra("newItem",newChatItem).putExtra("Room_Num",newChatItem.Room_Num)
                LocalBroadcastManager.getInstance(this).sendBroadcast(newChatItemIntent)
            } else if (remoteMessage.data["event_code"] == "invited_chatRoom") {
                Log.d("FCM_New_Message", "invited_chatRoom")
                var newChatRoomItem = Gson().fromJson(remoteMessage.data["message"], Chat_Room_Model::class.java)
                var newChatRoomItemIntent = Intent("newChatRoom").putExtra("newChatRoomItem",newChatRoomItem).putExtra("Room_Num",newChatRoomItem.Room_Num)
                LocalBroadcastManager.getInstance(this).sendBroadcast(newChatRoomItemIntent)
            } else if (remoteMessage.data["event_code"] == "video_call") {
                Log.d("FCM_New_Message", "event_code: video_call")

            } else {
                Log.d("FCM_New_Message", "event_code: else")

            }

//            var test = Gson().fromJson(remoteMessage.data["message"], Chat_Message_Model::class.java)
//            Log.d(TAG, "Notification Message data: ${test}")
//
//            if (ChatActivity.Room_Num != 0) {
//                ChatActivity.chatList.add(test)
//            } else {
//
//            }

            var invited_info = Gson().fromJson(remoteMessage.data["message"], Invited_Model::class.java)
            Log.d(TAG, "Notification Message data: ${invited_info}")

            if (invited_info.Event_Code == 0) {

            } else {
                startActivity(Intent(this, InvitedActivity::class.java)
//                    .putExtra("Inviter_Socket",invited_info.Opener_Socket)
                    .putExtra("Inviter_ID",invited_info.Opener_ID)
                    .putExtra("Inviter_Name",invited_info.Opener_Name)
                    .putExtra("Inviter_Num",invited_info.Opener_Num)
                    .putExtra("Inviter_Pic",invited_info.Opener_Pic)
                    .putExtra("Inviter_Token",invited_info.Opener_Token)
                    .putExtra("Inviter_Room_Num",invited_info.Room_Num).addFlags(FLAG_ACTIVITY_NEW_TASK))
            }

        }

    }

    private fun sendNotification(body: String?) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("Notification", body)
        }

        var pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        var notificationBuilder = NotificationCompat.Builder(this,"Notification")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Push Notification FCM")
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(notificationSound)
            .setContentIntent(pendingIntent)

        var notificationManager: NotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }

}