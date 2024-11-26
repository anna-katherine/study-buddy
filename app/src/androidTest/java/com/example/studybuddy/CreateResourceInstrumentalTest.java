package com.example.studybuddy;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
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
public class CreateResourceInstrumentalTest
{
    @Rule
    public ActivityScenarioRule<DashboardActivity> activityRule =
            new ActivityScenarioRule<>(DashboardActivity.class);


    @Test
    public void createStudyGroup() throws InterruptedException {
        onView(withId(R.id.createGroupButton)).perform(click());
        onView(withId(R.id.input_field)).perform(typeText("TestStudyGroup"), closeSoftKeyboard());
        onView(withId(android.R.id.button1)).perform(click()); // positive button in the dialog is android.R.id.button1
        onView(withId(R.id.input_field)).check(doesNotExist());
        onView(withId(R.id.groupList)).perform(scrollTo()).check(matches(hasDescendant(withText("Test Study Group"))));
    }
}