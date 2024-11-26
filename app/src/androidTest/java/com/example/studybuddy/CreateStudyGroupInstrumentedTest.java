package com.example.studybuddy;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.util.Log;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;


@RunWith(AndroidJUnit4.class)
public class CreateStudyGroupInstrumentedTest
{
    @Rule
    public ActivityScenarioRule<DashboardActivity> activityRule =
            new ActivityScenarioRule<>(DashboardActivity.class);


    @Before
    public void signInUser() throws InterruptedException {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        CountDownLatch latch = new CountDownLatch(1);

        auth.signInWithEmailAndPassword("tiffli@gmail.com", "tifftest")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Test", "User signed in successfully");
                    } else {
                        Log.e("Test", "Failed to sign in user", task.getException());
                    }
                    latch.countDown(); // Decrement the latch whether sign-in is successful or not
                });

        latch.await();
    }

    @Test
    public void createStudyGroup() throws InterruptedException {
        onView(withId(R.id.createGroupButton)).perform(click());
        onView(withId(R.id.input_field)).perform(typeText("TestStudyGroup"), closeSoftKeyboard());
        onView(withId(android.R.id.button1)).perform(click()); // positive button in the dialog is android.R.id.button1
        onView(withId(R.id.input_field)).check(doesNotExist());
        onView(withId(R.id.groupList)).perform(scrollTo()).check(matches(hasDescendant(withText("Test Study Group"))));
    }
}