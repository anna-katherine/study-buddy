package com.example.studybuddy;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
    ArrayList<String> chatUid;
    ArrayAdapter<String> adapter;
    FirebaseFirestore db;
    String groupName;
    ArrayList<String> memberList;

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
        chatUid = new ArrayList<>();

        chats.add("Main GroupChat");
        chatUid.add("");
        initializeChats(lv);

        // Set item click listener to open the ChatActivity when a group is selected
        lv.setOnItemClickListener((adapterView, view, i, l) -> {
            String selectedGroup = (String) adapterView.getItemAtPosition(i);

            // Check if the selected group is the "Main GroupChat"
            if (selectedGroup.equals("Main GroupChat")) {
                // Fetch chat logs for the "Main GroupChat" and pass them to the next activity
                fetchGroupChatLogs(groupName);  // Group ID, change as necessary
            }
            else{
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String chatID;
                if (userID.compareTo(chatUid.get(i)) < 0){
                    chatID = userID + chatUid.get(i);
                }
                else{
                    chatID = chatUid.get(i) + userID;
                }
                fetchGroupChatLogs(chatID);
            }
        });
    }
    private void initializeChats(ListView lv) {
        DocumentReference groupRef = db.collection("groups").document(groupName);

        // Fetch the group document from Firestore
        groupRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Retrieve the memberList from the document
                        List<String> memberList = (List<String>) document.get("memberList");

                        if (memberList != null && !memberList.isEmpty()) {
                            // Loop through each member ID and fetch their displayName from Firestore
                            for (String memberId : memberList) {
                                // Firebase reference to the users collection
                                DocumentReference userRef = db.collection("users").document(memberId);

                                // Fetch the user document from Firestore
                                userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot userDoc = task.getResult();
                                            if (userDoc.exists()) {
                                                // Retrieve the displayName of the user
                                                String userName = userDoc.getString("displayName");

                                                // Add the userName to the list if it's not null
                                                if (userName != null) {
                                                    if (memberId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                                        userName += " (You)";
                                                    }
                                                    chats.add(userName);
                                                    chatUid.add(memberId);
                                                }

                                                // Once all members have been processed, update the ListView
                                                if (chats.size() > memberList.size()) {
                                                    adapter = new ArrayAdapter<>(GroupChatActivity.this, android.R.layout.simple_list_item_1, chats);
                                                    lv.setAdapter(adapter);
                                                    Log.d("Firebase", "Member List: " + chats.toString());
                                                }
                                            }
                                        } else {
                                            Log.w("FirebaseError", "Error fetching user data", task.getException());
                                        }
                                    }
                                });
                            }
                        } else {
                            Log.d("Firebase", "No members found in the group.");
                        }
                    } else {
                        Log.d("Firebase", "Group document does not exist.");
                    }
                } else {
                    Log.w("FirebaseError", "Error getting group data", task.getException());
                }
            }
        });
    }

    // Fetch chat logs from Firestore
    private void fetchGroupChatLogs(String groupId) {
        db = FirebaseFirestore.getInstance();
        DocumentReference messageDoc = db.collection("messages").document(groupId);
        messageDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        //Exists, dont create new chat
                        ArrayList<HashMap<String, Object>> chatLogs = (ArrayList<HashMap<String, Object>>) document.get("chatLogs");
                        Intent intent = new Intent(GroupChatActivity.this, ChatActivity.class);
                        intent.putExtra("com.example.studybuddy.CHATLOGS", chatLogs);
                        intent.putExtra("com.example.studybuddy.GROUPNAME", groupId);
                        startActivity(intent);
                    }
                    else {
                        // Group does not exist, create it
                        Map<String, Object> chatInfo = new HashMap<>();
                        chatInfo.put("chatLogs", new ArrayList<HashMap<String, Object>>());
                        messageDoc.set(chatInfo);
                        Intent intent = new Intent(GroupChatActivity.this, ChatActivity.class);
                        intent.putExtra("com.example.studybuddy.CHATLOGS", new ArrayList<HashMap<String, Object>>());
                        intent.putExtra("com.example.studybuddy.GROUPNAME", groupId);
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
