package com.example.studybuddy;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.net.Uri;
import android.content.ActivityNotFoundException;
import android.database.Cursor;
import android.provider.OpenableColumns;
import android.app.ProgressDialog;
import android.webkit.MimeTypeMap;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ChatActivity extends AppCompatActivity {

    ListView chatMessagesListView;
    ChatMessageAdapter adapter;
    ArrayList<ChatMessage> chatMessages;
    FirebaseFirestore db;
    String groupName;
    String groupChatName;
    String user;
    private ActivityResultLauncher<String[]> filePickerLauncher;
    private FirebaseStorage storage;
    private static final int MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB max file size

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialize file picker launcher
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null) {
                        handleSelectedFile(uri);
                    }
                }
        );

        Intent intent = getIntent();
        groupName = intent.getStringExtra("com.example.studybuddy.GROUPNAME");
        ArrayList<HashMap<String, Object>> chatLogs = (ArrayList<HashMap<String, Object>>) intent.getSerializableExtra("com.example.studybuddy.CHATLOGS");

        chatMessagesListView = findViewById(R.id.chatMessagesListView);
        EditText editTextMessage = findViewById(R.id.editTextMessage);
        Button buttonSend = findViewById(R.id.buttonSend);
        Button buttonUpload = findViewById(R.id.buttonUpload);

        chatMessages = new ArrayList<>();

        adapter = new ChatMessageAdapter(this, chatMessages);
        chatMessagesListView.setAdapter(adapter);


        // Load existing chat messages
        for (int i = 0; i < chatLogs.size(); i++) {
            HashMap<String, Object> chatInfo = chatLogs.get(i);
            String messageText = (String) chatInfo.get("messageText");
            String sender = (String) chatInfo.get("sender");
            String userID = (String) chatInfo.get("userID");
            if (userID.equals(user)) {
                sender += " (You)";
            }
            Timestamp timestamp = (Timestamp) chatInfo.get("timestamp");
            ChatMessage message = new ChatMessage(messageText, timestamp, sender, (user.equals(userID)));
            if (chatInfo.containsKey("fileInfo")) {
                message.setFileInfo((HashMap<String, Object>) chatInfo.get("fileInfo"));
            }
            chatMessages.add(message);
        }

        adapter.notifyDataSetChanged();

        String senderName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = editTextMessage.getText().toString().trim();
                if (!messageText.isEmpty()) {
                    editTextMessage.setText("");
                    sendMessageToFirestore(groupName, groupChatName, senderName, messageText, null);
                }
            }
        });

        chatMessagesListView.setSelection(adapter.getCount() - 1);
        buttonUpload.setOnClickListener(v -> openFilePicker());
    }

    private void openFilePicker() {
        String[] mimeTypes = {
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "image/*",
                "text/*"
        };

        try {
            filePickerLauncher.launch(mimeTypes);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No file picker found", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleSelectedFile(Uri fileUri) {
        try {
            // Check file size
            long fileSize = getFileSize(fileUri);
            if (fileSize > MAX_FILE_SIZE) {
                Toast.makeText(this, "File size must be less than 10MB", Toast.LENGTH_LONG).show();
                return;
            }

            // Show upload progress
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading File");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(100);
            progressDialog.show();

            String fileName = getFileName(fileUri);

            // Create storage reference
            StorageReference storageRef = storage.getReference()
                    .child("chat_files")
                    .child(groupName)
                    .child(System.currentTimeMillis() + "_" + fileName);

            // Upload file
            storageRef.putFile(fileUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get download URL
                        storageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            // Create file message
                            String messageText = "📎 File: " + fileName;
                            HashMap<String, Object> fileInfo = new HashMap<>();
                            fileInfo.put("type", "file");
                            fileInfo.put("fileName", fileName);
                            fileInfo.put("fileUrl", downloadUri.toString());
                            fileInfo.put("mimeType", getMimeType(fileUri));

                            String senderName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                            sendMessageToFirestore(groupName, groupChatName, senderName, messageText, fileInfo);

                            progressDialog.dismiss();
                            Toast.makeText(ChatActivity.this, "File uploaded successfully", Toast.LENGTH_SHORT).show();
                        });
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(ChatActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        int progress = (int) ((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                        progressDialog.setProgress(progress);
                    });

        } catch (Exception e) {
            Toast.makeText(this, "Error processing file: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private long getFileSize(Uri fileUri) {
        try (Cursor cursor = getContentResolver().query(fileUri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (sizeIndex != -1) {
                    return cursor.getLong(sizeIndex);
                }
            }
        }
        return 0;
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private String getMimeType(Uri uri) {
        String mimeType = getContentResolver().getType(uri);
        if (mimeType == null) {
            String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        }
        return mimeType != null ? mimeType : "application/octet-stream";
    }

    public static ArrayList<ChatMessage> setupChatLogs(ArrayList<HashMap<String, Object>> chatLogs, String username) {
        ArrayList<ChatMessage> logs = new ArrayList<>();
        if (chatLogs != null) {
            for (int i = 0; i < chatLogs.size(); i++) {
                HashMap<String, Object> chatInfo = chatLogs.get(i);
                String messageText = (String) chatInfo.get("messageText");
                String sender = (String) chatInfo.get("sender");
                String userID = (String) chatInfo.get("userID");
                if (username != null) {
                    if (userID != null) {
                        if (userID.equals(username)) {
                            sender += " (You)";
                        }
                    }
                }
                Timestamp timestamp = (Timestamp) chatInfo.get("timestamp");
                ChatMessage message = new ChatMessage(messageText, timestamp, sender, (username.equals(userID)));
                if (chatInfo.containsKey("fileInfo")) {
                    message.setFileInfo((HashMap<String, Object>) chatInfo.get("fileInfo"));
                }
                logs.add(message);
            }
        }
        return logs;
    }

    public void sendMessageToFirestore(String groupId, String groupChatName, String senderName, String message, HashMap<String, Object> fileInfo) {
        Timestamp timestamp = Timestamp.now();

        HashMap<String, Object> newChat = new HashMap<>();
        newChat.put("messageText", message);
        newChat.put("sender", senderName);
        newChat.put("timestamp", timestamp);
        newChat.put("userID", user);
        if (fileInfo != null) {
            newChat.put("fileInfo", fileInfo);
        }

        DocumentReference groupRef = db.collection("messages").document(groupId);

        groupRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<HashMap<String, Object>> messages = (ArrayList<HashMap<String, Object>>) documentSnapshot.get("chatLogs");
                if (messages == null) {
                    messages = new ArrayList<>();
                }

                messages.add(newChat);

                groupRef.update("chatLogs", messages)
                        .addOnSuccessListener(aVoid -> {
                            ChatMessage newMessage = new ChatMessage(message, timestamp, senderName + " (You)", true);
                            if (fileInfo != null) {
                                newMessage.setFileInfo(fileInfo);
                            }
                            chatMessages.add(newMessage);
                            adapter.notifyDataSetChanged();
                            Log.d("Chat", "Message added successfully!");
                        })
                        .addOnFailureListener(e -> {
                            e.printStackTrace();
                            Toast.makeText(ChatActivity.this, "Failed to send message.", Toast.LENGTH_SHORT).show();
                        });
            } else {
                List<HashMap<String, Object>> chatLog = new ArrayList<>();
                chatLog.add(newChat);

                groupRef.set(new HashMap<String, Object>() {{
                    put("chatLogs", chatLog);
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