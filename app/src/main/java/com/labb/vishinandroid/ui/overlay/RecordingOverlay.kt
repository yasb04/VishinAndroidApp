package com.labb.vishinandroid.ui.overlay

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import com.labb.vishinandroid.data.service.CallMonitoringService

@SuppressLint("StaticFieldLeak")
object RecordingOverlay {
    private var overlayView: View? = null

    fun show(context: Context) {
        try {
            if (overlayView != null) return
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.CENTER

                x = 0
                y = 0
            }


            val button = Button(context).apply {
                text = "Börja spela in samtal?"
                setBackgroundColor(Color.RED)
                setTextColor(Color.WHITE)
                setPadding(40, 40, 40, 40)

                setOnClickListener {
                    Log.d("VishingGuard", "Användare tryckte på START")
                    val intent = Intent(context, CallMonitoringService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(intent)
                    }
                    hide(context)
                }
            }

            overlayView = button
            windowManager.addView(overlayView, params)
            Log.d("VishingGuard", "Overlay har lagts till i WindowManager")
        } catch (e: Exception) {
            Log.e("VishingGuard", "Kunde inte visa Overlay: ${e.message}")
        }
    }

    fun hide(context: Context) {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        overlayView?.let {
            windowManager.removeView(it)
            overlayView = null
        }
    }
}