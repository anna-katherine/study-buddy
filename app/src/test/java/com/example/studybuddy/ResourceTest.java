package com.example.studybuddy;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import android.net.Uri;

import org.junit.Test;

public class ResourceTest {

    @Test
    public void testGetTitle() {
        Uri mockUri = mock(Uri.class);
        Resource resource = new Resource("Sample Title", mockUri);
        assertEquals("Sample Title", resource.getTitle());
    }

}