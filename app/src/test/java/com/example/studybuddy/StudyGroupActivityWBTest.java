package com.example.studybuddy;

import static com.example.studybuddy.StudyGroupActivity.isValidSession;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.Test;

public class StudyGroupActivityWBTest {

    @Test
    public void isValidSessionNormalInput(){
        String name = "Valid Session";
        String location = "Valid Location";
        String date = "Date: 30/11/2024";
        String startTime = "Start Time: 15:00";
        String endTime = "End Time: 17:00";

        boolean expected = true;
        boolean actual = isValidSession(name, location, date, startTime, endTime);
        assertEquals(expected, actual);
    }

    @Test
    public void isValidSessionInvalidName(){
        String name = "";
        String location = "Valid Location";
        String date = "Date: 30/11/2024";
        String startTime = "Start Time: 15:00";
        String endTime = "End Time: 17:00";

        boolean expected = false;
        boolean actual = isValidSession(name, location, date, startTime, endTime);
        assertEquals(expected, actual);
    }

    @Test
    public void isValidSessionInvalidLocation(){
        String name = "Valid Name";
        String location = "";
        String date = "Date: 30/11/2024";
        String startTime = "Start Time: 15:00";
        String endTime = "End Time: 17:00";

        boolean expected = false;
        boolean actual = isValidSession(name, location, date, startTime, endTime);
        assertEquals(expected, actual);
    }

    @Test
    public void isValidSessionInvalidTime(){
        String name = "Valid Name";
        String location = "Valid Location";
        String date = "Date: 30/11/2024";
        String startTime = "Start Time: 18:00";
        String endTime = "End Time: 17:00";

        boolean expected = false;
        boolean actual = isValidSession(name, location, date, startTime, endTime);
        assertEquals(expected, actual);
    }

    @Test
    public void isValidSessionInvalidTime2(){
        String name = "Valid Name";
        String location = "Valid Location";
        String date = "Date: 30/11/2024";
        String startTime = "Start Time: 8:00";
        String endTime = "End Time: 7:00";

        boolean expected = false;
        boolean actual = isValidSession(name, location, date, startTime, endTime);
        assertEquals(expected, actual);
    }

    @Test
    public void isValidSessionInvalidTime3(){
        String name = "Valid Name";
        String location = "Valid Location";
        String date = "Date: 30/11/2024";
        String startTime = "Start Time: 8:55";
        String endTime = "End Time: 8:50";

        boolean expected = false;
        boolean actual = isValidSession(name, location, date, startTime, endTime);
        assertEquals(expected, actual);
    }

    @Test
    public void isValidSessionInvalidTime4(){
        String name = "Valid Name";
        String location = "Valid Location";
        String date = "Date: 30/11/2024";
        String startTime = "Start Time: 22:01";
        String endTime = "End Time: 22:00";

        boolean expected = false;
        boolean actual = isValidSession(name, location, date, startTime, endTime);
        assertEquals(expected, actual);
    }

    @Test
    public void isValidSessionValidTime(){
        String name = "Valid Name";
        String location = "Valid Location";
        String date = "Date: 30/11/2024";
        String startTime = "Start Time: 8:00";
        String endTime = "End Time: 8:01";

        boolean expected = true;
        boolean actual = isValidSession(name, location, date, startTime, endTime);
        assertEquals(expected, actual);
    }

    @Test
    public void isValidSessionValidTime2(){
        String name = "Valid Name";
        String location = "Valid Location";
        String date = "Date: 30/11/2024";
        String startTime = "Start Time: 18:50";
        String endTime = "End Time: 18:51";

        boolean expected = true;
        boolean actual = isValidSession(name, location, date, startTime, endTime);
        assertEquals(expected, actual);
    }
}