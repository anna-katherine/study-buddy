package com.example.studybuddy;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class StudyGroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.study_group);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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


    }
}