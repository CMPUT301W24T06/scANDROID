package com.example.scandroid;


import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

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
public class CreateEventActivityTests {
    @Rule
    public ActivityScenarioRule<CreateEventActivity> scenario = new
            ActivityScenarioRule<>(CreateEventActivity.class);
    @Test
    public void testAddEvent() {
        onView(withId(R.id.event_name_edit_text)).perform(typeText("Test Event"));
        onView(withId(R.id.event_location_edit_text)).perform(typeText("Edmonton"));
        onView(withId(R.id.edit_event_time_button)).perform(click());
        onView(isAssignableFrom(TimePicker.class)).perform(PickerAction.setTimeInTimePicker(12, 0));
        onView(withText("OK")).perform(click());
        onView(withId(R.id.edit_event_date_button)).perform(click());
        onView(isAssignableFrom(DatePicker.class)).perform(PickerAction.setDateInDatePicker(2024, 6, 23));
        onView(withText("OK")).perform(click());
        onView(withId(R.id.event_description_edit_text)).perform(typeText("Making a Test Event"));
        closeSoftKeyboard();
        onView(withId(R.id.create_event_confirm_button)).perform(click());
    }

    //OpenAI, 2024, ChatGPT, How to test TimePickerDialogs and DatePickerDialogs with custom view actions
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