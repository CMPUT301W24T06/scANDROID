package com.example.scandroid;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest

public class SignUpTest {

    @Rule
    public ActivityScenarioRule<BrowseActivity> activityScenarioRule = new ActivityScenarioRule<>(BrowseActivity.class);

    @Test
    public void testSignUpToEventFromDetails() throws InterruptedException {
        // Click on the Events tab
        onView(withText("Events")).perform(click());

        // Wait for 10 seconds (10000 milliseconds)
        Thread.sleep(10000);

        // Click on the first event in the list
        onView(withId(R.id.browse_event_list)).perform(click());

        // Wait for 10 seconds (10000 milliseconds)
        Thread.sleep(10000);

        // Click on sign up checkbox
        onView(withId(R.id.promise_checkbox)).perform(click());
    }
}