package com.example.studybuddy;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

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

import java.util.List;
import java.util.concurrent.CountDownLatch;

@RunWith(AndroidJUnit4.class)
public class StudySessionDataTest {

    // written by Anna

    private StudySession studySession;
    private StudySession studySessionWithoutId;
    private FirebaseUser mockUser1;
    private FirebaseUser mockUser2;

    @Before
    public void setUp() {
        mockUser1 = mock(FirebaseUser.class);
        mockUser2 = mock(FirebaseUser.class);

        studySession = new StudySession("2024-11-30", "10:00 AM", 123);
        studySession.attendees.add(mockUser1);
        studySession.attendees.add(mockUser2);
        studySessionWithoutId = new StudySession("2024-12-01", "3:00 PM");
    }

    @Test
    public void testGetDate() {
        assertEquals("2024-11-30", studySession.getDate());
        assertEquals("2024-12-01", studySessionWithoutId.getDate());
    }

    @Test
    public void testGetTime() {
        assertEquals("10:00 AM", studySession.getTime());
        assertEquals("3:00 PM", studySessionWithoutId.getTime());
    }

    @Test
    public void testGetId() {
        assertEquals(123, studySession.getId());
        assertEquals(0, studySessionWithoutId.getId());
    }

    @Test
    public void testGetAttendees() {
        List<FirebaseUser> attendees = studySession.getAttendees();
        assertEquals(2, attendees.size()-1);
        assertTrue(attendees.contains(mockUser1));
    }
}

