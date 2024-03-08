package com.example.scandroid;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;
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
public class EditProfileActivityTests {
    @Rule
    public ActivityScenarioRule<EditProfileActivity> scenario = new
            ActivityScenarioRule<>(EditProfileActivity.class);

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

        onView(withId(R.id.nameEditText)).perform(clearText(), typeText("Test Name"));
        closeSoftKeyboard();
        onView(withId(R.id.nameEditText)).check(matches(withText("Test Name")));

        onView(withId(R.id.emailEditText)).perform(typeText("testemail"));
        onView(withId(R.id.emailEditText)).check(matches(withText("testemail")));
        closeSoftKeyboard();

        onView(withId(R.id.phoneEditText)).perform(typeText("7801234567"));
        onView(withId(R.id.phoneEditText)).check(matches(withText("7801234567")));
        closeSoftKeyboard();

        onView(withId(R.id.aboutMeEditText)).perform(clearText(), typeText("About me text"));
        onView(withId(R.id.aboutMeEditText)).check(matches(withText("About me text")));
    }

    @Test
    public void testEditProfilePicture(){
        onView(ViewMatchers.withId(R.id.changePictureTextView)).perform(ViewActions.click());
        onView(withId(R.id.choose_image_fragment)).check(matches(isDisplayed()));
        onView(withId(R.id.camera_roll_access_button)).perform(click());
        intended(hasAction(Intent.ACTION_PICK));
    }

    @Test
    public void testReturnToHomePage() {
        onView(withId(R.id.aboutMeEditText)).perform(typeText("About me text"));
        closeSoftKeyboard();
        onView(ViewMatchers.withId(R.id.updateButton)).perform(ViewActions.click());
        intended(hasComponent(HomepageActivity.class.getName()));
    }
}
