package com.labb.vishinandroid.data.voip

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.json.JSONObject
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

class SignalingClient(
    private val serverUrl: String,
    private val listener: Listener
) : WebSocketListener() {

    interface Listener {
        fun onOfferReceived(sdp: SessionDescription)
        fun onAnswerReceived(sdp: SessionDescription)
        fun onIceCandidateReceived(candidate: IceCandidate)
    }

    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    fun connect() {
        val request = Request.Builder()
            .url(serverUrl)
            .build()
        webSocket = client.newWebSocket(request, this)
    }

    fun disconnect() {
        webSocket?.close(1000, "bye")
        webSocket = null
    }

    fun sendOffer(sdp: SessionDescription) {
        sendJson(
            JSONObject()
                .put("type", "offer")
                .put("sdp", sdp.description)
        )
    }

    fun sendAnswer(sdp: SessionDescription) {
        sendJson(
            JSONObject()
                .put("type", "answer")
                .put("sdp", sdp.description)
        )
    }

    fun sendIceCandidate(candidate: IceCandidate) {
        sendJson(
            JSONObject()
                .put("type", "candidate")
                .put("sdpMid", candidate.sdpMid)
                .put("sdpMLineIndex", candidate.sdpMLineIndex)
                .put("candidate", candidate.sdp)
        )
    }

    private fun sendJson(obj: JSONObject) {
        val ws = webSocket ?: return
        ws.send(obj.toString())
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        try {
            val json = JSONObject(text)
            when (json.getString("type")) {
                "offer" -> {
                    val sdp = SessionDescription(
                        SessionDescription.Type.OFFER,
                        json.getString("sdp")
                    )
                    listener.onOfferReceived(sdp)
                }
                "answer" -> {
                    val sdp = SessionDescription(
                        SessionDescription.Type.ANSWER,
                        json.getString("sdp")
                    )
                    listener.onAnswerReceived(sdp)
                }
                "candidate" -> {
                    val candidate = IceCandidate(
                        json.getString("sdpMid"),
                        json.getInt("sdpMLineIndex"),
                        json.getString("candidate")
                    )
                    listener.onIceCandidateReceived(candidate)
                }
            }
        } catch (e: Exception) {
            Log.e("SignalingClient", "Failed to parse signaling message", e)
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        // Not used
    }
}

