package com.example.scandroid;


import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertTrue;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.StringDef;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class QRCodeGeneratorTest {
    @Rule
    public ActivityScenarioRule<CheckInScreenActivity> scenario = new ActivityScenarioRule<>(CheckInScreenActivity.class);

    @Test
    public void addEvent() {
        onView(withId(R.id.check_in_button)).perform(click());
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
        //click on the events list for any event
        onView(withId(R.id.my_events_list)).perform(click());
        onView(withId(R.id.QR_code_info_button)).perform(click());
        onView(withId(R.id.check_in_qr_code_textview)).check(matches(withText("Check-In QR Code")));

    }
    public static class PickerAction {

        public static ViewAction setTimeInTimePicker(final int hour, final int minute) {
            return new ViewAction() {
                @Override
                public Matcher<View> getConstraints() {
                    return Matchers.allOf(
                            isAssignableFrom(TimePicker.class),
                            ViewMatchers.isDisplayed()
                    );
                }

                @Override
                public String getDescription() {
                    return "Set the time in the TimePicker";
                }

                @Override
                public void perform(UiController uiController, View view) {
                    TimePicker timePicker = (TimePicker) view;
                    timePicker.setCurrentHour(hour);
                    timePicker.setCurrentMinute(minute);
                }
            };
        }

        public static ViewAction setDateInDatePicker(final int year, final int month, final int day) {
            return new ViewAction() {
                @Override
                public Matcher<View> getConstraints() {
                    return Matchers.allOf(
                            isAssignableFrom(DatePicker.class),
                            ViewMatchers.isDisplayed()
                    );
                }

                @Override
                public String getDescription() {
                    return "Set the date in the DatePicker";
                }

                @Override
                public void perform(UiController uiController, View view) {
                    DatePicker datePicker = (DatePicker) view;
                    datePicker.updateDate(year, month, day);
                }
            };

        }
    }
}

