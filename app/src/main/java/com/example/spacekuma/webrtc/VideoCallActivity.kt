package com.example.spacekuma.webrtc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.spacekuma.R
import com.example.spacekuma.activities.MainActivity
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_video_call.*
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.*
import java.util.*

class VideoCallActivity : AppCompatActivity() {
    lateinit var eglBaseContext: EglBase.Context
    lateinit var peerConnectionFactory: PeerConnectionFactory
    lateinit var mediaStream: MediaStream
    lateinit var iceServers: MutableList<PeerConnection.IceServer>

    lateinit var peerConnectionMap: HashMap<String, PeerConnection>
    var remoteViewsIndex = 0

    private val room = "OldPlace"

    val onMessage : Emitter.Listener = Emitter.Listener {
        Log.e("chao", "message " + Arrays.toString(it))
        val arg = it[0]
        if (arg is String) {

        } else if (arg is JSONObject) {
            val data = arg
            val type = data.optString("type")
            if ("offer" == type) {
                runOnUiThread {
                    val socketId = data.optString("from")
                    val peerConnection = getOrCreatePeerConnection(socketId)
                    peerConnection.setRemoteDescription(
                        SdpAdapter("setRemoteSdp:$socketId"),
                        SessionDescription(SessionDescription.Type.OFFER, data.optString("sdp"))
                    )
                    peerConnection.createAnswer(object : SdpAdapter("localAnswerSdp") {
                        override fun onCreateSuccess(sdp: SessionDescription) {
                            super.onCreateSuccess(sdp)
                            peerConnectionMap[socketId]!!.setLocalDescription(SdpAdapter("setLocalSdp:$socketId"),sdp)
                            sendSessionDescription(sdp, socketId)
                        }
                    }, MediaConstraints())
                }
            } else if ("answer" == type) {
                val socketId = data.optString("from")
                val peerConnection = getOrCreatePeerConnection(socketId)
                peerConnection.setRemoteDescription(
                    SdpAdapter("setRemoteSdp:$socketId"),
                    SessionDescription(SessionDescription.Type.ANSWER, data.optString("sdp"))
                )
            } else if ("candidate" == type) {
                val socketId = data.optString("from")
                val peerConnection = getOrCreatePeerConnection(socketId)
                peerConnection.addIceCandidate(IceCandidate(data.optString("id"),data.optInt("label"),data.optString("candidate")))
            }
        }
    }

    val onCreate : Emitter.Listener = Emitter.Listener {
        Log.e("chao", "room created:" + MainActivity.mSocket.id())
    }

    val onJoined : Emitter.Listener = Emitter.Listener {
        Log.e("chao", "room created:" + MainActivity.mSocket.id())
    }

    val onJoin : Emitter.Listener = Emitter.Listener {
        Log.e("chao", "peer joined " + Arrays.toString(it))
        onPeerJoined(it[1].toString())
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call)

        MainActivity.mSocket.on("message",onMessage)
        MainActivity.mSocket.on("created",onCreate)
        MainActivity.mSocket.on("joined",onJoined)
        MainActivity.mSocket.on("join",onJoin)


        peerConnectionMap = HashMap()
        iceServers = ArrayList<PeerConnection.IceServer>()
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer())

        eglBaseContext = EglBase.create().eglBaseContext

        // create PeerConnectionFactory
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions
                .builder(this)
                .createInitializationOptions()
        )
        val options = PeerConnectionFactory.Options()
        val defaultVideoEncoderFactory = DefaultVideoEncoderFactory(eglBaseContext, true, true)
        val defaultVideoDecoderFactory = DefaultVideoDecoderFactory(eglBaseContext)
        peerConnectionFactory = PeerConnectionFactory.builder()
            .setOptions(options)
            .setVideoEncoderFactory(defaultVideoEncoderFactory)
            .setVideoDecoderFactory(defaultVideoDecoderFactory)
            .createPeerConnectionFactory()

        val surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBaseContext)
        // create VideoCapturer
        val videoCapturer = createCameraCapturer(true)
        val videoSource = peerConnectionFactory.createVideoSource(videoCapturer!!.isScreencast)
        videoCapturer.initialize(surfaceTextureHelper,applicationContext,videoSource.capturerObserver)
        videoCapturer.startCapture(480, 640, 30)

        localView.setMirror(true)
        localView.init(eglBaseContext, null)

        // create VideoTrack
        val videoTrack = peerConnectionFactory.createVideoTrack("100", videoSource)
