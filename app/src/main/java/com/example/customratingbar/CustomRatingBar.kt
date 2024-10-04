package com.example.customratingbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat

class CustomRatingBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var starDrawableFilled: Drawable? = null
    private var starDrawableEmpty: Drawable? = null
    private var starSize: Int = 0
    private var numStars: Int = 5
    private var rating: Float = 0f

    init {
        starDrawableFilled = ContextCompat.getDrawable(context, R.drawable.ic_star_filled)
        starDrawableEmpty = ContextCompat.getDrawable(context, R.drawable.ic_star_empty)
        starSize = resources.getDimensionPixelSize(R.dimen.star_size)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = starSize * numStars
        val height = starSize
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (i in 0 until numStars) {
            val drawable = if (i < rating) starDrawableFilled else starDrawableEmpty
            drawable?.setBounds(i * starSize, 0, (i + 1) * starSize, starSize)
            drawable?.draw(canvas)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
            // Tính toán ngôi sao được chạm vào
            val x = event.x
            val newRating = (x / starSize).toInt().coerceIn(0, numStars) // Cập nhật từ 0 đến numStars - 1

            setRating(newRating.toFloat() + 1) // Cập nhật rating (từ 1 đến numStars)
            return true // Xử lý sự kiện chạm
        }
        return super.onTouchEvent(event)
    }

    fun setRating(rating: Float) {
        this.rating = rating.coerceIn(0f, numStars.toFloat()) // Đảm bảo rating trong khoảng hợp lệ
        invalidate() // Yêu cầu vẽ lại
    }

    fun getRating(): Float = rating
}
