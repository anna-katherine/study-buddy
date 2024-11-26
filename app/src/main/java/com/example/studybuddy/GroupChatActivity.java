package com.example.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupChatActivity extends AppCompatActivity {

    ArrayList<String> chats;
    ArrayAdapter<String> adapter;
    FirebaseFirestore db;
    String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_chat);

        db = FirebaseFirestore.getInstance();  // Initialize Firebase Firestore

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the group name from intent
        groupName = getIntent().getStringExtra("com.example.studybuddy.GROUPNAME");

        ListView lv = findViewById(R.id.groupChatList);
        chats = new ArrayList<>();
        chats.add("Main GroupChat");  // Example entry

        // Set the adapter to the list view
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, chats);
        lv.setAdapter(adapter);

        // Set item click listener to open the ChatActivity when a group is selected
        lv.setOnItemClickListener((adapterView, view, i, l) -> {
            String selectedGroup = (String) adapterView.getItemAtPosition(i);

            // Check if the selected group is the "Main GroupChat"
            if (selectedGroup.equals("Main GroupChat")) {
                // Fetch chat logs for the "Main GroupChat" and pass them to the next activity
                fetchGroupChatLogs(groupName, "Main GroupChat");  // Group ID, change as necessary
            }
        });
    }

    // Fetch chat logs from Firestore
    private void fetchGroupChatLogs(String groupId, String groupChatName) {
        db = FirebaseFirestore.getInstance();
        DocumentReference messageDoc = db.collection("messages").document(groupId);
        messageDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        //Exists, dont create new chat
                        ArrayList<ChatMessage> chatLogs = (ArrayList<ChatMessage>) document.get("chatLogs");
                        Intent intent = new Intent(GroupChatActivity.this, ChatActivity.class);
                        intent.putExtra("com.example.studybuddy.GROUPNAME", chatLogs);
                        startActivity(intent);
                    }
                    else {
                        // Group does not exist, create it
                        Map<String, Object> chatInfo = new HashMap<>();
                        chatInfo.put("chatLogs", new ArrayList<ChatMessage>());
                        messageDoc.set(chatInfo);
                        Intent intent = new Intent(GroupChatActivity.this, ChatActivity.class);
                        intent.putExtra("com.example.studybuddy.GROUPNAME", new ArrayList<ChatMessage>());
                        startActivity(intent);
                    }
                }
                else{
                    int i = 0;
                }
            }
        });
    }
}