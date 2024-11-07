package com.example.studybuddy;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.Calendar;

public class StudyGroupActivity extends AppCompatActivity
{
    String groupName;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.study_group);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        groupName = intent.getStringExtra("com.example.studybuddy.GROUPNAME");
        TextView tv = findViewById(R.id.group_name);
        tv.setText(groupName);

        // Placeholders
        ListView lv = findViewById(R.id.memberList);
        ArrayList<String> items = new ArrayList<>();
        items.add("Member 1");
        items.add("Member 2");
        items.add("Member 3");
        items.add("Member 4");
        items.add("Member 5");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        lv.setAdapter(adapter);

        // Placeholders
        ListView lv2 = findViewById(R.id.sessionList);
        ArrayList<String> items2 = new ArrayList<>();
        items2.add("Session 1");
        items2.add("Session 2");
        items2.add("Session 3");
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items2);
        lv2.setAdapter(adapter2);

        Button resourceButton = findViewById(R.id.resources);
        resourceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Move to Resources page
                Intent intent = new Intent(StudyGroupActivity.this, ResourceActivity.class);
                startActivity(intent);
            }
        });

        // Navigation functionalities
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

    private void createSessionDialog(int number)
    {
        final EditText locationInput = new EditText(this);
        locationInput.setHint("Enter location");

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
        dialogLayout.addView(locationInput);

        builder.setView(dialogLayout);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String location = locationInput.getText().toString();
            String date = dateDisplay.getText().toString();
            String startTime = startTimeDisplay.getText().toString();
            String endTime = endTimeDisplay.getText().toString();

            // Save this date for Firebase @Alex
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
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
}