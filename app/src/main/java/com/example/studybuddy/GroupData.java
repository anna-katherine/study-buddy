package com.example.studybuddy;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutionException;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import com.google.android.gms.tasks.OnCompleteListener;

public class GroupData {
    // Your existing fields
    List<String> members;
    List<StudySession> studySessions;
    String groupName;
    FirebaseFirestore db;
    DocumentReference group;
    Map<String, Object> groupInfo;

    // Listener interface
    public interface OnGroupDataReadyListener {
        void onGroupDataReady(GroupData groupData);
        void onError(Exception e);
        void onCreateComplete();
        void onUpdateComplete();
    }

    // Constructor: GroupData initialization
    public GroupData(String groupName, Boolean isExisting, final OnGroupDataReadyListener listener) {
        db = FirebaseFirestore.getInstance();
        this.groupName = groupName;
        this.group = db.collection("groups").document(groupName);

        // Fetch group data from Firestore
        group.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        // Initialize group data
                        members = (List<String>) document.get("memberList");
                        studySessions = (List<StudySession>) document.get("sessionList");
                    } else {
                        // Group does not exist, create it
                        createGroup(listener);
                        return;
                    }
                } else {
                    // Error fetching the group data
                    if (listener != null) listener.onError(task.getException());
                    return;
                }

                // Notify listener when group data is ready
                if (listener != null) {
                    listener.onGroupDataReady(GroupData.this);
                }
            }
        });
    }

    // Update function: updates all necessary data and notifies the listener when done
    public void update(final OnGroupDataReadyListener listener) {
        // Example: Update members and session list in Firestore
        groupInfo = new HashMap<>();
        groupInfo.put("memberList", members);  // Set updated member list
        groupInfo.put("sessionList", studySessions);  // Set updated session list

        // Update the group document with new data
        group.update(groupInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    // Notify that update is complete
                    if (listener != null) {
                        listener.onUpdateComplete();  // New listener callback for update completion
                    }
                } else {
                    // Handle errors during update
                    if (listener != null) {
                        listener.onError(task.getException());
                    }
                }
            }
        });
    }

    // Method to create a new group with initial setup
    private void createGroup(final OnGroupDataReadyListener listener) {
        groupInfo = new HashMap<>();
        members = new ArrayList<>();
        groupInfo.put("memberList", members);
        groupInfo.put("sessionList", new ArrayList<StudySession>());

        group.set(groupInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    // Notify listener once group creation is successful
                    if (listener != null) {
                        listener.onCreateComplete();
                    }
                } else {
                    // Handle error in setting the document
                    if (listener != null) {
                        listener.onError(task.getException());
                    }
                }
            }
        });
    }
}
