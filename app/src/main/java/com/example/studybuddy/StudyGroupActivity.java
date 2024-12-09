package com.example.studybuddy;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class StudyGroupActivity extends AppCompatActivity {
    private String groupName;
    private LinearLayout sessionListContainer;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private AlertDialog currentDialog;
    private ListView memberListView;
    private ArrayAdapter<String> memberAdapter;
    private ArrayList<String> memberNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study_group);

        // Initialize Firebase
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        groupName = getIntent().getStringExtra("com.example.studybuddy.GROUPNAME");
        TextView groupNameTextView = findViewById(R.id.group_name);
        groupNameTextView.setText(groupName);

        memberListView = findViewById(R.id.memberList);
        memberNames = new ArrayList<>();
        memberAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, memberNames);
        memberListView.setAdapter(memberAdapter);

        sessionListContainer = findViewById(R.id.sessionListContainer);

        setupBottomNavigation();

        loadGroupMembers();

        loadExistingSessions();

        Button resourceButton = findViewById(R.id.resources);
        resourceButton.setOnClickListener(v -> {
            Intent intent = new Intent(StudyGroupActivity.this, ResourceActivity.class);
            startActivity(intent);
        });
    }

    private void loadGroupMembers() {
        db.collection("groups").document(groupName).get()
                .addOnSuccessListener(document -> {
                    ArrayList<String> memberIds = (ArrayList<String>) document.get("memberList");
                    if (memberIds != null) {
                        for (String memberId : memberIds) {
                            // Fetch each member's display name
                            db.collection("users").document(memberId).get()
                                    .addOnSuccessListener(userDoc -> {
                                        String displayName = userDoc.getString("displayName");
                                        if (displayName != null) {
                                            memberNames.add(displayName);
                                            memberAdapter.notifyDataSetChanged();
                                        }
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(StudyGroupActivity.this,
                                            "Failed to load member information", Toast.LENGTH_SHORT).show());
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this,
                        "Failed to load group members", Toast.LENGTH_SHORT).show());
    }


    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.chat) {
                    Intent intent = new Intent(StudyGroupActivity.this, GroupChatActivity.class);
                    intent.putExtra("com.example.studybuddy.GROUPNAME", groupName);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.calendar) {
                    Intent intent = new Intent(StudyGroupActivity.this, CalendarActivity.class);
                    intent.putExtra("com.example.studybuddy.GROUPNAME", groupName);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.create) {
                    showCreateSessionDialog();
                    return true;
                }
                return false;
            }
        });
    }

    private void loadExistingSessions() {
        db.collection("groups").document(groupName).get()
                .addOnSuccessListener(document -> {
                    ArrayList<String> sessionIds = (ArrayList<String>) document.get("sessionList");
                    if (sessionIds != null) {
                        for (String sessionId : sessionIds) {
                            loadSessionButton(sessionId);
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load sessions", Toast.LENGTH_SHORT).show());
    }

    private void loadSessionButton(String sessionId) {
        db.collection("sessions").document(sessionId).get()
                .addOnSuccessListener(document -> {
                    String sessionName = document.getString("sessionName");
                    String date = document.getString("date");
                    String startTime = document.getString("startTime");
                    String endTime = document.getString("endTime");
                    String location = document.getString("location");

                    createSessionButton(sessionId, sessionName, date, startTime, endTime, location);
                });
    }

    private void createSessionButton(String sessionId, String name, String date,
                                     String startTime, String endTime, String location) {
        Button sessionButton = new Button(this);
        String sessionText = String.format("%s\n%s\n%s\n%s\n%s",
                name, date, startTime, endTime, location);
        sessionButton.setText(sessionText);
        sessionButton.setAllCaps(false);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 16);
        sessionButton.setLayoutParams(params);

        sessionButton.setOnClickListener(v -> showSessionDetailsDialog(sessionId, sessionText));
        sessionButton.setOnLongClickListener(v -> {
            showDeleteSessionDialog(sessionId);
            return true;
        });

        sessionListContainer.addView(sessionButton);
    }

    private void showSessionDetailsDialog(String sessionId, String sessionDetails) {
        db.collection("sessions").document(sessionId).get()
                .addOnSuccessListener(document -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Session Details");

                    LinearLayout layout = new LinearLayout(this);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    layout.setPadding(50, 50, 50, 50);

                    // Session details
                    TextView detailsView = new TextView(this);
                    detailsView.setText(sessionDetails);
                    layout.addView(detailsView);

                    // Members section
                    TextView membersTitle = new TextView(this);
                    membersTitle.setText("\nMembers:");
                    membersTitle.setTextSize(16);
                    layout.addView(membersTitle);

                    // Layout for members list
                    LinearLayout membersLayout = new LinearLayout(this);
                    membersLayout.setOrientation(LinearLayout.VERTICAL);
                    membersLayout.setPadding(0, 10, 0, 10);
                    layout.addView(membersLayout);

                    // Get members list and display them
                    ArrayList<String> membersList = (ArrayList<String>) document.get("membersList");
                    if (membersList != null) {
                        fetchAndDisplayMembers(membersLayout, membersList);
                    }

                    // Add join/leave button after a slight delay to ensure members are displayed
                    layout.postDelayed(() -> {
                        Button actionButton = new Button(this);
                        if (membersList != null && membersList.contains(currentUser.getUid())) {
                            actionButton.setText("Leave Session");
                            actionButton.setOnClickListener(v -> leaveSession(sessionId));
                        } else {
                            actionButton.setText("Join Session");
                            actionButton.setOnClickListener(v -> joinSession(sessionId));
                        }
                        layout.addView(actionButton);
                    }, 500); // Small delay to ensure members are loaded

                    builder.setView(layout);
                    builder.setPositiveButton("Close", null);

                    currentDialog = builder.create();
                    currentDialog.show();
                });
    }

    private void fetchAndDisplayMembers(LinearLayout layout, ArrayList<String> memberIds) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        for (String memberId : memberIds) {
            db.collection("users").document(memberId).get()
                    .addOnSuccessListener(userDoc -> {
                        String userName = userDoc.getString("displayName");
                        if (userName != null) {
                            TextView memberView = new TextView(this);
                            memberView.setText("• " + userName);
                            memberView.setPadding(20, 5, 20, 5);
                            layout.addView(memberView);
                        }
                    });
        }
    }

    private void joinSession(String sessionId) {
        db.collection("sessions").document(sessionId)
                .update("membersList", FieldValue.arrayUnion(currentUser.getUid()))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Joined session successfully", Toast.LENGTH_SHORT).show();
                    if (currentDialog != null) {
                        currentDialog.dismiss();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this,
                        "Failed to join session", Toast.LENGTH_SHORT).show());
    }

    private void leaveSession(String sessionId) {
        db.collection("sessions").document(sessionId)
                .update("membersList", FieldValue.arrayRemove(currentUser.getUid()))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Left session successfully", Toast.LENGTH_SHORT).show();
                    if (currentDialog != null) {
                        currentDialog.dismiss();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this,
                        "Failed to leave session", Toast.LENGTH_SHORT).show());
    }

    private void showCreateSessionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create New Study Session");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 40, 40, 40);

        EditText nameInput = new EditText(this);
        nameInput.setHint("Session Name");

        EditText locationInput = new EditText(this);
        locationInput.setHint("Location");

        TextView dateText = new TextView(this);
        dateText.setText("Select Date");
        TextView startTimeText = new TextView(this);
        startTimeText.setText("Select Start Time");
        TextView endTimeText = new TextView(this);
        endTimeText.setText("Select End Time");

        final Calendar calendar = Calendar.getInstance();

        dateText.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, day) -> {
                dateText.setText(String.format("Date: %d/%d/%d", day, month + 1, year));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        startTimeText.setOnClickListener(v -> {
            new TimePickerDialog(this, (view, hour, minute) -> {
                startTimeText.setText(String.format("Start Time: %02d:%02d", hour, minute));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        });

        endTimeText.setOnClickListener(v -> {
            new TimePickerDialog(this, (view, hour, minute) -> {
                endTimeText.setText(String.format("End Time: %02d:%02d", hour, minute));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        });

        layout.addView(nameInput);
        layout.addView(locationInput);
        layout.addView(dateText);
        layout.addView(startTimeText);
        layout.addView(endTimeText);

        builder.setView(layout);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String name = nameInput.getText().toString();
            String location = locationInput.getText().toString();
            String date = dateText.getText().toString();
            String startTime = startTimeText.getText().toString();
            String endTime = endTimeText.getText().toString();

            if (isValidSession(name, location, date, startTime, endTime)) {
                saveNewSession(name, location, date, startTime, endTime);
            } else {
                Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }


    public static boolean isValidSession(String name, String location, String date, String startTime, String endTime){
        if (name.isEmpty() || location.isEmpty() || date.isEmpty() || startTime.isEmpty() || endTime.isEmpty()){
            return false;
        }
        else if (name.length() > 100){
            return false;
        }
        int i = startTime.length() - 5;
        int j = endTime.length() - 5;
        char s = startTime.charAt(i);
        char e = endTime.charAt(j);
        if ((e == ' ') && (s != ' ')){
            return false;
        }
        else if (s != ' ' && e != ' '){
            if (e < s){
                return false;
            }
            else if (e == s){
                s = startTime.charAt(i + 1);
                e = endTime.charAt(j + 1);
                if (e < s){
                    return false;
                }
                else if (e == s){
                    s = startTime.charAt(i + 3);
                    e = endTime.charAt(j + 3);
                    if (e < s){
                        return false;
                    }
                    else if (e == s){
                        s = startTime.charAt(i + 4);
                        e = endTime.charAt(j + 4);
                        if (e <= s){
                            return false;
                        }
                    }
                }
            }
        }
        else if ((e == ' ') && (s == ' ')){
            s = startTime.charAt(i + 1);
            e = endTime.charAt(j + 1);
            if (e < s){
                return false;
            }
            else if (e == s){
                s = startTime.charAt(i + 3);
                e = endTime.charAt(j + 3);
                if (e < s){
                    return false;
                }
                else if (e == s){
                    s = startTime.charAt(i + 4);
                    e = endTime.charAt(j + 4);
                    if (e <= s){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void saveNewSession(String name, String location, String date,
                                String startTime, String endTime) {
        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("sessionName", name);
        sessionData.put("location", location);
        sessionData.put("date", date);
        sessionData.put("startTime", startTime);
        sessionData.put("endTime", endTime);
        sessionData.put("groupName", groupName);

        ArrayList<String> membersList = new ArrayList<>();
        membersList.add(currentUser.getUid());
        sessionData.put("membersList", membersList);

        db.collection("sessions").add(sessionData)
                .addOnSuccessListener(documentReference -> {
                    String sessionId = documentReference.getId();

                    // Add session to group's session list
                    db.collection("groups").document(groupName)
                            .update("sessionList", FieldValue.arrayUnion(sessionId))
                            .addOnSuccessListener(aVoid -> {
                                createSessionButton(sessionId, name, date, startTime, endTime, location);
                                Toast.makeText(this, "Session created successfully!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> Toast.makeText(this,
                                    "Failed to add session to group", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(this,
                        "Failed to create session", Toast.LENGTH_SHORT).show());
    }

    private void showDeleteSessionDialog(String sessionId) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Session")
                .setMessage("Are you sure you want to delete this session?")
                .setPositiveButton("Yes", (dialog, which) -> deleteSession(sessionId))
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteSession(String sessionId) {
        db.collection("sessions").document(sessionId).delete()
                .addOnSuccessListener(aVoid -> {
                    // Remove session from group's session list
                    db.collection("groups").document(groupName)
                            .update("sessionList", FieldValue.arrayRemove(sessionId))
                            .addOnSuccessListener(unused -> {
                                // Refresh the session list
                                sessionListContainer.removeAllViews();
                                loadExistingSessions();
                                Toast.makeText(this, "Session deleted successfully", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> Toast.makeText(this,
                        "Failed to delete session", Toast.LENGTH_SHORT).show());
    }
}
