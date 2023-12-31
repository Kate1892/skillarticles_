package ru.skillbranch.skillarticles.markdown.spans

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.Spannable
import android.text.TextPaint
import android.text.style.LeadingMarginSpan
import android.text.style.LineHeightSpan
import android.text.style.MetricAffectingSpan
import android.util.Log
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting


class HeaderSpan constructor(
    @IntRange(from = 1, to = 6)
    private val level: Int,
    @ColorInt
    private val textColor: Int,
    @ColorInt
    private val dividerColor: Int,
    @Px
    private val marginTop: Float,
    @Px
    private val marginBottom: Float
) :
    MetricAffectingSpan(), LineHeightSpan, LeadingMarginSpan {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val linePadding = 0.4f
    private var originAscent = 0

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val sizes = mapOf(
        1 to 2f,
        2 to 1.5f,
        3 to 1.25f,
        4 to 1f,
        5 to 0.875f,
        6 to 0.85f
    )

    override fun chooseHeight(
        text: CharSequence?,
        start: Int,
        end: Int,
        spanstartv: Int,
        lineHeight: Int,
        fm: Paint.FontMetricsInt?
    ) {
        fm ?: return

        text as Spannable
        val spanStart = text.getSpanStart(this)
        val spanEnd = text.getSpanEnd(this)

        if (spanStart == start) {
            originAscent = fm.ascent
            fm.ascent = (fm.ascent - marginTop).toInt()
        } else {
            fm.ascent = originAscent
        }

        if (spanEnd == end.dec()) {
            val originHeight = fm.descent - originAscent
            fm.descent = (originHeight * linePadding + marginBottom).toInt()
        }

        fm.top = fm.ascent
        fm.bottom = fm.descent

    }

    override fun updateMeasureState(paint: TextPaint) {
        with(paint) {
            textSize *= sizes.getOrElse(level) { 1f }
            isFakeBoldText = true
        }
    }

    override fun updateDrawState(tp: TextPaint) {
        with(tp) {
            textSize *= sizes.getOrElse(level) { 1f }
            isFakeBoldText = true
            color = textColor
        }
    }

    override fun drawLeadingMargin(
        canvas: Canvas, paint: Paint, currentMarginLocation: Int, paragraphDirection: Int,
        lineTop: Int, lineBaseline: Int, lineBottom: Int, text: CharSequence?, lineStart: Int,
        lineEnd: Int, isFirstLine: Boolean, layout: Layout?
    ) {
        if ((level == 1 || level == 2) && (text as Spannable).getSpanEnd(this) == lineEnd) {
            paint.forLine {
                val lh = (paint.descent() - paint.ascent()) * sizes.getOrElse(lineEnd) { 1f }
                val lineOffset = lineBaseline + lh * linePadding

                canvas.drawLine(
                    0f,
                    lineOffset,
                    canvas.width.toFloat(),
                    lineOffset,
                    paint
                )
            }
        }
    }

    override fun getLeadingMargin(first: Boolean): Int {
        //TODO implement me
        return 0
    }

    private inline fun Paint.forLine(block: () -> Unit) {
        val oldColor = color
        val oldStyle = style
        val oldWith = strokeWidth

        color = dividerColor
        style = Paint.Style.STROKE
        strokeWidth = 0f

        block()

        color = oldColor
        style = oldStyle
        strokeWidth = oldWith
    }
}