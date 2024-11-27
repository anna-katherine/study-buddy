package com.example.studybuddy;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class StudySession {
    String date;
    String time;
    List<FirebaseUser> attendees;
    int id;
    FirebaseUser currUser;

    StudySession(String date, String time, int id){
        this.date = date;
        this.time = time;
        this.id = id;
        attendees = new ArrayList<FirebaseUser>();
        attendees.add(FirebaseAuth.getInstance().getCurrentUser());
    }

    StudySession(String date, String time)
    {
        this.date = date;
        this.time = time;
        id = 0;
    }


    public String getDate(){
        return date;
    }

    public String getTime() {
        return time;
    }

    public List<FirebaseUser> getAttendees() {
        return attendees;
    }

    public int getId() {
        return id;
    }

    public void leaveSession(FirebaseUser user){
        attendees.remove(user);
        if (attendees.isEmpty()){
            //Remove this study session from database.
        }
    }
}
