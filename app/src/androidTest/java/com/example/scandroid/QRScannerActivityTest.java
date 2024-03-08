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

import androidx.activity.result.ActivityResultLauncher;
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

import com.journeyapps.barcodescanner.ScanOptions;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class QRScannerActivityTest {
    @Rule
    public
    ActivityScenarioRule<QRScannerActivity> scenario = new ActivityScenarioRule<QRScannerActivity>(QRScannerActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    // tests for the navigation bar icons
    @Test
    public void testHomeButton(){
        // press the button
        onView(withId(R.id.home_button)).perform(click());
        // check if the activity switched by confirming that the unique UI elements
        // in HomepageActivity appear
        onView(withId(R.id.edit_profile_button)).check(matches(isDisplayed()));
        onView(withId(R.id.create_event_button)).check(matches(isDisplayed()));
        onView(withId(R.id.homepage_name_text)).check(matches(isDisplayed()));
    }

    @Test
    public void testBrowseButton(){
        // press the button
        onView(withId(R.id.browse_button)).perform(click());
        // check if the activity switched by confirming that the unique UI elements
        // in BrowseActivity appear
        onView(withId(R.id.browse_tabs)).check(matches(isDisplayed()));
        onView(withId(R.id.browse_pager)).check(matches(isDisplayed()));
    }

    // test if the QR Code Scanner opens and runs
    @Test
    public void testQRScanner(){
        // press the button
        onView(withId(R.id.QR_scan_code_button)).perform(click());
        // verify if the camera activity is launched
        intended(hasComponent(CaptureAct.class.getName()));
    }
}
