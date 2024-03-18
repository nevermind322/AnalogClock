package com.example.analogclock

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


class AnalogClockView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val clockBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.clock)
    private val rect = Rect()
    private val handPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL_AND_STROKE
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val desiredWidth = 100
        val desiredHeight = 100

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> min(desiredWidth, widthSize)
            else -> desiredWidth
        }

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> min(desiredHeight, heightSize)
            else -> desiredHeight
        }
        setMeasuredDimension(width, height)
    }


    private fun Canvas.drawHands() {
        val c: Calendar = Calendar.getInstance()
        c.timeZone = TimeZone.getDefault()
        var hour = c.get(Calendar.HOUR_OF_DAY).toDouble()
        val minute = c.get(Calendar.MINUTE)
        val second = c.get(Calendar.SECOND)
        hour = if (hour > 12) hour - 12 else hour
        hour += minute.toDouble() / 60.0
        drawHourHand(hour)
        drawMinuteHand(minute)
        drawSecondHand(second)
    }

    private fun Canvas.drawSecondHand(loc: Int) {
        val angle = (Math.PI * loc) / 30.0 - Math.PI / 2.0
        val clockSize = min(width, height)
        val r = (clockSize / 2) * SECOND_HAND_SIZE
        drawLine(
            (width / 2).toFloat(),
            (height / 2).toFloat(),
            (width / 2 + cos(angle) * r).toFloat(),
            (height / 2 + sin(angle) * r).toFloat(),
            handPaint.apply { strokeWidth = clockSize.toFloat() / 200f }
        )
    }

    private fun Canvas.drawMinuteHand(loc: Int) {
        val angle = (Math.PI * loc) / 30.0 - Math.PI / 2.0
        val clockSize = min(width, height)
        val r = (clockSize / 2) * MINUTE_HAND_SIZE
        drawLine(
            (width / 2).toFloat(),
            (height / 2).toFloat(),
            (width / 2 + cos(angle) * r).toFloat(),
            (height / 2 + sin(angle) * r).toFloat(),
            handPaint.apply { strokeWidth = clockSize.toFloat() / 100f }
        )
    }

    private fun Canvas.drawHourHand(loc: Double) {
        val angle = Math.PI * loc / 6.0 - Math.PI / 2.0
        val clockSize = min(width, height)
        val r = (clockSize / 2) * HOUR_HAND_SIZE
        drawLine(
            (width / 2).toFloat(),
            (height / 2).toFloat(),
            (width / 2 + cos(angle) * r).toFloat(),
            (height / 2 + sin(angle) * r).toFloat(),
            handPaint.apply { strokeWidth = clockSize.toFloat() / 50f }
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (width > height)
            rect.set((width - height) / 2, 0, width - ((width - height) / 2), height)
        else if (height > width)
            rect.set(0, (height - width) / 2, width, height - ((height - width) / 2))
        else
            rect.set(0, 0, width, height)

        canvas.drawBitmap(
            clockBitmap, null, rect, null
        )
        canvas.drawHands()
        postInvalidateDelayed(500)
    }

    companion object {
        const val HOUR_HAND_SIZE = 0.6
        const val MINUTE_HAND_SIZE = 0.75
        const val SECOND_HAND_SIZE = 0.9
    }

}