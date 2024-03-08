package com.example.scandroid;
import android.content.Intent;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

@RunWith(AndroidJUnit4.class)
public class CheckInScreenActivityTest {

    @Rule
    public ActivityScenarioRule<CheckInScreenActivity> activityRule =
            new ActivityScenarioRule<>(CheckInScreenActivity.class);

    @Test
    public void checkInButtonClicked_opensHomepageActivity() {
        // Register Intents to monitor and verify them later
        Intents.init();

        // Click the check-in button
        onView(ViewMatchers.withId(R.id.check_in_button)).perform(click());

        // Verify that the intent to open HomepageActivity is sent
        intended(hasComponent(HomepageActivity.class.getName()));

        // Unregister Intents to avoid interference with other tests
        Intents.release();
    }
}