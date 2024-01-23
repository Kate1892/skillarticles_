package ru.skillbranch.skillarticles

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.SpannableString
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.text.buildSpannedString
import androidx.core.text.getSpans
import androidx.core.view.children
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.*
import org.junit.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import ru.skillbranch.skillarticles.data.longText
import ru.skillbranch.skillarticles.data.repositories.MarkdownParser
import ru.skillbranch.skillarticles.data.repositories.clearContent
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.extensions.groupByBounds
import ru.skillbranch.skillarticles.extensions.indexesOf
import ru.skillbranch.skillarticles.ui.custom.markdown.*
import ru.skillbranch.skillarticles.ui.custom.spans.HeaderSpan
import ru.skillbranch.skillarticles.ui.custom.spans.SearchSpan
import java.lang.Thread.sleep


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class InstrumentalTest1 {

    companion object {
        var scaledDensity: Float = 0f
        var metrics: DisplayMetrics = DisplayMetrics()

        @BeforeClass
        @JvmStatic
        fun setupClass() {
            val ctx = ApplicationProvider.getApplicationContext<App>()
            scaledDensity = ctx.resources.displayMetrics.scaledDensity
            metrics = ctx.resources.displayMetrics
        }
    }

    @Test
    fun group_by_bounds() {
        val query = "background"
        val expectedResult: List<List<Pair<Int, Int>>> = listOf(
            listOf(25 to 35, 92 to 102, 153 to 163),
            listOf(220 to 230),
            listOf(239 to 249),
            listOf(330 to 340),
            listOf(349 to 359),
            listOf(421 to 431),
            listOf(860 to 870, 954 to 964),
            listOf(1084 to 1094),
            listOf(1209 to 1219, 1355 to 1365, 1795 to 1805),
            listOf(),
            listOf(
                2115 to 2125,
                2357 to 2367,
                2661 to 2671,
                2807 to 2817,
                3314 to 3324,
                3348 to 3358,
                3423 to 3433,
                3623 to 3633,
                3711 to 3721,
                4076 to 4086
            ),
            listOf(),
            listOf(5766 to 5776, 5897 to 5907, 5939 to 5949),
            listOf(),
            listOf(),
            listOf(),
            listOf(),
            listOf(),
            listOf()
        )
        val rawContent = MarkdownParser.parse(longText)

        val bounds = rawContent.map { it.bounds }

        val searchResult = rawContent.clearContent()
            .indexesOf(query)
            .map { it to it + query.length }

        val result = searchResult.groupByBounds(bounds)

        assertEquals(expectedResult, result)
    }

    @Test
    fun draw_search_background() {
        val testViewId = 100

        val string = buildSpannedString {
            append(
                "Header1 for first line, and for second line also header for , third line",
                HeaderSpan(1, Color.BLACK, Color.GRAY, 12.dpf(), 8.dpf()),
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            append("\nsimple text on line")
        }
        val mockDrawable = mockk<Drawable>(relaxed = true)
        val mockLeftDrawable = mockk<Drawable>(relaxed = true)
        val mockMiddleDrawable = mockk<Drawable>(relaxed = true)
        val mockRightDrawable = mockk<Drawable>(relaxed = true)


        val scenario = launchFragmentInContainer<TestFragment>(themeResId = R.style.AppTheme)
        //single line
        scenario.onFragment {
            val container = it.requireView() as FrameLayout
            val helper = SearchBgHelper(
                it.requireContext(),
                { top: Int, bot: Int -> /* nothing */ },
                mockDrawable,
                mockLeftDrawable,
                mockMiddleDrawable,
                mockRightDrawable
            )
            val mv = MarkdownTextView(it.requireContext(), 14f).apply {
                searchBgHelper = helper
                setText(string, TextView.BufferType.SPANNABLE)
                setPadding(it.requireContext().dpToIntPx(8))
                renderSearchResult(listOf(0 to 11), 0)
                id = testViewId
            }
            container.addView(mv)
        }
        sleep(2000)

        val singleBounds = mutableListOf<Int>()
        verifyOrder {
            mockDrawable.setBounds(
                capture(singleBounds),
                capture(singleBounds),
                capture(singleBounds),
                capture(singleBounds)
            )
            mockDrawable.draw(any())
        }

        assertEquals(
            "single line drawable setBounds ",
            "left: ${(-4).dp()}, top: ${12.dp()}, right: ${152.dp()}, bottom : ${45.dp()}",
            "left: ${singleBounds[0]}, top: ${singleBounds[1]}, right: ${singleBounds[2]}, bottom : ${singleBounds[3]}"
        )

        //multi line
        scenario.onFragment {
            val mv = it.requireView().findViewById<MarkdownTextView>(testViewId)
            mv.renderSearchResult(listOf(26 to 66), 0)
        }

        sleep(2000)

        val multiBounds = mutableListOf<Int>()
        verifyOrder {
            mockLeftDrawable.setBounds(
                capture(multiBounds),
                capture(multiBounds),
                capture(multiBounds),
                capture(multiBounds)
            )
            mockLeftDrawable.draw(any())
        }
        assertEquals(
            "first line drawable setBounds",
            "left: ${294.dp()}, top: ${12.dp()}, right: ${360.dp()}, bottom : ${45.dp()}",
            "left: ${multiBounds[0]}, top: ${multiBounds[1]}, right: ${multiBounds[2]}, bottom : ${multiBounds[3]}"
        )

        multiBounds.clear()
        verifyOrder {
            mockMiddleDrawable.setBounds(
                capture(multiBounds),
                capture(multiBounds),
                capture(multiBounds),
                capture(multiBounds)
            )
            mockMiddleDrawable.draw(any())
        }

        assertEquals(
            "middle line drawable setBounds",
            "left: ${(-4).dp()}, top: ${53.dp()}, right: ${354.dp()}, bottom : ${86.dp()}",
            "left: ${multiBounds[0]}, top: ${multiBounds[1]}, right: ${multiBounds[2]}, bottom : ${multiBounds[3]}"
        )

        multiBounds.clear()
        verifyOrder {
            mockRightDrawable.setBounds(
                capture(multiBounds),
                capture(multiBounds),
                capture(multiBounds),
                capture(multiBounds)
            )
            mockRightDrawable.draw(any())
        }
        assertEquals(
            "last line drawable setBounds",
            "left: ${(-4).dp()}, top: ${94.dp()}, right: ${45.dp()}, bottom : ${127.dp()}",
            "left: ${multiBounds[0]}, top: ${multiBounds[1]}, right: ${multiBounds[2]}, bottom : ${multiBounds[3]}"
        )

        //simple text
        scenario.onFragment {
            val mv = it.requireView().findViewById<MarkdownTextView>(testViewId)
            mv.renderSearchResult(listOf(79 to 87), 0)
        }
        sleep(2000)
        singleBounds.clear()
        verifyOrder {
            mockDrawable.setBounds(
                capture(singleBounds),
                capture(singleBounds),
                capture(singleBounds),
                capture(singleBounds)
            )
            mockDrawable.draw(any())
        }

        assertEquals(
            "simple text drawable setBounds ",
            "left: ${(38).dp()}, top: ${149.dp()}, right: ${92.dp()}, bottom : ${165.5f.dp()}",
            "left: ${singleBounds[0]}, top: ${singleBounds[1]}, right: ${singleBounds[2]}, bottom : ${singleBounds[3]}"
        )
    }


    @Test
    fun draw_markdown_image_view() {

        var viewUnderTest: MarkdownImageView? = null

        val scenario = launchFragmentInContainer<TestFragment>(themeResId = R.style.AppTheme)
        val bounds = mutableListOf<Int>()

        scenario.onFragment() {
            viewUnderTest = spyk(
                MarkdownImageView(it.requireContext(), 14f, "any", "title", "alt title"),
                recordPrivateCalls = true
            )
            viewUnderTest!!.ivImage.setImageDrawable(
                it.requireContext().getDrawable(R.drawable.logo)
            )
            val container = it.requireView() as FrameLayout
            container.addView(viewUnderTest)


        }
        sleep(2000)
        verifyOrder {
            viewUnderTest!!.onMeasure(any(), any())
            viewUnderTest!!.onLayout(
                any(),
                capture(bounds),
                capture(bounds),
                capture(bounds),
                capture(bounds)
            )
            viewUnderTest!!.dispatchDraw(any())
        }

        assertEquals(
            "MarkdownImageView onLayout ",
            "left: 0, top: 0, right: ${384.dp()}, bottom : ${362.5f.dp()}",
            "left: ${bounds[0]}, top: ${bounds[1]}, right: ${bounds[2]}, bottom : ${bounds[3]}"
        )

        assertEquals(
            "markdown view measure width",
            384.dpf(),
            viewUnderTest!!.measuredWidth.toFloat(),
            scaledDensity
        )
        assertEquals(
            "markdown view measure height",
            362.dpf(),
            viewUnderTest!!.measuredHeight.toFloat(),
            scaledDensity
        )

        assertEquals(
            "ivImage view bounds",
            "view ${viewUnderTest!!.ivImage::class.simpleName} has bounds left:0, top:0, right:${384.dp()}, bottom:${340.dp()}",
            viewUnderTest!!.ivImage.viewBounds(),
        )

        assertEquals(
            "tvAlt view bounds",
            "view ${viewUnderTest!!.tvAlt!!::class.simpleName} has bounds left:0, top:${305.dp()}, right:${384.dp()}, bottom:${340.dp()}",
            viewUnderTest!!.tvAlt!!.viewBounds(),
        )

        assertEquals(
            "tvTitle view bounds",
            "view ${viewUnderTest!!.tvTitle::class.simpleName} has bounds left:0, top:${348.dp()}, right:${384.dp()}, bottom:${362.5f.dp()}",
            viewUnderTest!!.tvTitle.viewBounds(),
        )


        val mockCanvas = mockk<Canvas>(relaxed = true) {
            every { getWidth() } returns 384.dp()
        }

        val lineLeft = mutableListOf<Float>()
        val lineRight = mutableListOf<Float>()

        viewUnderTest!!.dispatchDraw(mockCanvas)
        verifyOrder {
            mockCanvas.drawLine(
                capture(lineLeft),
                capture(lineLeft),
                capture(lineLeft),
                capture(lineLeft),
                any()
            )
            mockCanvas.drawLine(
                capture(lineRight),
                capture(lineRight),
                capture(lineRight),
                capture(lineRight),
                any()
            )
        }

        assertEquals(
            "markdown view draw left decor line",
            "left: 0.0, top: ${355.25f.dpf()}, right: ${56f.dpf()}, bottom : ${355.25f.dpf()}",
            "left: ${lineLeft[0]}, top: ${lineLeft[1]}, right: ${lineLeft[2]}, bottom : ${lineLeft[3]}"
        )

        assertEquals(
            "markdown view draw right decor line",
            "left: ${328f.dpf()}, top: ${355.25f.dpf()}, right: ${384f.dpf()}, bottom : ${355.25f.dpf()}",
            "left: ${lineRight[0]}, top: ${lineRight[1]}, right: ${lineRight[2]}, bottom : ${lineRight[3]}"
        )
    }

    @Test
    fun draw_markdown_scroll_view() {

        val testViewId = 100
        var viewUnderTest: MarkdownCodeView? = null

        val scenario = launchFragmentInContainer<TestFragment>(themeResId = R.style.AppTheme)
        scenario.onFragment {
            viewUnderTest = spyk(MarkdownCodeView(it.requireContext(), 14f, "any code"))
            viewUnderTest!!.id = testViewId
            val container = it.requireView() as FrameLayout
            container.addView(viewUnderTest)
        }
        sleep(2000)

        verifyOrder {
            viewUnderTest!!.onMeasure(any(), any())
            viewUnderTest!!.onLayout(any(), 0, 0, 384.dp(), 32.5f.dp())
        }

        assertEquals("markdown view measure width", 384.dp(), viewUnderTest!!.measuredWidth)
        assertEquals("markdown view measure height", 32.5f.dp(), viewUnderTest!!.measuredHeight)
        assertEquals(
            "markdown scroll measure height",
            16.5f.dp(),
            viewUnderTest!!.svScroll.measuredHeight
        )

        assertEquals(
            "ivSwitch view bounds",
            "view ${viewUnderTest!!.ivSwitch::class.simpleName} has bounds left:${346.dp()}, top:${10.dp()}, right:${358.dp()}, bottom:${22.dp()}",
            viewUnderTest!!.ivSwitch.viewBounds(),
        )

        assertEquals(
            "ivCopy view bounds",
            "view ${viewUnderTest!!.ivCopy::class.simpleName} has bounds left:${364.dp()}, top:${10.dp()}, right:${376.dp()}, bottom:${22.dp()}",
            viewUnderTest!!.ivCopy.viewBounds(),
        )

        scenario.onFragment {
            val cv = it.requireView().findViewById<MarkdownCodeView>(testViewId)
            val container = it.requireView() as FrameLayout
            container.removeView(cv)
            viewUnderTest =
                spyk(MarkdownCodeView(it.requireContext(), 14f, "first line\nsecond line"))
            container.addView(viewUnderTest)
        }
        sleep(2000)
        verifyOrder {
            viewUnderTest!!.onMeasure(any(), any())
            viewUnderTest!!.onLayout(any(), 0, 0, 384.dp(), 46.5f.dp())
        }

        assertEquals("markdown view measure width", 384.dp(), viewUnderTest!!.measuredWidth)
        assertEquals("markdown view measure height", 46.5f.dp(), viewUnderTest!!.measuredHeight)
        assertEquals(
            "markdown scroll measure height",
            30.5f.dp(),
            viewUnderTest!!.svScroll.measuredHeight
        )

        assertEquals(
            "ivSwitch view bounds",
            "view ${viewUnderTest!!.ivSwitch::class.simpleName} has bounds left:${346.dp()}, top:${8.dp()}, right:${358.dp()}, bottom:${20.dp()}",
            viewUnderTest!!.ivSwitch.viewBounds(),
        )

        assertEquals(
            "ivCopy view bounds",
            "view ${viewUnderTest!!.ivCopy::class.simpleName} has bounds left:${364.dp()}, top:${8.dp()}, right:${376.dp()}, bottom:${20.dp()}",
            viewUnderTest!!.ivCopy.viewBounds(),
        )
    }

    @Test
    fun draw_markdown_content_view() {
        val string = """
    ### Finding out where the background should be drawn
    We specify parts of the text that should have a background by using `Annotation` spans in our string resources. Find out more about working with Annotation spans from [this article](https://medium.com/google-developers/styling-internationalized-text-in-android-f99759fb7b8f).

    We created a `SearchBgHelper` class that:

    * Enables us to position the background based on the text directionality: left-to-right or right-to-left
    * Renders the background, based on the drawables and the horizontal and vertical padding

    In the `SearchBgHelper.draw` method, for every `Annotation` span found in the text, we get the start and end index of the span, find the line number for each and then compute the start and end character offset (within the line). Then, we use the `SearchBgRenderer` implementations to render the background.
    ```fun draw(canvas: Canvas, text: Spanned, layout: Layout) {
        // ideally the calculations here should be cached since
        // they are not cheap. However, proper
        // invalidation of the cache is required whenever
        // anything related to text has changed.
        val spans = text.getSpans(0, text.length, Annotation::class.java)
        spans.forEach { span ->
            if (span.value.equals("rounded")) {
                val spanStart = text.getSpanStart(span)
                val spanEnd = text.getSpanEnd(span)
                val startLine = layout.getLineForOffset(spanStart)
                val endLine = layout.getLineForOffset(spanEnd)

                // start can be on the left or on the right depending
                // on the language direction.
                val startOffset = (layout.getPrimaryHorizontal(spanStart)
                    + -1 * layout.getParagraphDirection(startLine) * horizontalPadding).toInt()
                // end can be on the left or on the right depending
                // on the language direction.
                val endOffset = (layout.getPrimaryHorizontal(spanEnd)
                    + layout.getParagraphDirection(endLine) * horizontalPadding).toInt()

                val renderer = if (startLine == endLine) singleLineRenderer else multiLineRenderer
                renderer.draw(canvas, layout, startLine, endLine, startOffset, endOffset)
            }
        }
    }```
    ### Provide drawables as attributes
    To easily supply drawables for different `TextViews` in our app, we define 4 custom attributes corresponding to the drawables and 2 attributes for the horizontal and vertical padding. We created a `TextRoundedBgAttributeReader` class that reads these attributes from the xml layout.
    ### Render the background drawable(s)
    Once we have the drawables we need to draw them. For that, we need to know:

    * The start and end line for the background
    * The character offset where the background should start and end at.

    We created an abstract class `SearchBgRenderer` that knows how to compute the top and the bottom offset of the line, but exposes an abstract `draw` function:
    ```abstract fun draw(canvas: Canvas,layout: Layout,startLine: Int,endLine: Int,startOffset: Int,endOffset: Int)```
    The `draw` function will have different implementations depending on whether our text spans a single line or multiple lines. Both of the implementations work on the same principle: based on the line top and bottom, set the bounds of the drawable and render it on the canvas.

    The single line implementation only needs to draw one drawable.
    ![Single line text with search rounded background](https://miro.medium.com/max/1155/0*HS6zOL8stqjTodpJ "Single line text")
            """.trimIndent()

        val testViewId = 100

        val content = MarkdownParser.parse(string)
        var viewUnderTest: MarkdownContentView? = null
        val scenario = launchFragmentInContainer<TestFragment>(themeResId = R.style.AppTheme)
        scenario.onFragment {
            viewUnderTest = spyk(MarkdownContentView(it.requireContext(), null, 0))
            viewUnderTest!!.id = testViewId
            viewUnderTest!!.setCopyListener { }
            viewUnderTest!!.setContent(content)
            val container = it.requireView() as FrameLayout
            container.addView(viewUnderTest)
        }
        sleep(2000)

        assertEquals("markdown scroll measure width", 384.dp(), viewUnderTest!!.measuredWidth)
        assertEquals("markdown view measure height", 1794.5f.dp(), viewUnderTest!!.measuredHeight)
        assertEquals("markdown content child count", 6, viewUnderTest!!.childCount)
        assertEquals(
            "markdown content code view count",
            2,
            viewUnderTest!!.children.filterIsInstance<MarkdownCodeView>().toList().size
        )
        assertEquals(
            "markdown content text view count",
            3,
            viewUnderTest!!.children.filterIsInstance<MarkdownTextView>().toList().size
        )
        assertEquals(
            "markdown content image view count",
            1,
            viewUnderTest!!.children.filterIsInstance<MarkdownImageView>().toList().size
        )

        scenario.onFragment {
            val vat = it.requireView().findViewById<MarkdownContentView>(testViewId)
            vat.renderSearchResult(listOf(50 to 60))
        }
        sleep(2000)
        val markdownTextView = viewUnderTest!!.getChildAt(0) as MarkdownTextView
        val spansSearch = markdownTextView.spannableContent.getSpans<SearchSpan>()
        val start = markdownTextView.spannableContent.getSpanStart(spansSearch.first())
        val end = markdownTextView.spannableContent.getSpanEnd(spansSearch.first())
        assertEquals("markdown renderSearchResult", "start: 50 end: 60", "start: $start end: $end")

        scenario.onFragment {
            val vat = it.requireView().findViewById<MarkdownContentView>(testViewId)
            vat.clearSearchResult()
        }
        sleep(2000)
        val spans = markdownTextView.spannableContent.getSpans(
            0,
            markdownTextView.spannableContent.length,
            SearchSpan::class.java
        )
        assertEquals(
            "markdown clearSearchResult",
            "search span count 0",
            "search span count ${spans.size}"
        )
    }

    private fun Int.dp() =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), metrics).toInt()

    private fun Int.dpf() =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), metrics)

    private fun Float.dp() =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, metrics).toInt()

    private fun Float.dpf() =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, metrics)

    private fun View.viewBounds() =
        "view ${this::class.simpleName} has bounds left:${this.left}, top:${this.top}, right:${this.right}, bottom:${this.bottom}"

}

class TestFragment() : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FrameLayout(requireContext())
}