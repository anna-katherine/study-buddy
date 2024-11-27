package com.example.studybuddy;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.Mockito.*;
import com.google.firebase.auth.FirebaseAuth;
@RunWith(AndroidJUnit4.class)
public class RegisterEmptyAndDuplicateTest {
    private FirebaseAuth mockAuth;
    @Before
    public void setUp() {
        // Initialize mock FirebaseAuth before tests
        mockAuth = mock(FirebaseAuth.class);
    }
    @Test
    public void testEmptyUsername() {
        ActivityScenario<RegisterActivity> scenario = ActivityScenario.launch(RegisterActivity.class);
        Espresso.onView(withId(R.id.username))
                .perform(typeText(""), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.password))
                .perform(typeText("password123"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.firstname))
                .perform(typeText("John"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.lastname))
                .perform(typeText("Doe"), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.register_button)).perform(click());
        Espresso.onView(withId(R.id.username))
                .check(matches(hasErrorText("Enter username.")));
        scenario.close();
    }
    @Test
    public void testDuplicateEmailHandling() {
        ActivityScenario<RegisterActivity> scenario = ActivityScenario.launch(RegisterActivity.class);
        String testEmail = "duplicate@example.com";
        String testPassword = "validPassword";
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(testEmail, testPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Espresso.onView(withId(R.id.username))
                                .perform(typeText(testEmail), ViewActions.closeSoftKeyboard());
                        Espresso.onView(withId(R.id.password))
                                .perform(typeText(testPassword), ViewActions.closeSoftKeyboard());
                        Espresso.onView(withId(R.id.firstname))
                                .perform(typeText("John"), ViewActions.closeSoftKeyboard());
                        Espresso.onView(withId(R.id.lastname))
                                .perform(typeText("Doe"), ViewActions.closeSoftKeyboard());
                        Espresso.onView(withId(R.id.register_button)).perform(click());
                        scenario.onActivity(activity -> {
                            new android.os.Handler().postDelayed(() -> {
                                Espresso.onView(withId(R.id.username))
                                        .check(matches(hasErrorText("Email is already registered")));
                            }, 2000); // Delay to ensure Firebase completes the request
                        });
                    }
                });
        scenario.close();
    }
}