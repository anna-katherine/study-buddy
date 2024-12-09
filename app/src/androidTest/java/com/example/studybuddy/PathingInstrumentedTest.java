package com.example.studybuddy;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.fail;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class PathingInstrumentedTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    //Tests below are written by Alex Flat
    @Test
    public void pathToDashboard() {
        // Should logout user and bring them to login page.
        Login();
        stall(2000);
        navToDashboard();
        onView(withId(R.id.groupList)).check(matches(isDisplayed()));
    }

    @Test
    public void pathToStudyGroup(){
        Login();
        stall(2000);
        navToStudyGroup();
        onView(withId(R.id.memberList)).check(matches(isDisplayed()));
    }

    @Test
    public void pathToGroupChats(){
        Login();
        stall(2000);
        navToGroupChats();
        onView(withId(R.id.groupChatList)).check(matches(isDisplayed()));
    }

    @Test
    public void pathToMainGroupChat(){
        Login();
        stall(2000);
        navToMainChat();
        onView(withId(R.id.chatMessagesListView)).check(matches(isDisplayed()));
    }

    @Test
    public void pathToCalendar(){
        Login();
        stall(2000);
        navToStudyGroup();
        onView(withId(R.id.calendar)).perform(click());
        stall(2000);
        onView(withId(R.id.calendarTitle)).check(matches(isDisplayed()));
    }

    private void navToMainChat(){
        navToGroupChats();
        stall(2000);
        onView(withText("Main GroupChat")).perform(click());
        stall(2000);
    }
    private void navToGroupChats(){
        navToStudyGroup();
        stall(2000);
        onView(withId(R.id.chat)).perform(click());
        stall(2000);
    }

    private void navToStudyGroup(){
        navToDashboard();
        onView(withText("Alex's MATH Group")).perform(click());
        stall(2000);
    }
    private void Login(){
        onView(withId(R.id.username)).perform(typeText("testuser1@gmail.com"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("password123"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
    }

    private void navToDashboard(){
        onView(withId(R.id.dashboard)).perform(click());
        stall(2000);
    }

    private void stall(int time){
        try {
            Thread.sleep(time);
        }
        catch (InterruptedException e){
            fail();
        }
    }
}