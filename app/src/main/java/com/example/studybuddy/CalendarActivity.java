package com.example.studybuddy;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CalendarActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CalendarView calendarView;
    private ListView sessionListView;
    private ArrayList<String> sessionList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.calendar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        calendarView = findViewById(R.id.calendarView);
        sessionListView = findViewById(R.id.sessionList);

        // Initialize adapter for ListView
        adapter = new ArrayAdapter<>(CalendarActivity.this, android.R.layout.simple_list_item_1, sessionList);
        sessionListView.setAdapter(adapter);

        // Set up calendar date change listener
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = formatDate(year, month, dayOfMonth);
            fetchSessionsForDate(selectedDate);
        });
    }

    private String formatDate(int year, int month, int dayOfMonth) {
        month = month + 1; // Calendar months are 0-indexed
        return String.format("%02d/%02d/%04d", dayOfMonth, month, year);
    }

    // Fetch sessions for the selected date from Firestore
    private void fetchSessionsForDate(String selectedDate) {
        sessionList.clear(); // Clear the previous session list

        db.collection("sessions")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Loop through the sessions and add them to the session list
                            for (DocumentSnapshot document : querySnapshot) {
                                String sessionName = document.getString("sessionName");
                                String startTime = document.getString("startTime");
                                String endTime = document.getString("endTime");
                                String location = document.getString("location");
                                String sessionDate = document.getString("date");

                                // Remove the "date :" part of the session date
                                if (sessionDate != null) {
                                    sessionDate = sessionDate.substring(6);
                                }

                                // Compare the session date with the selected date
                                if (sessionDate.equals(selectedDate)) {
                                    // Format session details
                                    String sessionDetails = "\n" + "Name: " + sessionName + "\n" +
                                             startTime + "\n" +
                                             endTime + "\n" +
                                            "Location: " + location + "\n";
                                    sessionList.add(sessionDetails);
                                }
                            }

                            // Notify the adapter that the data has changed
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(CalendarActivity.this, "No sessions found for this date.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CalendarActivity.this, "Error retrieving sessions.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}