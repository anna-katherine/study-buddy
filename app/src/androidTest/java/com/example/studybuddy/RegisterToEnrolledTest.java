package com.example.studybuddy;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;

import org.junit.Test;

public class RegisterToEnrolledTest {
    @Test
    public void testSelectedCoursesArePassedToNextActivity() {
        ActivityScenario<RegisterActivity> scenario2 = ActivityScenario.launch(RegisterActivity.class);

        Espresso.onView(withId(R.id.courseDropdown)).perform(click());
        Espresso.onView(withText("Course 1: Math")).perform(click());
        Espresso.onView(withText("Course 2: English")).perform(click());
        Espresso.onView(withText("OK")).perform(click());

        Espresso.onView(withId(R.id.username)).perform(ViewActions.typeText("newtestuser@example.com"));
        Espresso.onView(withId(R.id.password)).perform(ViewActions.typeText("Password123"));
        Espresso.onView(withId(R.id.firstname)).perform(ViewActions.typeText("John"));
        Espresso.onView(withId(R.id.lastname)).perform(ViewActions.typeText("Doe"));


        Espresso.onView(withId(R.id.register_button)).perform(ViewActions.click());

        Intents.init();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        intended(allOf(
                hasComponent(EnrolledClassesActivity.class.getName()),
                hasExtra(equalTo("com.example.studybuddy.COURSES"),
                        hasItems("Course 1: Math", "Course 2: English")) // Extra check for courses
        ));

        Intents.release();
    }
}
