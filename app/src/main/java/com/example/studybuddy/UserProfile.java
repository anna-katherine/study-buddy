package com.example.studybuddy;

import java.util.List;

public class UserProfile {
    String userName;
    List<String> courses;
    Calendar calendar;

    UserProfile(String userName, List<String> courses, Calendar calendar){
        this.userName = userName;
        this.courses = courses;
        this.calendar = calendar;
    }

    public void addCourse(String course){
        courses.add(course);
    }

    public void removeCourse(String course){
        courses.remove(course);
    }

    public Calendar getCalendar(){
        return calendar;
    }

    //FIXME: Implement
    public void joinStudyGroup(){
    }
}
