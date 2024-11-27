package com.example.studybuddy;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static org.junit.Assert.assertNotNull;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DashboardToEnrolledTest {

    private FirebaseAuth firebaseAuth;

    @Before
    public void setUp() throws InterruptedException {
        firebaseAuth = FirebaseAuth.getInstance();


        firebaseAuth.signInWithEmailAndPassword("gsianipa@usc.edu", "password")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        assertNotNull("User should be logged in", user);
                        System.out.println("Logged in as: " + user.getEmail());
                    } else {
                        System.out.println("Sign-in failed: " + task.getException().getMessage());
                    }
                });
        Thread.sleep(3000);

        Intents.init();
    }

    @Test
    public void testNavigationToEnrolledClasses() {
        // Launch the DashboardActivity after login
        ActivityScenario.launch(DashboardActivity.class);

        // Simulate clicking on the "courses" item in BottomNavigationView
        Espresso.onView(ViewMatchers.withId(R.id.bottomNavigationView))
                .perform(ViewActions.click());

        // Verify that the EnrolledClassesActivity is opened
        intended(hasComponent(EnrolledClassesActivity.class.getName()));

        // Clean up Intents
        Intents.release();
    }
}