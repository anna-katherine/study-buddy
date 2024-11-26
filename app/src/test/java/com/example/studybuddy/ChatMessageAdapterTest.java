package com.example.studybuddy;

import static com.example.studybuddy.ChatMessageAdapter.getDateFromTimestamp;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.firebase.Timestamp;

import org.junit.jupiter.api.Test;

public class ChatMessageAdapterTest {

    //Created by Alex F
    @org.junit.Test
    public void getDateFromTimestampIsCorrect() {
        String expected;
        String actual;

        //December 25th, 2024, 11:00AM GMT-08:00
        Timestamp testStamp2 = new Timestamp(1735153200, 0);
        expected = "Dec 25, 11:00AM";
        actual = getDateFromTimestamp(testStamp2);
        assertEquals(expected, actual);

        //December 25th, 2024, 12:00pm GMT-08:00
        Timestamp testStamp = new Timestamp(1735156860, 0);
        expected = "Dec 25, 12:01PM";
        actual = getDateFromTimestamp(testStamp);
        assertEquals(expected, actual);

        //December 25th, 2024, 12:00pm GMT-08:00
        Timestamp testStamp4 = new Timestamp(1735156800, 0);
        expected = "Dec 25, 12:00PM";
        actual = getDateFromTimestamp(testStamp4);
        assertEquals(expected, actual);

        //January 1st, 2000, 6:59:37.00AM GMT-08:00
        Timestamp testStamp3 = new Timestamp(946738777, 0);
        expected = "Jan 1, 6:59AM";
        actual = getDateFromTimestamp(testStamp3);
        assertEquals(expected, actual);

        //August 28, 1998, 8:28:38.00PM GMT-08:00
        Timestamp testStamp5 = new Timestamp(904361318, 0);
        expected = "Aug 28, 8:28PM";
        actual = getDateFromTimestamp(testStamp5);
        assertEquals(expected, actual);
    }
}