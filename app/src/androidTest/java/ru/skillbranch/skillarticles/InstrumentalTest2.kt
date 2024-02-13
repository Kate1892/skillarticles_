//package ru.skillbranch.skillarticles
//
//import android.content.Context
//import android.graphics.Canvas
//import android.os.Bundle
//import android.util.DisplayMetrics
//import android.util.TypedValue
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.FrameLayout
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.testing.launchFragmentInContainer
//import androidx.test.core.app.ApplicationProvider
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import io.mockk.*
//import org.junit.Assert.assertEquals
//import org.junit.BeforeClass
//import org.junit.Test
//import org.junit.runner.RunWith
//import ru.skillbranch.skillarticles.ui.custom.ArticleSubmenu
//import ru.skillbranch.skillarticles.ui.custom.Bottombar
//import ru.skillbranch.skillarticles.ui.custom.CheckableImageView
//import java.lang.Thread.sleep
//
//
///**
// * Instrumented test, which will execute on an Android device.
// *
// * See [testing documentation](http://d.android.com/tools/testing).
// */
//@RunWith(AndroidJUnit4::class)
//class InstrumentalTest2 {
//
//    companion object {
//        var scaledDensity: Float = 0f
//        var metrics: DisplayMetrics = DisplayMetrics()
//        var ctx: Context = ApplicationProvider.getApplicationContext<App>()
//
//        @BeforeClass
//        g@JvmStatic
//        fun setupClass() {
//            scaledDensity = ctx.resources.displayMetrics.scaledDensity
//            metrics = ctx.resources.displayMetrics
//        }
//    }
//
//    @Test
//    fun bottombar() {
//        var viewUnderTest: Bottombar? = null
//
//        val scenario = launchFragmentInContainer<TestFragment>(themeResId = R.style.AppTheme)
//        scenario.onFragment() {
//            viewUnderTest = spyk(
//                Bottombar(it.requireContext()),
//                recordPrivateCalls = true
//            )
//
//            val container = it.requireView() as FrameLayout
//            container.addView(viewUnderTest)
//        }
//        sleep(2000)
//        val bounds = mutableListOf<Int>()
//        verifyOrder {
//            viewUnderTest!!.onMeasure(any(), any())
//            viewUnderTest!!.onLayout(
//                any(),
//                capture(bounds),
//                capture(bounds),
//                capture(bounds),
//                capture(bounds)
//            )
//
//        }
//
//        assertEquals(
//            "Bottombar onLayout ",
//            "left: 0, top: 0, right: ${384.dp()}, bottom : ${56.dp()}",
//            "left: ${bounds[0]}, top: ${bounds[1]}, right: ${bounds[2]}, bottom : ${bounds[3]}"
//        )
//        assertEquals(
//            "btnLike view bounds",
//            "view ${viewUnderTest!!.btnLike::class.simpleName} has bounds left:0, top:0, right:${56.dp()}, bottom:${56.dp()}",
//            viewUnderTest!!.btnLike.viewBounds(),
//        )
//
//        assertEquals(
//            "btnBookmark view bounds",
//            "view ${viewUnderTest!!.btnBookmark::class.simpleName} has bounds left:${56.dp()}, top:0, right:${112.dp()}, bottom:${56.dp()}",
//            viewUnderTest!!.btnBookmark.viewBounds(),
//        )
//
//        assertEquals(
//            "btnShare view bounds",
//            "view ${viewUnderTest!!.btnShare::class.simpleName} has bounds left:${112.dp()}, top:0, right:${168.dp()}, bottom:${56.dp()}",
//            viewUnderTest!!.btnShare.viewBounds(),
//        )
//
//        assertEquals(
//            "btnSettings view bounds",
//            "view ${viewUnderTest!!.btnSettings::class.simpleName} has bounds left:${328.dp()}, top:0, right:${384.dp()}, bottom:${56.dp()}",
//            viewUnderTest!!.btnSettings.viewBounds(),
//        )
//
//        assertEquals(
//            "btnSearchClose view bounds",
//            "view ${viewUnderTest!!.btnSearchClose::class.simpleName} has bounds left:0, top:0, right:${56.dp()}, bottom:${56.dp()}",
//            viewUnderTest!!.btnSearchClose.viewBounds(),
//        )
//
//        assertEquals(
//            "tvSearchResult view bounds",
//            "view ${viewUnderTest!!.tvSearchResult::class.simpleName} has bounds left:${56.dp()}, top:${18.5f.dp()}, right:${134.dp()}, bottom:${37.5f.dp()}",
//            viewUnderTest!!.tvSearchResult.viewBounds(),
//        )
//
//        assertEquals(
//            "btnResultDown view bounds",
//            "view ${viewUnderTest!!.btnResultDown::class.simpleName} has bounds left:${272.dp()}, top:0, right:${328.dp()}, bottom:${56.dp()}",
//            viewUnderTest!!.btnResultDown.viewBounds(),
//        )
//
//        assertEquals(
//            "btnResultUp view bounds",
//            "view ${viewUnderTest!!.btnResultUp::class.simpleName} has bounds left:${328.dp()}, top:0, right:${384.dp()}, bottom:${56.dp()}",
//            viewUnderTest!!.btnResultUp.viewBounds(),
//        )
//    }
//
//    @Test
//    fun articlesubmenu() {
//        var viewUnderTest: ArticleSubmenu? = null
//
//        val scenario = launchFragmentInContainer<TestFragment>(themeResId = R.style.AppTheme)
//        scenario.onFragment() {
//            viewUnderTest = spyk(
//                ArticleSubmenu(it.requireContext()),
//                recordPrivateCalls = true
//            )
//
//            val container = it.requireView() as FrameLayout
//            container.addView(viewUnderTest)
//        }
//        sleep(2000)
//        val bounds = mutableListOf<Int>()
//        verifyOrder {
//            viewUnderTest!!.onMeasure(any(), any())
//            viewUnderTest!!.onLayout(
//                any(),
//                capture(bounds),
//                capture(bounds),
//                capture(bounds),
//                capture(bounds)
//            )
//            viewUnderTest!!.dispatchDraw(any())
//        }
//
//        assertEquals(
//            "ArticleSubmenu onLayout ",
//            "left: 0, top: 0, right: ${200.dp()}, bottom : ${96.dp()}",
//            "left: ${bounds[0]}, top: ${bounds[1]}, right: ${bounds[2]}, bottom : ${bounds[3]}"
//        )
//
//        assertEquals(
//            "btnTextDown view bounds",
//            "view ${viewUnderTest!!.btnTextDown::class.simpleName} has bounds left:0, top:0, right:${100.dp()}, bottom:${40.dp()}",
//            viewUnderTest!!.btnTextDown.viewBounds(),
//        )
//
//        assertEquals(
//            "btnBookmark view bounds",
//            "view ${viewUnderTest!!.btnTextUp::class.simpleName} has bounds left:${100.dp()}, top:0, right:${200.dp()}, bottom:${40.dp()}",
//            viewUnderTest!!.btnTextUp.viewBounds(),
//        )
//
//        assertEquals(
//            "tvLabel view bounds",
//            "view ${viewUnderTest!!.tvLabel::class.simpleName} has bounds left:${16.dp()}, top:${58.5f.dp()}, right:${117.dp()}, bottom:${77.5f.dp()}",
//            viewUnderTest!!.tvLabel.viewBounds(),
//        )
//
//        assertEquals(
//            "switchMode view bounds",
//            "view ${viewUnderTest!!.switchMode::class.simpleName} has bounds left:${136.dp()}, top:${44.dp()}, right:${184.dp()}, bottom:${92.dp()}",
//            viewUnderTest!!.switchMode.viewBounds(),
//        )
//
//    }
//
//    private fun Int.dp() =
//        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), metrics).toInt()
//
//    private fun Int.dpf() =
//        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), metrics)
//
//    private fun Float.dp() =
//        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, metrics).toInt()
//
//    private fun Float.dpf() =
//        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, metrics)
//
//    private fun View.viewBounds() =
//        "view ${this::class.simpleName} has bounds left:${this.left}, top:${this.top}, right:${this.right}, bottom:${this.bottom}"
//
//}
//
