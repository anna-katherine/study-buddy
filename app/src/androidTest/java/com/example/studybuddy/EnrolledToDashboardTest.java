package com.example.studybuddy;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.action.ViewActions;

import org.junit.Before;
import org.junit.Test;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

public class EnrolledToDashboardTest {

    @Before
    public void setUp() {
        Intents.init();
    }

    @Test
    public void EnrolledToDashboardActivity() {
        ActivityScenario.launch(EnrolledClassesActivity.class);

        Espresso.onView(ViewMatchers.withId(R.id.bottomNavigationView))
                .perform(ViewActions.click());

        Espresso.onView(ViewMatchers.withId(R.id.dashboard))
                .perform(ViewActions.click());

        intended(hasComponent(DashboardActivity.class.getName()));

        Intents.release();
    }
}