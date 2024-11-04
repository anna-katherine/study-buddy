package com.example.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class EnrolledClassesActivity extends AppCompatActivity {

    Button dashboardButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.course_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // some code here to add the courses to the ListView (this is filler @Alex)
        ListView lv = findViewById(R.id.courseList);
        ArrayList<String> items = new ArrayList<>();
        items.add("Course 1");
        items.add("Course 2");
        items.add("Course 3");
        items.add("Course 4");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        lv.setAdapter(adapter);

        // navigate to dashboard page
        dashboardButton = findViewById(R.id.dashboardbutton);
        dashboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // just basic navigation for now, please edit for login functionality
                Intent intent = new Intent(EnrolledClassesActivity.this, DashboardActivity.class);
                startActivity(intent);
            }
        });

        // Logout user code (copy and paste on every activity with logout button)
        Button logoutButton = findViewById(R.id.logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Logout user
                FirebaseAuth.getInstance().signOut();
                // Navigate to login page
                Intent intent = new Intent(EnrolledClassesActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }
}