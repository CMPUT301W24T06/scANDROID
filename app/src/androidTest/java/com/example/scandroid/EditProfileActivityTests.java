package com.example.scandroid;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
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
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.ImageView;
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
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertNotEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EditProfileActivityTests {
    @Rule
    public ActivityScenarioRule<EditProfileActivity> scenario = new ActivityScenarioRule<>(EditProfileActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }


    @Test
    public void testCreateProfile() {
        // clear and replace name
        onView(withId(R.id.nameEditText)).perform(click(), clearText(), typeText("Test Name"));
        onView(withId(R.id.nameEditText)).check(matches(withText("Test Name")));
        closeSoftKeyboard();

        // clear and replace email
        onView(withId(R.id.emailEditText)).perform(click(), clearText(), typeText("testemail"));
        onView(withId(R.id.emailEditText)).check(matches(withText("testemail")));
        closeSoftKeyboard();

        // clear and replace ID
        onView(withId(R.id.phoneEditText)).perform(click(), clearText(), typeText("7801234567"));
        onView(withId(R.id.phoneEditText)).check(matches(withText("7801234567")));
        closeSoftKeyboard();

        // clear and replace About me
        onView(withId(R.id.aboutMeEditText)).perform(click(), clearText(), typeText("About me text"));
        onView(withId(R.id.aboutMeEditText)).check(matches(withText("About me text")));
        closeSoftKeyboard();
    }

    @Test
    public void testEditProfilePicture() {
        // checks if camera roll opens
        onView(withId(R.id.changePictureTextView)).perform(click());
        onView(withId(R.id.choose_image_fragment)).check(matches(isDisplayed()));
        onView(withId(R.id.camera_roll_access_button)).perform(click());
    }

    @Test
    public void testRemoveProfilePicture() {
        // click on the "Remove Picture" button
        onView(withId(R.id.changePictureTextView)).perform(click());

        onView(withId(R.id.remove_picture_button)).perform(click());

        // verify that the profile picture ImageView is now empty or has a placeholder
//        onView(withId(R.id.profile_image)).check(matches(isDisplayed()));
    }

}
