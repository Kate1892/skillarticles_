package ru.skillbranch.skillarticles.markdown.spans

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.style.LeadingMarginSpan
import androidx.annotation.ColorInt
import androidx.annotation.Px


class UnorderedListSpan(
    @Px
    private val gapWidth: Float,
    @Px
    private val bulletRadius: Float,
    @ColorInt
    private val bulletColor: Int
) : LeadingMarginSpan {

    override fun getLeadingMargin(first: Boolean): Int {
        return (4 * bulletRadius + gapWidth).toInt()
    }

    override fun drawLeadingMargin(
        canvas: Canvas, paint: Paint, currentMarginLocation: Int, paragraphDirection: Int,
        lineTop: Int, lineBaseline: Int, lineBottom: Int, text: CharSequence?, lineStart: Int,
        lineEnd: Int, isFirstLine: Boolean, layout: Layout?
    ) {
        if (isFirstLine) {
            val oldColor = paint.color
            paint.withCustomColor {
                canvas.drawCircle(
                    gapWidth + currentMarginLocation + bulletRadius,
                    (lineTop + lineBottom) / 2f,
                    bulletRadius,
                    paint
                )
            }
            paint.color = oldColor
        }
    }

    private inline fun Paint.withCustomColor(block: () -> Unit) {
        val oldColor = color
        val oldStyle = style

        color = bulletColor
        style = Paint.Style.FILL

        block()

        color = oldColor
        style = oldStyle
    }
}