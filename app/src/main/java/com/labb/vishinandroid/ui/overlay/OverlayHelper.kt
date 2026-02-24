package com.labb.vishinandroid.ui.overlay

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.labb.vishinandroid.R

@SuppressLint("StaticFieldLeak")
object OverlayHelper {

    private var overlayView: View? = null

    fun showWarningOverlay(context: Context, smsText: String, score: Float) {
        if (!Settings.canDrawOverlays(context)) return

        val percentage = (score * 100).toInt()
        val scoreText = "Riskbedömning: $percentage%"

        if (isPopUpOpen()) {
            try {
                val scoreView = overlayView?.findViewById<TextView>(R.id.fraud_score)
                val smsContentView = overlayView?.findViewById<TextView>(R.id.sms_content)

                scoreView?.text = scoreText
                smsContentView?.text = smsText
                return
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

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
        val newView = inflater.inflate(R.layout.warning_overlay, null)

        val scoreView = newView.findViewById<TextView>(R.id.fraud_score)
        val smsContentView = newView.findViewById<TextView>(R.id.sms_content)
        val closeButton = newView.findViewById<Button>(R.id.btn_close_overlay)

        scoreView.text = scoreText
        smsContentView.text = smsText

        try {
            windowManager.addView(newView, params)
            overlayView = newView
        } catch (e: Exception) {
            e.printStackTrace()
        }

        closeButton.setOnClickListener {
            hideOverlay(context)
        }
    }

    fun hideOverlay(context: Context) {
        if (overlayView != null) {
            try {
                val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                windowManager.removeView(overlayView)
                overlayView = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun isPopUpOpen(): Boolean {
        return overlayView != null
    }
}