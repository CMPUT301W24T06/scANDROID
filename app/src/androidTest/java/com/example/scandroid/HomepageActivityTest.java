package com.example.scandroid;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class HomepageActivityTest {
    @Rule
    public
    ActivityScenarioRule<HomepageActivity> scenario = new ActivityScenarioRule<HomepageActivity>(HomepageActivity.class);

    // test editProfileButton
    @Test
    public void testEditProfileButton(){
        Intents.init();
        // press the button
        onView(withId(R.id.edit_profile_button)).perform(click());
        // check if the activity switched by confirming that the UI elements
        // in EditProfileActivity appear
        onView(withId(R.id.edit_profile_image)).check(matches(isDisplayed()));
        onView(withId(R.id.emailLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.aboutMeLayout)).check(matches(isDisplayed()));
        Intents.release();
    }

    // test createEventButton
    @Test
    public void testCreateEventButton(){
        Intents.init();
        // press the button
        onView(withId(R.id.create_event_button)).perform(click());
        // check if the activity switched by confirming that the unique UI elements
        // in CreateEventActivity appear
        onView(withId(R.id.create_event_name_text)).check(matches(isDisplayed()));
        onView(withId(R.id.event_name_edit_text)).check(matches(isDisplayed()));
        onView(withId(R.id.add_poster_icon)).check(matches(isDisplayed()));
        Intents.release();
    }

    // tests for the navigation bar icons
    @Test
    public void testQRScannerButton(){
        Intents.init();
        // press the button
        onView(withId(R.id.qr_button)).perform(click());
        // check if the activity switched by confirming that the unique UI elements
        // in QRScannerActivity appear
        onView(withId(R.id.QR_scan_code_button)).check(matches(isDisplayed()));
        Intents.release();
    }

    @Test
    public void testBrowseButton(){
        Intents.init();
        // press the button
        onView(withId(R.id.browse_button)).perform(click());
        // check if the activity switched by confirming that the unique UI elements
        // in BrowseActivity appear
        onView(withId(R.id.browse_tabs)).check(matches(isDisplayed()));
        onView(withId(R.id.browse_pager)).check(matches(isDisplayed()));
        Intents.release();
    }
}
