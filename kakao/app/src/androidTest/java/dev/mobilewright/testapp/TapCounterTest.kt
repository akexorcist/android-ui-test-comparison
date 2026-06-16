package dev.mobilewright.testapp

import android.util.Log
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.kakaocup.kakao.edit.KEditText
import io.github.kakaocup.kakao.screen.Screen
import io.github.kakaocup.kakao.text.KButton
import io.github.kakaocup.kakao.text.KTextView
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TapCounterTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    class MwScreen : Screen<MwScreen>() {
        val tapButton = KButton { withContentDescription("tap-button") }
        val counter = KTextView { withContentDescription("counter") }
        val input = KEditText { withContentDescription("input-field") }
        val echoButton = KButton { withContentDescription("echo-button") }
        val echo = KTextView { withContentDescription("echo") }
        val longButton = KButton { withContentDescription("long-button") }
        val longStatus = KTextView { withContentDescription("long-status") }
        val bottomMarker = KTextView { withContentDescription("bottom-marker") }
    }

    private fun mark(tag: String, t0: Long) = Log.i("MWBENCH", "$tag=${System.currentTimeMillis() - t0}")

    @Test
    fun comprehensive6() {
        Screen.onScreen<MwScreen> {
            repeat(6) { i ->
                var t = System.currentTimeMillis()
                tapButton.click(); mark("tap", t); t = System.currentTimeMillis()
                counter.hasText("Count: ${i * 2 + 1}"); mark("verify_tap", t); t = System.currentTimeMillis()

                // raw Espresso (no Kakao wrapper) for comparison
                onView(withContentDescription("tap-button")).perform(ViewActions.click()); mark("raw_click", t); t = System.currentTimeMillis()
                onView(withContentDescription("counter")).check(matches(isDisplayed())); mark("raw_check", t); t = System.currentTimeMillis()

                input.clearText(); input.typeText("hello"); Espresso.closeSoftKeyboard(); mark("type", t); t = System.currentTimeMillis()
                echoButton.click(); mark("tap_echo", t); t = System.currentTimeMillis()
                echo.hasText("Echo: hello"); mark("verify_type", t); t = System.currentTimeMillis()
                longButton.longClick(); mark("long_press", t); t = System.currentTimeMillis()
                longStatus.hasText("Long: pressed"); mark("verify_long", t); t = System.currentTimeMillis()
                bottomMarker.scrollTo(); mark("swipe", t); t = System.currentTimeMillis()
                bottomMarker.isDisplayed(); mark("verify_swipe", t)
                counter.scrollTo()
            }
        }
    }
}
