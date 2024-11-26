package com.example.studybuddy;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

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
        onView(withId(R.id.username)).perform(typeText("tiffli@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("tifftest"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.dashboard)).perform(click());
        onView(withId(R.id.joinGroupButton)).perform(click());
        onView(withText("Alex's MATH Group")).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.groupList)).perform(scrollTo()).check(matches(hasDescendant(withText("Alex's MATH Group"))));
    }
}