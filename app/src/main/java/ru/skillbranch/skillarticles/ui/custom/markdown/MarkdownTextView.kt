package ru.skillbranch.skillarticles.ui.custom.markdown

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.text.Spannable
import android.text.Spanned
import android.text.method.LinkMovementMethod
import androidx.annotation.VisibleForTesting
import androidx.core.graphics.withTranslation
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.attrValue
import ru.skillbranch.skillarticles.extensions.dpToPx

@SuppressLint("ViewConstructor")
class MarkdownTextView(
    context: Context,
    fontSize: Float,
    private val isSizeDepend: Boolean = true
) : androidx.appcompat.widget.AppCompatTextView(context, null, 0), IMarkdownView {

    private val color = context.attrValue(com.google.android.material.R.attr.colorOnBackground)
    private val focusRect = Rect()
    private val searchPadding = context.dpToPx(56)

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var searchBgHelper = SearchBgHelper(context) { top, bottom ->
        focusRect.set(0, top - searchPadding.toInt(), width, bottom + searchPadding.toInt())

        requestRectangleOnScreen(focusRect, false)
    }

    init {
        setTextColor(color)
        textSize = fontSize
        movementMethod = LinkMovementMethod()
    }

    override var fontSize: Float = fontSize
        set(value) {
            textSize = value
            field = value
        }

    override val spannableContent: Spannable
        get() = text as Spannable

    override fun onDraw(canvas: Canvas) {
        if (text is Spanned && layout != null) {
            canvas.withTranslation(totalPaddingLeft.toFloat(), totalPaddingRight.toFloat()) { }
            searchBgHelper.draw(canvas, text as Spanned, layout)
        }
        super.onDraw(canvas)
    }

    override fun setTextSize(size: Float) {
        if (isSizeDepend) setLineSpacing(context.dpToPx(if (size == 14f) 8 else 10), 1f)
        super.setTextSize(size)
    }
}