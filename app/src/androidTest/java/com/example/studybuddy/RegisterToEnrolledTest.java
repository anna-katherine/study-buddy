package com.example.studybuddy;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.core.IsEqual.equalTo;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.action.ViewActions;

import com.example.studybuddy.EnrolledClassesActivity;

import org.junit.Test;
import org.junit.Before;

public class RegisterToEnrolledTest {



    @Test
    public void testSelectedCoursesArePassedToNextActivity() {
        // Launch RegisterActivity
        ActivityScenario<RegisterActivity> scenario2 = ActivityScenario.launch(RegisterActivity.class);

        // Selecting courses
        Espresso.onView(ViewMatchers.withId(R.id.courseDropdown)).perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withText("Course 1: Math")).perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withText("Course 2: English")).perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withText("OK")).perform(ViewActions.click());

        // Fill out registration form
        Espresso.onView(ViewMatchers.withId(R.id.username)).perform(ViewActions.typeText("randomuser@example.com"));
        Espresso.onView(ViewMatchers.withId(R.id.password)).perform(ViewActions.typeText("Password123"));
        Espresso.onView(ViewMatchers.withId(R.id.firstname)).perform(ViewActions.typeText("John"));
        Espresso.onView(ViewMatchers.withId(R.id.lastname)).perform(ViewActions.typeText("Doe"));

        Espresso.onView(ViewMatchers.withId(R.id.register_button)).perform(ViewActions.click());

        Intents.init();

        intended(allOf(
                hasComponent(EnrolledClassesActivity.class.getName()),
                hasExtra(equalTo("com.example.studybuddy.COURSES"),
                        hasItems("Course 1: Math", "Course 2: English"))
        ));

        Intents.release();
    }

}

