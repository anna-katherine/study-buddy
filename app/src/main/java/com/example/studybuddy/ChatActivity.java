package com.example.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.Timestamp;

import java.util.List;

public class
ChatActivity extends AppCompatActivity {

    ListView chatMessagesListView;
    ChatMessageAdapter adapter;
    ArrayList<ChatMessage> chatMessages;
    FirebaseFirestore db;
    String groupName;
    String groupChatName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();

        chatMessagesListView = findViewById(R.id.chatMessagesListView);
        EditText editTextMessage = findViewById(R.id.editTextMessage);
        Button buttonSend = findViewById(R.id.buttonSend);

        groupName = intent.getStringExtra("com.example.studybuddy.GROUPNAME");
        groupChatName = intent.getStringExtra("com.example.studybuddy.GROUPCHATNAME");

        chatMessages = (ArrayList<ChatMessage>) intent.getSerializableExtra("com.example.studybuddy.CHATLOGS");
        if (chatMessages == null){
            chatMessages = new ArrayList<ChatMessage>();
        }
        adapter = new ChatMessageAdapter(this, chatMessages);
        chatMessagesListView.setAdapter(adapter);

        //chatMessages.add(new ChatMessage("Hey, how's it going?", "10:00", "Alice", false)); // Message from Alice
        //chatMessages.add(new ChatMessage("Good, thanks! How about you?", "10:01", "You", true)); // Your message
        //chatMessages.add(new ChatMessage("I'm doing well, just working on a project.", "10:02", "Alice", false)); // Message from Alice

        adapter.notifyDataSetChanged();

        String senderName = "You";
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = editTextMessage.getText().toString().trim();
                if (!messageText.isEmpty()) {
                    Timestamp timestamp = Timestamp.now();
                    ChatMessage newMessage = new ChatMessage(messageText, timestamp, senderName, true); // 'true' for your messages
                    chatMessages.add(newMessage);
                    adapter.notifyDataSetChanged();
                    editTextMessage.setText("");
                    chatMessagesListView.setSelection(chatMessages.size() - 1);  // Scroll to the bottom
                    sendMessageToFirestore(groupName, groupChatName, senderName, messageText);
                }
            }
        });
    }

    public void sendMessageToFirestore(String groupId, String groupChatName, String senderName, String message) {
        // Create a new Timestamp
        Timestamp timestamp = Timestamp.now();  // Use Firebase's Timestamp class to get the current time

        // Create a new ChatMessage object
        ChatMessage chatMessage = new ChatMessage(message, timestamp, senderName, true);  // true for user messages

        // Get a reference to the Firestore database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get the document reference for the group chat
        DocumentReference groupRef = db.collection("messages").document(groupId);

        // Get the current messages in the group
        groupRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Fetch existing messages if they exist
                List<ChatMessage> messages = (List<ChatMessage>) documentSnapshot.get("messages");
                if (messages == null) {
                    messages = new ArrayList<>();
                }

                // Add the new message to the list
                messages.add(chatMessage);

                // Update the group document with the new message list
                groupRef.update("messages", messages)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("Chat", "Message added successfully!");
                        })
                        .addOnFailureListener(e -> {
                            e.printStackTrace();
                            Toast.makeText(ChatActivity.this, "Failed to send message.", Toast.LENGTH_SHORT).show();
                        });
            } else {
                // If no document exists, create one with the first message
                List<ChatMessage> newMessagesList = new ArrayList<>();
                newMessagesList.add(chatMessage);

                groupRef.set(new HashMap<String, Object>() {{
                    put("groupChatName", groupChatName);
                    put("messages", newMessagesList);
                }}).addOnSuccessListener(aVoid -> {
                    Log.d("Chat", "Group chat created and message added!");
                }).addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(ChatActivity.this, "Failed to create group chat.", Toast.LENGTH_SHORT).show();
                });
            }
        }).addOnFailureListener(e -> {
            e.printStackTrace();
            Toast.makeText(ChatActivity.this, "Failed to retrieve group chat data.", Toast.LENGTH_SHORT).show();
        });
    }
}