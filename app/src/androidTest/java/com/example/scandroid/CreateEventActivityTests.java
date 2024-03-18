package com.example.scandroid;


import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

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
public class CreateEventActivityTests {
    @Rule
    public ActivityScenarioRule<CreateEventActivity> scenario = new
            ActivityScenarioRule<>(CreateEventActivity.class);
    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }
    @Test
    public void testAddEvent() {
        // fill in all fields
        onView(withId(R.id.event_name_edit_text)).perform(click(), clearText(), typeText("Test Event"));

        onView(withId(R.id.event_location_edit_text)).perform(click(), clearText(), typeText("Edmonton"));
        onView(withId(R.id.edit_event_date_button)).perform(click());
        closeSoftKeyboard();

        onView(isAssignableFrom(DatePicker.class)).perform(PickerAction.setDateInDatePicker(2024, 6, 23));
        onView(withText("OK")).perform(click());
        closeSoftKeyboard();

        onView(withId(R.id.edit_event_time_button)).perform(click());
                onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(setHour(12));
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(setMinute(0));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.event_description_edit_text)).perform(click(), clearText(), typeText("Making a Test Event"));
        closeSoftKeyboard();
        onView(withId(R.id.create_event_confirm_button)).perform(click());
    }

    public static ViewAction setHour(final int hour) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(TimePicker.class);
            }

            @Override
            public String getDescription() {
                return "Set the hour";
            }

            @Override
            public void perform(UiController uiController, View view) {
            }
        };
    }

    public static ViewAction setMinute(final int minute) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(TimePicker.class);
            }

            @Override
            public String getDescription() {
                return "Set the minute";
            }

            @Override
            public void perform(UiController uiController, View view) {
            }
        };
    }

    //OpenAI, 2024, ChatGPT, How to make Intent tests for images more robust
    @Test
    public void testEditEventPoster() {
        // result intent with image data
        Intent resultData = new Intent();
        resultData.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // stub the gallery intent to return the result intent
        intending(isInternal()).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData));

        // open gallery
        onView(withId(R.id.create_event_change_poster)).perform(click());

        // verify that the intent to pick an image is sent
        intended(hasAction(Intent.ACTION_PICK));
        intended(hasData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
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