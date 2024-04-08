package com.example.scandroid;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withParentIndex;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.contains;

import static java.nio.file.Files.exists;

import android.widget.ListView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.android.material.tabs.TabItem;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

//made no separate test for the browsing portion; it is implicitly tested. If you cannot view an event, you cannot delete it.
@LargeTest
@RunWith(AndroidJUnit4.class)
public class AdminViewsTests {
    //start from the top. create a new user profile with admin permissions each time!!!
    private ActivityScenario<CheckInScreenActivity> scenario;

    @Before
    //what to do before each test
    public void setUp(){
        //launch the checkin screen
        scenario = ActivityScenario.launch(CheckInScreenActivity.class);
        Intents.init();
        //click check in
        onView(withId(R.id.check_in_button)).perform(click());
        //should now be on the homepage activity
        onView(withId(R.id.edit_profile_button)).perform(click());
        //click admin wrench button
        onView(withId(R.id.admin_wrench_button)).perform(click());
        onView(withId(R.id.key_admin_text)).perform(click(), clearText(), typeText("ThisPersonIsAnAdmin1298"));
        onView(withId(R.id.confirm_button_admin)).perform(click());
        onView(withId(R.id.updateButton)).perform(click());
        //set up admin permissions: complete
    }

    @After
    public void tearDown(){
        Intents.release();
        scenario.close();
    }

    @Test
    public void testAdminViewAndDeleteUserDisplay(){
        onView(withId(R.id.browse_button)).perform(click());
        //maybe click the first item in the view
        waitForViewToDisappear(R.id.loading_browse_users_text,60000);
        onData(anything()).inAdapterView(withId(R.id.browse_users_list)).atPosition(0).perform(longClick());
        onView(withId(R.id.remove_user_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testAdminViewAndDeleteEventDisplay(){
        onView(withId(R.id.browse_button)).perform(click());
        //maybe click the first item in the view
        onView(withText("Events")).perform(click());
        waitForViewToDisappear(R.id.loading_browse_events_text,60000);
        onData(anything()).inAdapterView(withId(R.id.browse_event_list)).atPosition(0).perform(longClick());
        onView(withId(R.id.inspect_remove_event_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testAdminViewAndDeleteImagesDisplay(){
        onView(withId(R.id.browse_button)).perform(click());
        waitForViewToDisappear(R.id.loading_browse_users_text,60000);
        onData(anything()).inAdapterView(withId(R.id.browse_users_list)).atPosition(0).onChildView(withId(R.id.user_content_profile_picture)).perform(click());
        onView(withId(R.id.cancel_image_inspect_button)).perform(click());
    }

    //source: Stackoverflow - answer by "Adam Burley"
    //https://stackoverflow.com/a/68528795
    public void waitForViewToDisappear(int viewId, long maxWaitTimeInMs){
        long endTime = System.currentTimeMillis() + maxWaitTimeInMs;
        while(System.currentTimeMillis() <=endTime){
            try{
                onView(allOf(withId(viewId), isDisplayed())).check(matches(not(doesNotExist())));
            }catch (NoMatchingViewException exception){
                return;
            }
        }
        throw new RuntimeException("timeout exceeded!");
    }
    public void waitForViewToAppear(int viewId, long maxWaitTimeInMs){
        long endtime = System.currentTimeMillis() + maxWaitTimeInMs;
        while (System.currentTimeMillis() <= endtime){
            try{

            }
            catch (NoMatchingViewException ex){
                return;
            }
        }
        throw new RuntimeException("timeout exceeded!");
    }

}
