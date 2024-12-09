package com.example.studybuddy;

import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {
    private static final String TAG = "CalendarActivity";
    private FirebaseFirestore db;
    private String groupName;
    private LinearLayout sessionListContainer;
    private CalendarView calendarView;
    private HashMap<String, ArrayList<SessionInfo>> sessionsByDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.calendar);

            Log.d(TAG, "Starting onCreate");

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            // Initialize Firebase
            db = FirebaseFirestore.getInstance();
            sessionsByDate = new HashMap<>();

            // Get group name from intent
            groupName = getIntent().getStringExtra("com.example.studybuddy.GROUPNAME");
            if (groupName == null) {
                Log.e(TAG, "Group name is null");
                Toast.makeText(this, "Error: Group name not found", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            Log.d(TAG, "Group name: " + groupName);

            // Initialize views
            try {
                calendarView = findViewById(R.id.calendarView);
                sessionListContainer = findViewById(R.id.sessionListContainer);

                if (calendarView == null) {
                    Log.e(TAG, "CalendarView not found");
                    throw new NullPointerException("CalendarView not found in layout");
                }

                if (sessionListContainer == null) {
                    Log.e(TAG, "SessionListContainer not found");
                    throw new NullPointerException("SessionListContainer not found in layout");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error finding views: " + e.getMessage());
                Toast.makeText(this, "Error initializing views", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Set up calendar date change listener
            calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
                String selectedDate = String.format(Locale.getDefault(), "Date: %d/%d/%d", dayOfMonth, month + 1, year);
                Log.d(TAG, "Selected date: " + selectedDate);
                displaySessionsForDate(selectedDate);
            });

            // Load all sessions
            loadSessions();

            Log.d(TAG, "onCreate completed successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            Toast.makeText(this, "Error initializing calendar", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadSessions() {
        Log.d(TAG, "Starting loadSessions for group: " + groupName);

        db.collection("groups").document(groupName).get()
                .addOnSuccessListener(document -> {
                    if (!document.exists()) {
                        Log.e(TAG, "Group document does not exist");
                        return;
                    }

                    ArrayList<String> sessionIds = (ArrayList<String>) document.get("sessionList");
                    if (sessionIds != null) {
                        Log.d(TAG, "Found " + sessionIds.size() + " sessions");
                        for (String sessionId : sessionIds) {
                            loadSessionDetails(sessionId);
                        }
                    } else {
                        Log.d(TAG, "No sessions found");
                        displaySessionsForDate(getCurrentDate());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading sessions: " + e.getMessage());
                    Toast.makeText(this, "Failed to load sessions", Toast.LENGTH_SHORT).show();
                });
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
        return "Date: " + sdf.format(new Date());
    }

    private void loadSessionDetails(String sessionId) {
        Log.d(TAG, "Loading details for session: " + sessionId);

        db.collection("sessions").document(sessionId).get()
                .addOnSuccessListener(document -> {
                    if (!document.exists()) {
                        Log.e(TAG, "Session document does not exist: " + sessionId);
                        return;
                    }

                    String sessionName = document.getString("sessionName");
                    String date = document.getString("date");
                    String startTime = document.getString("startTime");
                    String endTime = document.getString("endTime");
                    String location = document.getString("location");

                    Log.d(TAG, "Session loaded - Name: " + sessionName + ", Date: " + date);

                    SessionInfo sessionInfo = new SessionInfo(sessionId, sessionName, startTime, endTime, location);

                    if (!sessionsByDate.containsKey(date)) {
                        sessionsByDate.put(date, new ArrayList<>());
                    }
                    sessionsByDate.get(date).add(sessionInfo);

                    String today = getCurrentDate();
                    if (date.equals(today)) {
                        displaySessionsForDate(date);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading session details: " + e.getMessage());
                });
    }

    private void displaySessionsForDate(String date) {
        Log.d(TAG, "Displaying sessions for date: " + date);

        try {
            sessionListContainer.removeAllViews();
            ArrayList<SessionInfo> sessions = sessionsByDate.get(date);
            if (sessions != null && !sessions.isEmpty()) {
                Log.d(TAG, "Found " + sessions.size() + " sessions for date");
                for (SessionInfo session : sessions) {
                    TextView sessionView = new TextView(this);
                    String sessionText = String.format("%s\n%s - %s\nLocation: %s",
                            session.name, session.startTime, session.endTime, session.location);
                    sessionView.setText(sessionText);
                    sessionView.setPadding(20, 20, 20, 20);
                    sessionListContainer.addView(sessionView);
                }
            } else {
                Log.d(TAG, "No sessions found for date");
                TextView noSessionsView = new TextView(this);
                noSessionsView.setText("No sessions scheduled for this date");
                noSessionsView.setPadding(20, 20, 20, 20);
                sessionListContainer.addView(noSessionsView);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error displaying sessions: " + e.getMessage());
        }
    }

    private static class SessionInfo {
        String id;
        String name;
        String startTime;
        String endTime;
        String location;

        SessionInfo(String id, String name, String startTime, String endTime, String location) {
            this.id = id;
            this.name = name;
            this.startTime = startTime;
            this.endTime = endTime;
            this.location = location;
        }
    }
}