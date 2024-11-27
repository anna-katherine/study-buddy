package com.example.studybuddy;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import android.net.Uri;

import org.junit.Test;

public class StudyGroupTest {

    @Test
    public void testDisplayGroupName() {
        // Simulate the intent that would start this activity
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), StudyGroupActivity.class);
        intent.putExtra("com.example.studybuddy.GROUPNAME", "Test Group");
        activityRule.getScenario().onActivity(activity -> activity.startActivity(intent));

        // Check if the group name is displayed correctly
        onView(withId(R.id.group_name)).check(matches(withText("Test Group")));
    }

}