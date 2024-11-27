package com.example.studybuddy;

import static org.hamcrest.CoreMatchers.any;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import android.net.Uri;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.Context;

import java.util.Map;

public class StudyGroupTest {

    private FirebaseFirestore mockFirestore;
    private StudyGroupActivity studyGroupActivity;

    @Before
    public void setUp() {
        studyGroupActivity = spy(new StudyGroupActivity());
        mockFirestore = mock(FirebaseFirestore.class);
        studyGroupActivity.db = mockFirestore;
    }
    

    @Test
    public void testCreateSessionDialog() {

        Context context = ApplicationProvider.getApplicationContext();
        doReturn(context).when(studyGroupActivity).getApplicationContext();

        // doNothing().when(studyGroupActivity).styleDateOrTimePicker(any(TextView.class));

        studyGroupActivity.createSessionDialog(0);

        verify(mockFirestore.collection("sessions")).add(any(Map.class));
    }

}