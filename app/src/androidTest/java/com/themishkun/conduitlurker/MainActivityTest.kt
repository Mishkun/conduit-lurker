package com.themishkun.conduitlurker


import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun mainActivityTest() {
        onView(
            allOf(
                withParent(
                    allOf(
                        withClassName(`is`(LinearLayout::class.java.name)),
                        withParent(withClassName(`is`(ScrollView::class.java.name)))
                    )
                ),
                withParentIndex(0)
            )
        ).perform(click())
    }
}
