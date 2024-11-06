package com.example.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class GroupChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.group_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        ListView lv = findViewById(R.id.groupChatList);
        ArrayList<String> chats = new ArrayList<>();
        // some arbitrary amount
        for (int i = 0; i < 5; i++)
        {
            chats.add("Group Chat " + i);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, chats);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Intent intent = new Intent(GroupChatActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });

        // FIXME Now need to find groupName in Firebase database and use it to get past chat history.
        Intent intent = getIntent();
        String groupName = intent.getStringExtra("com.example.studybuddy.GROUPNAME");

    }
}