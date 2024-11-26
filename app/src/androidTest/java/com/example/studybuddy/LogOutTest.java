package com.example.studybuddy;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class LogOutTest {

    private FirebaseAuth firebaseAuth;

    @Before
    public void setUp() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Test
    public void testLogoutRedirectsToLoginActivity() throws InterruptedException {
        firebaseAuth.signInWithEmailAndPassword("gsianipa@usc.edu", "password")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            assertNotNull("User should be logged in", user);
                            System.out.println("Logged in as: " + user.getEmail());

                            // Proceed with the rest of the test here, inside the successful sign-in callback
                            runLogoutTest();

                        } else {
                            System.out.println("User is null, sign-in failed");
                        }
                    } else {
                        System.out.println("Sign-in failed: " + task.getException().getMessage());
                    }
                });

        // Wait for the sign-in process to complete (using a latch or other async mechanism is preferred)
        Thread.sleep(5000); // Or consider using a more robust async mechanism like a CountDownLatch
    }

    private void runLogoutTest() {

        Intents.init();

        ActivityScenario.launch(EnrolledClassesActivity.class);

        Espresso.onView(ViewMatchers.withId(R.id.bottomNavigationView))
                .perform(ViewActions.click());

        Espresso.onView(ViewMatchers.withId(R.id.log_out))
                .perform(ViewActions.click());

        intended(hasComponent(LoginActivity.class.getName()));

        Intents.release();
    }
}
