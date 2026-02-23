package com.labb.vishinandroid.ui.overlay

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.CountDownTimer
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.graphics.toColorInt

/**
 * Visar en varningsruta som ligger kvar i 5 sekunder.
 */
@SuppressLint("SetTextI18n", "StaticFieldLeak")
object InterventionOverlay {

    private var overlayView: View? = null
    private var isShowing = false

    private var timer: CountDownTimer? = null

    fun show(context: Context) {
        if (isShowing) return

        try {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

            val windowType = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY

            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT, // Täcker hela fönstret, kan ändra till WRAP_CONTENT istället om man vill kunna trycka på knappar bakom overlayen
                windowType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.CENTER
            }


            val rootLayout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setBackgroundColor("#CC000000".toColorInt())
                setPadding(32, 32, 32, 32)
            }

            val card = CardView(context).apply {
                radius = 40f
                setCardBackgroundColor(Color.WHITE)
                setContentPadding(60, 60, 60, 60)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply{
                    setMargins(60,0,60,0)
                }
            }

            val contentLayout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
            }

            val title = TextView(context).apply {
                text = "⚠️ SÄKERHETSVARNING"
                textSize = 24f
                setTextColor(Color.RED)
                gravity = Gravity.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }

            val message = TextView(context).apply {
                text = "Du pratar just nu med ett okänt nummer.\n\nBedragare ber dig ofta logga in med BankID eller Swisha.\n\nTänk efter före du agerar."
                textSize = 18f
                setTextColor(Color.DKGRAY)
                gravity = Gravity.CENTER
                setPadding(0, 30, 0, 30)
            }

            val timerText = TextView(context).apply {
                text = "Vänligen vänta 3 sekunder..."
                textSize = 16f
                setTextColor(Color.GRAY)
                gravity = Gravity.CENTER
            }

            val confirmButton = Button(context).apply{
                text = "Jag förstår"
                textSize = 16f
                setBackgroundColor(Color.parseColor("#006400"))
                setTextColor(Color.WHITE)
                visibility = View.GONE

                setOnClickListener{
                    hide(context)
                }
            }

            contentLayout.addView(title)
            contentLayout.addView(message)
            contentLayout.addView(timerText)
            contentLayout.addView(confirmButton)
            card.addView(contentLayout)
            rootLayout.addView(card)

            overlayView = rootLayout
            windowManager.addView(overlayView, params)
            isShowing = true

            object : CountDownTimer(3000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val secondsLeft = millisUntilFinished / 1000 + 1
                    timerText.text = "Du kan fortsätta om $secondsLeft sekunder..."
                }

                override fun onFinish() {
                    timerText.text = ""
                    confirmButton.visibility = View.VISIBLE
                }
            }.start()

        } catch (e: Exception) {
            e.printStackTrace()
            isShowing = false
        }
    }

    fun hide(context: Context) {
        if (!isShowing) return
        try {
            timer?.cancel()
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            overlayView?.let {
                windowManager.removeView(it)
            }
            overlayView = null
            isShowing = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}