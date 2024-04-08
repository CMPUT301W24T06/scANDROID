package com.example.scandroid;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class BrowseEventsAndPostersTest {

    @Rule
    public ActivityScenarioRule<BrowseActivity> activityScenarioRule = new ActivityScenarioRule<>(BrowseActivity.class);


    @Test
    public void testBrowseEventsAndOpenDetails() throws InterruptedException {
        // Click on the Events tab
        onView(withText("Events")).perform(click());

        // Wait for 10 seconds (10000 milliseconds)
        Thread.sleep(10000);

        // Click on the first event in the list
        onView(withId(R.id.browse_event_list)).perform(click());
        //click on the poster
        onView(withId(R.id.create_event_change_poster)).perform(click());


        // Check if we can view the poster
        onView(withId(R.id.create_event_change_poster)).check(matches(isDisplayed()));
    }
}
