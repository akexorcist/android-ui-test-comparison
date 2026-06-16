package dev.mobilewright.testapp

import androidx.test.espresso.Espresso
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.kakaocup.kakao.edit.KEditText
import io.github.kakaocup.kakao.screen.Screen
import io.github.kakaocup.kakao.text.KButton
import io.github.kakaocup.kakao.text.KTextView
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Kakao/Espresso version of the shared benchmark flow:
 *   tap -> verify, type -> verify, long-press -> verify, swipe/scroll -> verify.
 */
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

    @Test
    fun basicInteractions() {
        Screen.onScreen<MwScreen> {
            // 1) tap
            tapButton.click()
            counter.hasText("Count: 1")

            // 2) type text
            input.typeText("hello")
            Espresso.closeSoftKeyboard()
            echoButton.click()
            echo.hasText("Echo: hello")

            // 3) long press
            longButton.longClick()
            longStatus.hasText("Long: pressed")

            // 4) swipe / scroll
            bottomMarker.scrollTo()
            bottomMarker.isDisplayed()
        }
    }
}
