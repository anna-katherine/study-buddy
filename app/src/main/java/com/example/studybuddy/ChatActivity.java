package com.example.studybuddy;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class
ChatActivity extends AppCompatActivity {

    private ListView chatMessagesListView;
    private ChatMessageAdapter adapter;
    private ArrayList<ChatMessage> chatMessages;

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

        chatMessagesListView = findViewById(R.id.chatMessagesListView);
        EditText editTextMessage = findViewById(R.id.editTextMessage);
        Button buttonSend = findViewById(R.id.buttonSend);

        chatMessages = new ArrayList<>();
        adapter = new ChatMessageAdapter(this, chatMessages);
        chatMessagesListView.setAdapter(adapter);

        chatMessages.add(new ChatMessage("Hey, how's it going?", "10:00", "Alice", false)); // Message from Alice
        chatMessages.add(new ChatMessage("Good, thanks! How about you?", "10:01", "You", true)); // Your message
        chatMessages.add(new ChatMessage("I'm doing well, just working on a project.", "10:02", "Alice", false)); // Message from Alice

        adapter.notifyDataSetChanged();

        String senderName = "You";
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = editTextMessage.getText().toString().trim();
                if (!messageText.isEmpty()) {
                    String timestamp = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                    ChatMessage newMessage = new ChatMessage(messageText, timestamp, senderName, true); // 'true' for your messages
                    chatMessages.add(newMessage);
                    adapter.notifyDataSetChanged();
                    editTextMessage.setText("");
                    chatMessagesListView.setSelection(chatMessages.size() - 1);  // Scroll to the bottom
                }
            }
        });
    }
}