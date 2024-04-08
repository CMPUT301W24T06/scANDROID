package com.example.scandroid;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class BrowseSignedUpEventsTest {

    @Rule
    public ActivityTestRule<HomepageActivity> activityTestRule = new ActivityTestRule<>(HomepageActivity.class);

    @Test
    public void testAttendingEventsFragment() throws InterruptedException {
        // Click on the "Attending Events" tab, from here you can view all the events you signed up for/checked in to
        onView(withText("Attending Events")).perform(click());
        Thread.sleep(1000);

        // Click on the first event in the list
        onView(withId(R.id.my_events_list)).perform(click());

    }
}