//        // display in localView
        videoTrack.addSink(localView)


        remoteView.setMirror(false)
        remoteView.init(eglBaseContext, null)

        val audioSource = peerConnectionFactory.createAudioSource(MediaConstraints())
        val audioTrack = peerConnectionFactory.createAudioTrack("101",audioSource)

        mediaStream = peerConnectionFactory.createLocalMediaStream("mediaStream")
        mediaStream.addTrack(videoTrack)
        mediaStream.addTrack(audioTrack)

//        SignalingClient.get().init(this)
        MainActivity.mSocket.emit("create or join",intent.getStringExtra("Room"))

    }

    private fun createCameraCapturer(isFront: Boolean): VideoCapturer? {
        val enumerator = Camera1Enumerator(false)
        val deviceNames = enumerator.deviceNames

        // First, try to find front facing camera
        for (deviceName in deviceNames) {
            if (if (isFront) enumerator.isFrontFacing(deviceName) else enumerator.isBackFacing(deviceName)) {
                val videoCapturer = enumerator.createCapturer(deviceName, null)

                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }

        return null
    }

    @Synchronized
    private fun getOrCreatePeerConnection(socketId: String): PeerConnection {
        var peerConnection = peerConnectionMap[socketId]
        if (peerConnection != null) {
            return peerConnection
        }
        peerConnection = peerConnectionFactory.createPeerConnection(iceServers, object : PeerConnectionAdapter("PC:$socketId") {
                override fun onIceCandidate(iceCandidate: IceCandidate) {
                    super.onIceCandidate(iceCandidate)
                    sendIceCandidate(iceCandidate, socketId)
                }

                override fun onAddStream(mediaStream: MediaStream) {
                    super.onAddStream(mediaStream)
                    val remoteVideoTrack = mediaStream.videoTracks[0]
                    runOnUiThread {
                        remoteVideoTrack.addSink(remoteView)
                    }
                }
            })
        peerConnection!!.addStream(mediaStream)
        peerConnectionMap[socketId] = peerConnection
        return peerConnection
    }

    fun onPeerJoined(socketId: String) {
        val peerConnection = getOrCreatePeerConnection(socketId)
        peerConnection.createOffer(object : SdpAdapter("createOfferSdp:$socketId") {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                super.onCreateSuccess(sessionDescription)
                peerConnection.setLocalDescription(
                    SdpAdapter("setLocalSdp:$socketId"),
                    sessionDescription
                )
                sendSessionDescription(sessionDescription, socketId)
            }
        }, MediaConstraints())
    }

    fun sendIceCandidate(iceCandidate: IceCandidate, to: String) {
        val jo = JSONObject()
        try {
            jo.put("type", "candidate")
            jo.put("label", iceCandidate.sdpMLineIndex)
            jo.put("id", iceCandidate.sdpMid)
            jo.put("candidate", iceCandidate.sdp)
            jo.put("from", MainActivity.mSocket.id())
            jo.put("to", to)

            MainActivity.mSocket.emit("message", jo)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }
    fun sendSessionDescription(sdp: SessionDescription, to: String) {
        val jo = JSONObject()
        try {
            jo.put("type", sdp.type.canonicalForm())
            jo.put("sdp", sdp.description)
            jo.put("from", MainActivity.mSocket.id())
            jo.put("to", to)
            MainActivity.mSocket.emit("message", jo)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    override fun onDestroy() {
        MainActivity.mSocket.off("message",onMessage)
        super.onDestroy()
    }
}
