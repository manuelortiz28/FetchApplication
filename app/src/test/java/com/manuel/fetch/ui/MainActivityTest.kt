package com.manuel.fetch.ui

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToLastPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.manuel.fetch.R
import com.manuel.fetch.ui.di.HiringServiceHolder
import com.manuel.fetch.ui.matcher.atPosition
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.coEvery
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowToast
import javax.inject.Inject

@RunWith(RobolectricTestRunner::class)
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
class MainActivityTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var mockHiringServiceHolder: HiringServiceHolder

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun `loading the screen should display buttons`() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                onView(withId(R.id.group_sorting)).check(matches(isDisplayed()))
                onView(withId(R.id.name_sorting)).check(matches(isDisplayed()))
            }
        }
    }

    @Test
    fun `service returning a successful response should load groups data in ascending order`() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                onView(withId(R.id.hirings_recycler_view))
                    .check(matches(isDisplayed()))
                    .check(matches(atPosition(0, withText("Group: 1"))))
                    .check(matches(atPosition(1, withText("Alice"))))
                    .check(matches(atPosition(2, withText("Danny"))))
                    .check(matches(atPosition(3, withText("Ryan"))))
                    .perform(scrollToPosition<RecyclerView.ViewHolder>(7))

                onView(withId(R.id.hirings_recycler_view))
                    .check(matches(atPosition(4, withText("Group: 2"))))
                    .check(matches(atPosition(5, withText("Charlie"))))
                    .check(matches(atPosition(6, withText("Thomas"))))
                    .perform(scrollToLastPosition<RecyclerView.ViewHolder>())

                onView(withId(R.id.hirings_recycler_view))
                    .check(matches(atPosition(7, withText("Group: 3"))))
                    .check(matches(atPosition(8, withText("Benny"))))
                    .check(matches(atPosition(9, withText("Bob"))))
                    .check(matches(atPosition(10, withText("John"))))
                    .check(matches(atPosition(11, withText("Ross"))))
            }
        }
    }

    @Test
    fun `service returning a failure should display error message`() {
        coEvery { mockHiringServiceHolder.hiringsService.fetchHirings() } throws RuntimeException("Something went wrong")

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val toastMessage = ShadowToast.getTextOfLatestToast()
                assertEquals("Error: Something went wrong", toastMessage)
            }
        }
    }

    @Test
    fun `clicking group sorting button should sort groups in descending order`() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                onView(withId(R.id.hirings_recycler_view))
                    .check(matches(isDisplayed()))

                onView(withId(R.id.group_sorting)).perform(click())

                onView(withId(R.id.hirings_recycler_view))
                    .check(matches(isDisplayed()))
                    .check(matches(atPosition(0, withText("Group: 3"))))
                    .check(matches(atPosition(1, withText("Benny"))))
                    .check(matches(atPosition(2, withText("Bob"))))
                    .check(matches(atPosition(3, withText("John"))))
                    .check(matches(atPosition(4, withText("Ross"))))
                    .perform(scrollToPosition<RecyclerView.ViewHolder>(8))

                onView(withId(R.id.hirings_recycler_view))
                    .check(matches(atPosition(5, withText("Group: 2"))))
                    .check(matches(atPosition(6, withText("Charlie"))))
                    .check(matches(atPosition(7, withText("Thomas"))))
                    .perform(scrollToLastPosition<RecyclerView.ViewHolder>())

                onView(withId(R.id.hirings_recycler_view))
                    .check(matches(isDisplayed()))
                    .check(matches(atPosition(8, withText("Group: 1"))))
                    .check(matches(atPosition(9, withText("Alice"))))
                    .check(matches(atPosition(10, withText("Danny"))))
                    .check(matches(atPosition(11, withText("Ryan"))))
            }
        }
    }

    @Test
    fun `clicking name sorting button should sort hirings in descending order`() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                onView(withId(R.id.hirings_recycler_view))
                    .check(matches(isDisplayed()))

                onView(withId(R.id.name_sorting)).perform(click())

                onView(withId(R.id.hirings_recycler_view))
                    .check(matches(isDisplayed()))
                    .check(matches(atPosition(0, withText("Group: 1"))))
                    .check(matches(atPosition(1, withText("Ryan"))))
                    .check(matches(atPosition(2, withText("Danny"))))
                    .check(matches(atPosition(3, withText("Alice"))))
                    .perform(scrollToPosition<RecyclerView.ViewHolder>(7))

                onView(withId(R.id.hirings_recycler_view))
                    .check(matches(atPosition(4, withText("Group: 2"))))
                    .check(matches(atPosition(5, withText("Thomas"))))
                    .check(matches(atPosition(6, withText("Charlie"))))
                    .perform(scrollToLastPosition<RecyclerView.ViewHolder>())

                onView(withId(R.id.hirings_recycler_view))
                    .check(matches(atPosition(7, withText("Group: 3"))))
                    .check(matches(atPosition(8, withText("Ross"))))
                    .check(matches(atPosition(9, withText("John"))))
                    .check(matches(atPosition(10, withText("Bob"))))
                    .check(matches(atPosition(11, withText("Benny"))))
            }
        }
    }
}
