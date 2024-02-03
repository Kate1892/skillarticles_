package ru.skillbranch.skillarticles.ui.custom.spans

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.Spanned
import android.text.style.LeadingMarginSpan
import androidx.annotation.ColorInt
import androidx.annotation.Px
import ru.skillbranch.skillarticles.extensions.getLineBottomWithoutPadding

class BlockquotesSpan(
    @Px
    private val gapWidth: Float,
    @Px
    private val quoteWidth: Float,
    @ColorInt
    private val lineColor: Int
) : LeadingMarginSpan {

    override fun drawLeadingMargin(
        canvas: Canvas, paint: Paint, currentMarginLocation: Int, paragraphDirection: Int,
        lineTop: Int, lineBaseline: Int, lineBottom: Int, text: CharSequence?, lineStart: Int,
        lineEnd: Int, isFirstLine: Boolean, layout: Layout
    ) {
        paint.withCustomColor {
            canvas.drawLine(
                quoteWidth / 2f,
                if (!isFirstLine || (text as Spanned).getSpans(
                        lineStart.dec(),
                        lineStart.dec(),
                        BlockquotesSpan::class.java
                    ).isNotEmpty()
                ) layout.getLineBottomWithoutPadding(layout.getLineForOffset(lineStart.dec()))
                    .toFloat() else lineTop.toFloat(),
                quoteWidth / 2f,
                layout.getLineBottomWithoutPadding(layout.getLineForOffset(lineEnd)).toFloat(),
                paint
            )
        }
    }

    override fun getLeadingMargin(first: Boolean): Int {
        return (quoteWidth + gapWidth).toInt()
    }

    private inline fun Paint.withCustomColor(block: () -> Unit) {
        val oldColor = color
        val oldStyle = style
        val oldWith = strokeWidth

        color = lineColor
        style = Paint.Style.STROKE
        strokeWidth = quoteWidth

        block()

        color = oldColor
        style = oldStyle
        strokeWidth = oldWith
    }
}