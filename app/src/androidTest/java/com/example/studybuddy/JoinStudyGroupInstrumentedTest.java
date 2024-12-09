package com.example.studybuddy;

import static androidx.test.espresso.Espresso.onIdle;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.not;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class JoinStudyGroupInstrumentedTest
{

    // This test is written by Anna Katherine Zhao

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void joinStudyGroup() throws InterruptedException {
        onView(withId(R.id.username)).perform(typeText("testuser1@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("password123"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.dashboard)).perform(click());
        onView(withId(R.id.joinGroupButton)).perform(click());
        onView(withText("Xia's Group")).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.groupList)).perform(scrollTo()).check(matches(hasDescendant(withText("Xia's Group"))));

        // leave
        onView(withText("Xia's Group")).perform(longClick());
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.groupList)).perform(scrollTo()).check(matches(not(hasDescendant(withText("Xia's Group")))));
    }
}