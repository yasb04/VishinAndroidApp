package com.labb.vishinandroid.data.voip

import android.app.Application
import org.webrtc.AudioSource
import org.webrtc.AudioTrack
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import org.webrtc.audio.JavaAudioDeviceModule
import com.labb.vishinandroid.data.interfaces.VoipEngine

class WebRtcVoipEngine(
    private val application: Application
) : VoipEngine {

    private var peerConnectionFactory: PeerConnectionFactory? = null
    private var peer: PeerConnection? = null
    private var localAudioSource: AudioSource? = null
    private var localAudioTrack: AudioTrack? = null

    private val signalingClient: SignalingClient = SignalingClient(
        serverUrl = "wss://your-signaling-server.example.com/ws",
        listener = object : SignalingClient.Listener {
            override fun onOfferReceived(sdp: SessionDescription) {
                // Callee-roll hanteras inte i denna MVP-version.
            }

            override fun onAnswerReceived(sdp: SessionDescription) {
                peer?.setRemoteDescription(simpleSdpObserver(), sdp)
            }

            override fun onIceCandidateReceived(candidate: IceCandidate) {
                peer?.addIceCandidate(candidate)
            }
        }
    )

    private fun ensureFactory() {
        if (peerConnectionFactory != null) return

        val eglBase = EglBase.create()

        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(application)
                .setEnableInternalTracer(false)
                .createInitializationOptions()
        )

        val audioModule = JavaAudioDeviceModule.builder(application)
            .createAudioDeviceModule()

        val encoderFactory = DefaultVideoEncoderFactory(
            eglBase.eglBaseContext,
            /* enableIntelVp8Encoder */ true,
            /* enableH264HighProfile */ true
        )
        val decoderFactory = DefaultVideoDecoderFactory(eglBase.eglBaseContext)

        peerConnectionFactory = PeerConnectionFactory.builder()
            .setAudioDeviceModule(audioModule)
            .setVideoEncoderFactory(encoderFactory)
            .setVideoDecoderFactory(decoderFactory)
            .createPeerConnectionFactory()
    }

    private fun createPeerConnection(factory: PeerConnectionFactory): PeerConnection? {
        val rtcConfig = PeerConnection.RTCConfiguration(emptyList())

        val observer = object : PeerConnection.Observer {
            override fun onSignalingChange(newState: PeerConnection.SignalingState) {}
            override fun onIceConnectionChange(newState: PeerConnection.IceConnectionState) {}
            override fun onIceConnectionReceivingChange(receiving: Boolean) {}
            override fun onIceGatheringChange(newState: PeerConnection.IceGatheringState) {}
            override fun onIceCandidate(candidate: IceCandidate) {
                signalingClient.sendIceCandidate(candidate)
            }
            override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate>) {}
            override fun onAddStream(stream: MediaStream) {}
            override fun onRemoveStream(stream: MediaStream) {}
            override fun onDataChannel(dc: org.webrtc.DataChannel) {}
            override fun onRenegotiationNeeded() {}
            override fun onAddTrack(receiver: org.webrtc.RtpReceiver, streams: Array<out MediaStream>) {}
        }

        val connection = factory.createPeerConnection(rtcConfig, observer)

        val constraints = MediaConstraints()
        localAudioSource = factory.createAudioSource(constraints)
        localAudioTrack = factory.createAudioTrack("audio0", localAudioSource)

        val stream = factory.createLocalMediaStream("stream0")
        stream.addTrack(localAudioTrack)
        connection?.addStream(stream)

        return connection
    }

    override fun startCall() {
        ensureFactory()
        val factory = peerConnectionFactory ?: return

        signalingClient.connect()

        if (peer == null) {
            peer = createPeerConnection(factory)
        }

        val connection = peer ?: return
        connection.createOffer(object : SdpObserver {
            override fun onCreateSuccess(desc: SessionDescription) {
                connection.setLocalDescription(simpleSdpObserver(), desc)
                signalingClient.sendOffer(desc)
            }

            override fun onSetSuccess() {}
            override fun onCreateFailure(error: String?) {}
            override fun onSetFailure(error: String?) {}
        }, MediaConstraints())
    }

    override fun endCall() {
        localAudioTrack?.dispose()
        localAudioSource?.dispose()
        peer?.close()

        signalingClient.disconnect()

        localAudioTrack = null
        localAudioSource = null
        peer = null
    }

    private fun simpleSdpObserver(): SdpObserver = object : SdpObserver {
        override fun onCreateSuccess(sdp: SessionDescription?) {}
        override fun onSetSuccess() {}
        override fun onCreateFailure(error: String?) {}
        override fun onSetFailure(error: String?) {}
    }
}
