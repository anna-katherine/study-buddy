package com.example.studybuddy;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.Mockito.mock;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RegisterPasswordTest {
    private FirebaseAuth mockAuth;

    @Before
    public void setUp() {
        // Initialize mock FirebaseAuth before tests
        mockAuth = mock(FirebaseAuth.class);
    }

    @Test
    public void testPasswordTooShort() {
        ActivityScenario<RegisterActivity> scenario = ActivityScenario.launch(RegisterActivity.class);

        String testEmail = "shortuser@example.com";
        String shortPassword = "short"; // Password too short
        String firstName = "John";
        String lastName = "Doe";

        Espresso.onView(withId(R.id.username))
                .perform(typeText(testEmail), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.password))
                .perform(typeText(shortPassword), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.firstname))
                .perform(typeText(firstName), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.lastname))
                .perform(typeText(lastName), ViewActions.closeSoftKeyboard());

        Espresso.onView(withId(R.id.register_button)).perform(click());


        Espresso.onIdle();


        Espresso.onView(withId(R.id.password))
                .check(matches(hasErrorText("Password must be at least 8 characters.")));

        scenario.close();
    }
}