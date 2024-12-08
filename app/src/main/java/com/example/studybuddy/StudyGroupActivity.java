package com.example.studybuddy;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudyGroupActivity extends AppCompatActivity
{
    String groupName;
    ArrayList<String> items2;
    ListView lv2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.study_group);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        groupName = intent.getStringExtra("com.example.studybuddy.GROUPNAME");

        TextView tv = findViewById(R.id.group_name);
        tv.setText(groupName);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String groupId = groupName;
        DocumentReference groupRef = db.collection("groups").document(groupId);

        ListView lv = findViewById(R.id.memberList);
        ArrayList<String> items = new ArrayList<>();

        groupRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<String> memberList = (List<String>) document.get("memberList");
                    if (memberList != null && !memberList.isEmpty()) {
                        for (String memberId : memberList) {
                            DocumentReference userRef = db.collection("users").document(memberId);
                            userRef.get().addOnCompleteListener(userTask -> {
                                if (userTask.isSuccessful()) {
                                    DocumentSnapshot userDoc = userTask.getResult();
                                    if (userDoc.exists()) {
                                        String userName = userDoc.getString("displayName");
                                        if (userName != null) {
                                            items.add(userName);
                                        }

                                        if (items.size() == memberList.size()) {
                                            ArrayAdapter<String> adapter = new ArrayAdapter<>(StudyGroupActivity.this, android.R.layout.simple_list_item_1, items);
                                            lv.setAdapter(adapter);
                                        }
                                    }
                                } else {
                                    Log.w("FirebaseError", "Error fetching user data", userTask.getException());
                                }
                            });
                        }
                    }
                }
            } else {
                Log.w("FirebaseError", "Error getting group data", task.getException());
            }
        });

        lv2 = findViewById(R.id.sessionList);
        items2 = new ArrayList<>();

        lv2.setOnItemLongClickListener((parent, view, position, id) -> {
            new AlertDialog.Builder(StudyGroupActivity.this)
                    .setTitle("Delete Session")
                    .setMessage("Are you sure you want to delete this session?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        String sessionDetails = items2.get(position);
                        Log.d("Items2", "Items2 list: " + items2);
                        String sessionId = extractSessionId(sessionDetails); // Implement this method if necessary
                        removeStudySession(sessionId);
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
            return true;
        });

        groupRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<String> sessionList = (List<String>) document.get("sessionList");
                    if (sessionList != null && !sessionList.isEmpty()) {
                        for (String sessionId : sessionList) {
                            DocumentReference sessionRef = db.collection("sessions").document(sessionId);
                            sessionRef.get().addOnCompleteListener(sessionTask -> {
                                if (sessionTask.isSuccessful()) {
                                    DocumentSnapshot sessionDoc = sessionTask.getResult();
                                    if (sessionDoc.exists()) {
                                        String sessionName = sessionDoc.getString("sessionName");
                                        String startTime = sessionDoc.getString("startTime");
                                        String endTime = sessionDoc.getString("endTime");
                                        String location = sessionDoc.getString("location");
                                        String date = sessionDoc.getString("date");

                                        String sessionDetails = "\n" + "Name: " + sessionName + "\n" +
                                                date + "\n" +
                                                startTime + "\n" +
                                                endTime + "\n" +
                                                "Location: " + location + "\n";
                                        items2.add(sessionDetails);

                                        if (items2.size() == sessionList.size()) {
                                            ArrayAdapter<String> adapter2 = new ArrayAdapter<>(StudyGroupActivity.this, android.R.layout.simple_list_item_1, items2);
                                            lv2.setAdapter(adapter2);
                                        }
                                    }
                                } else {
                                    Log.w("FirebaseError", "Error fetching session data", sessionTask.getException());
                                }
                            });
                        }
                    }
                }
            } else {
                Log.w("FirebaseError", "Error getting group data", task.getException());
            }
        });

        Button resourceButton = findViewById(R.id.resources);
        resourceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudyGroupActivity.this, ResourceActivity.class);
                intent.putExtra("groupname", groupName);
                startActivity(intent);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                Intent intent;
                if (item.getItemId() == R.id.chat) {
                    intent = new Intent(StudyGroupActivity.this, GroupChatActivity.class);
                    intent.putExtra("com.example.studybuddy.GROUPNAME", groupName);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.calendar) {
                    intent = new Intent(StudyGroupActivity.this, CalendarActivity.class);
                    intent.putExtra("com.example.studybuddy.GROUPNAME", groupName);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.create) {
                    // we should pass the counter number in here so we can add it to the name
                    createSessionDialog(0);
                }
                return true;
            }
        });
    }

    private String extractSessionId(String sessionDetails) {
        return sessionDetails.split(" ")[0];
    }

    private void createSessionDialog(int number)
    {
        final EditText locationInput = new EditText(this);
        locationInput.setHint("Enter location");

        final EditText titleInput = new EditText(this);
        titleInput.setHint("Enter name");

        final TextView dateDisplay = new TextView(this);
        dateDisplay.setText("Select Date");
        styleDateOrTimePicker(dateDisplay);

        final TextView startTimeDisplay = new TextView(this);
        startTimeDisplay.setText("Select Start Time");
        styleDateOrTimePicker(startTimeDisplay);

        final TextView endTimeDisplay = new TextView(this);
        endTimeDisplay.setText("Select End Time");
        styleDateOrTimePicker(endTimeDisplay);

        final Calendar calendar = Calendar.getInstance();
        final int currentYear = calendar.get(Calendar.YEAR);
        final int currentMonth = calendar.get(Calendar.MONTH);
        final int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        final int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        final int currentMinute = calendar.get(Calendar.MINUTE);

        dateDisplay.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(StudyGroupActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        dateDisplay.setText("Date: " + dayOfMonth + "/" + (month + 1) + "/" + year);
                    }, currentYear, currentMonth, currentDay);
            datePickerDialog.show();
        });

        startTimeDisplay.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(StudyGroupActivity.this,
                    (view, hourOfDay, minute) -> {
                        startTimeDisplay.setText("Start Time: " + hourOfDay + ":" + (minute < 10 ? "0" + minute : minute));
                    }, currentHour, currentMinute, true);
            timePickerDialog.show();
        });

        endTimeDisplay.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(StudyGroupActivity.this,
                    (view, hourOfDay, minute) -> {
                        endTimeDisplay.setText("End Time: " + hourOfDay + ":" + (minute < 10 ? "0" + minute : minute));
                    }, currentHour, currentMinute, true);
            timePickerDialog.show();
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Study Session 0");

        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(40, 40, 40, 40);

        dialogLayout.addView(dateDisplay);
        dialogLayout.addView(startTimeDisplay);
        dialogLayout.addView(endTimeDisplay);
        dialogLayout.addView(titleInput);
        dialogLayout.addView(locationInput);

        builder.setView(dialogLayout);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = titleInput.getText().toString();
            String location = locationInput.getText().toString();
            String date = dateDisplay.getText().toString();
            String startTime = startTimeDisplay.getText().toString();
            String endTime = endTimeDisplay.getText().toString();

            if (!isValidSession(name, location, date, startTime, endTime)) {
                return;
            }

            Map<String, Object> sessionData = new HashMap<>();
            sessionData.put("sessionName", name);
            sessionData.put("location", location);
            sessionData.put("date", date);
            sessionData.put("startTime", startTime);
            sessionData.put("endTime", endTime);
            sessionData.put("groupName", groupName);

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("sessions").add(sessionData)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("Firebase", "Session created with ID: " + documentReference.getId());

                        DocumentReference groupRef = db.collection("groups").document(groupName);
                        groupRef.update("sessionList", FieldValue.arrayUnion(documentReference.getId()))
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(StudyGroupActivity.this, "Session added to group successfully!", Toast.LENGTH_SHORT).show();
                                    String sessionDetails = "\n" + "Name: " + name + "\n" +
                                            date + "\n" +
                                            startTime + "\n" +
                                            endTime + "\n" +
                                            "Location: " + location + "\n";

                                    items2.add(sessionDetails);
                                    ArrayAdapter<String> adapter2 = new ArrayAdapter<>(StudyGroupActivity.this, android.R.layout.simple_list_item_1, items2);
                                    lv2.setAdapter(adapter2);
                                })
                                .addOnFailureListener(e -> {
                                    Log.w("FirebaseError", "Error adding session to group", e);
                                    Toast.makeText(StudyGroupActivity.this, "Failed to add session to group", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Log.w("FirebaseError", "Error adding session", e);
                        Toast.makeText(StudyGroupActivity.this, "Failed to save session", Toast.LENGTH_SHORT).show();
                    });
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void removeStudySession(String sessionId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference sessionDoc = db.collection("sessions").document(sessionId);
        DocumentReference groupDoc = db.collection("groups").document(groupName);

        db.runTransaction(transaction -> {
            DocumentSnapshot groupSnapshot = transaction.get(groupDoc);

            List<String> sessionList = (List<String>) groupSnapshot.get("sessionList");
            if (sessionList != null && sessionList.contains(sessionId)) {
                sessionList.remove(sessionId);
                transaction.update(groupDoc, "sessionList", sessionList);
            }

            transaction.delete(sessionDoc);

            return null;
        }).addOnSuccessListener(aVoid -> {
            Log.d("SessionId", "Session ID: " + sessionId);
            Log.d("Firebase", "Session successfully removed!");
            for (int i = 0; i < items2.size(); i++) {
                if (items2.get(i).contains(sessionId)) { // Assuming sessionId is part of the session details
                    items2.remove(i);
                    break;
                }
            }

            ArrayAdapter<String> adapter2 = new ArrayAdapter<>(StudyGroupActivity.this, android.R.layout.simple_list_item_1, items2);
            lv2.setAdapter(adapter2);

            Toast.makeText(StudyGroupActivity.this, "Session removed successfully!", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Log.w("FirebaseError", "Error removing session", e);
            Toast.makeText(StudyGroupActivity.this, "Failed to remove session", Toast.LENGTH_SHORT).show();
        });
    }



    private void styleDateOrTimePicker(TextView textView) {
        textView.setTextColor(Color.BLACK);
        textView.setBackgroundColor(Color.parseColor("#E9E9EB"));
        textView.setTextSize(18f);
        textView.setPadding(32, 16, 32, 16);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(16, 32, 16, 32);
        textView.setLayoutParams(params);
        textView.setClickable(true);
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
}