package com.example.scandroid;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.hamcrest.Matcher;
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
        // simulate image change
        // choose an image from the drawable resources
        Drawable newImageDrawable = ContextCompat.getDrawable(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                R.drawable.guest_default_image);
        // update the ImageView in UI with the new image drawable
        onView(withId(R.id.image_inside_card)).perform(setImageDrawable(newImageDrawable));
        // checks if camera roll opens
        onView(withId(R.id.changePictureTextView)).perform(click());
        onView(withId(R.id.choose_image_fragment)).check(matches(isDisplayed()));
        onView(withId(R.id.camera_roll_access_button)).perform(click());
    }

    private ViewAction setImageDrawable(Drawable newImageDrawable) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(ImageView.class);
            }

            @Override
            public String getDescription() {
                return "Set drawable to image view";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((ImageView) view).setImageDrawable(newImageDrawable);
            }
        };
    }

    @Test
    public void testRemoveProfilePicture() {
        // remove the current profile picture if it is not a default
        onView(withId(R.id.changePictureTextView)).perform(click());
        onView(withId(R.id.remove_picture_button)).perform(click());
    }

}
