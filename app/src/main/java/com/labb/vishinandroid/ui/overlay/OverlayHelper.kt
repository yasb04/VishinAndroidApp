package com.labb.vishinandroid.ui.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView

import com.labb.vishinandroid.R

object OverlayHelper {


    fun showWarningOverlay(context: Context, smsText: String, score: Float) {
        if (!Settings.canDrawOverlays(context)) return

        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        params.y = 100

        val inflater = LayoutInflater.from(context)
        val overlayView = inflater.inflate(R.layout.warning_overlay, null)


        val scoreView = overlayView.findViewById<TextView>(R.id.fraud_score)
        val smsContentView = overlayView.findViewById<TextView>(R.id.sms_content)
        val closeButton = overlayView.findViewById<Button>(R.id.btn_close_overlay)


        val percentage = (score * 100).toInt()
        scoreView.text = "Risk för bedrägeri: $percentage%"
        smsContentView.text = smsText

        try {
            windowManager.addView(overlayView, params)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        closeButton.setOnClickListener {
            try {
                windowManager.removeView(overlayView)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}