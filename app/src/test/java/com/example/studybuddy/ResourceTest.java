package com.example.studybuddy;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import android.net.Uri;

import org.junit.Test;

public class ResourceTest
{
    @Test
    public void testGetTitle()
    {
        Uri mockUri = mock(Uri.class);
        Resource resource = new Resource("Sample Title", mockUri);
        assertEquals("Sample Title", resource.getTitle());

        Uri mockUri2 = mock(Uri.class);
        Resource resource2 = new Resource(" ", mockUri2);
        assertEquals(" ", resource2.getTitle());

        Uri mockUri3 = mock(Uri.class);
        Resource resource3 = new Resource("Testing with Special Characters!@#$%^&*(", mockUri3);
        assertEquals("Testing with Special Characters!@#$%^&*(", resource3.getTitle());
    }

}