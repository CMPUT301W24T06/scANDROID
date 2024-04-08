package com.example.scandroid;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SendAnnouncementTest {
    @Rule
    public
    ActivityScenarioRule<HomepageActivity> scenario = new ActivityScenarioRule<HomepageActivity>(HomepageActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void addEvent() {
        onView(withId(R.id.create_event_button)).perform(click());
        //initialize event
        onView(withId(R.id.event_name_edit_text)).perform(typeText("Test Event"));
        onView(withId(R.id.event_location_edit_text)).perform(typeText("Edmonton"));
        closeSoftKeyboard();
        onView(withId(R.id.edit_event_time_button)).perform(click());
        onView(isAssignableFrom(TimePicker.class)).perform(QRCodeGeneratorTest.PickerAction.setTimeInTimePicker(12, 0));
        onView(withText("OK")).perform(click());
        onView(withId(R.id.edit_event_date_button)).perform(click());
        onView(isAssignableFrom(DatePicker.class)).perform(QRCodeGeneratorTest.PickerAction.setDateInDatePicker(2024, 6, 23));
        onView(withText("OK")).perform(click());
        onView(withId(R.id.event_description_edit_text)).perform(typeText("Making a Test Event"));
        closeSoftKeyboard();
        onView(withId(R.id.create_event_confirm_button)).perform(click());
    }

    @Test
    public void testCreateAnnouncement() throws InterruptedException {
        // check if Firebase list is loaded before performing a click
        Thread.sleep(5000);
        onView(withId(R.id.my_events_list)).check(matches(isDisplayed()));
        onView(withId(R.id.my_events_list)).perform(click());

        onView(withId(R.id.announcements_button)).check(matches(isDisplayed()));
        onView(withId(R.id.announcements_button)).perform(click());

        onView(withId(R.id.edit_notification_title)).perform(typeText("Test Announcement"));
        onView(withId(R.id.edit_notification_info)).perform(typeText("Test Info"));
        closeSoftKeyboard();

        onView(withId(R.id.send_notification_button)).perform(click());
    }
}