package com.labb.vishinandroid.data.service


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecordingService : Service() {
    private var recorder: MediaRecorder? = null
    private var isRecording: Boolean = false


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startRecordingNotification()
        startRecording()
        return START_STICKY
    }

    override fun onDestroy() {
        stopRecording()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null


    fun startRecording(){
        if(isRecording)return

        try{
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "scamVC_$timeStamp.mp4"
            val file = File(getExternalFilesDir(null), fileName)
            recorder = MediaRecorder().apply {
                //setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION) fungerar ej i men ur samtal
                setAudioSource(MediaRecorder.AudioSource.MIC) //ur inte i
                //setAudioSource(MediaRecorder.AudioSource.UNPROCESSED) ur inte i
                //setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION) varken i eller ur
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }
            isRecording = true
            Log.d("VishingGuard", "startade inspelning")
        }catch (e: Exception){
            Log.e("VishingGuard", "Kunde inte starta inspelning", e)
        }

    }
    fun stopRecording(){
        try {
            if (isRecording) {
                recorder?.stop()
                recorder?.release()
                recorder = null
                isRecording = false
                Log.d("VishingGuard", "Inspelning stoppad.")
            }
        } catch (e: Exception) {
            Log.e("VishingGuard", "Kunde inte stoppsa inspelningen", e)
        }

    }

    private fun startRecordingNotification() {
        val channelId = "vishing_recording_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Inspelning pågår",
                NotificationManager.IMPORTANCE_LOW,

            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }


        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("VishingGuard")
            .setContentText("Samtal spelas in...")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .build()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            startForeground(
                1,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
            )
        } else {
            startForeground(1, notification)
        }
    }
}