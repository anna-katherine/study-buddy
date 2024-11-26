package com.example.studybuddy;

import static android.app.PendingIntent.getActivity;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import static org.hamcrest.Matchers.is;

import static java.util.function.Predicate.not;

import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class LoginInstrumentedTest
{
    private View decorView;

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void setUp() {
        activityRule.getScenario().onActivity(new ActivityScenario.ActivityAction<LoginActivity>() {
            @Override
            public void perform(LoginActivity activity) {
                decorView = activity.getWindow().getDecorView();
            }
        });
    }
    @Test
    public void testSuccessfulLogin() {
        onView(withId(R.id.username)).perform(typeText("testuser@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("password123"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.courseList)).check(matches(isDisplayed()));
    }

    @Test
    public void testUnsuccessfulLogin() {
        onView(withId(R.id.username)).perform(typeText("wrong@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("wrongpassword"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        onView(withText("Login failed.")).inRoot(withDecorView(Matchers.not(decorView))).check(matches(isDisplayed()));
    }

    @Test
    public void noUsernameLogin() throws InterruptedException {
        Thread.sleep(1000); // in case Toast message is still there from previous test
        onView(withId(R.id.username)).perform(clearText());
        onView(withId(R.id.password)).perform(typeText("password123"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        onView(withText("Enter username")).inRoot(withDecorView(Matchers.not(decorView))).check(matches(isDisplayed()));
    }

    @Test
    public void noPasswordLogin() {
        onView(withId(R.id.username)).perform(typeText("testuser@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        onView(withText("Enter password")).inRoot(withDecorView(Matchers.not(decorView))).check(matches(isDisplayed()));
    }


}