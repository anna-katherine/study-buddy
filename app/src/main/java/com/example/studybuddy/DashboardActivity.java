package com.example.studybuddy;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {

    ArrayList<String> groupList = new ArrayList<>(Collections.singletonList(" "));;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;
    DocumentReference userDoc;
    CollectionReference groupCol;
    ArrayList<String> items;
    ArrayAdapter<String> adapter;

    private boolean[] toPrimitiveBooleanArray(List<Boolean> checkedGroups) {
        boolean[] result = new boolean[checkedGroups.size()];
        for (int i = 0; i < checkedGroups.size(); i++) {
            result[i] = checkedGroups.get(i);
        }
        return result;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userDoc = db.collection("users").document(user.getUid());
        groupCol = db.collection("groups");

        ListView lv = findViewById(R.id.groupList);
        items = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        lv.setAdapter(adapter);

        fetchUserData(user.getUid());

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Intent intent = new Intent(DashboardActivity.this, StudyGroupActivity.class);
                intent.putExtra("com.example.studybuddy.GROUPNAME", adapter.getItem(i));
                startActivity(intent);
            }
        });

        lv.setOnItemLongClickListener((parent, view, position, id) -> {
            new AlertDialog.Builder(DashboardActivity.this)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    String remove = items.get(position);
                    items.remove(position);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(DashboardActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                    // Add Firebase implementation here @Alex
                    removeGroup(remove);

                })
                .setNegativeButton("No", (dialog, which) -> {
                    // Do nothing
                    dialog.dismiss();
                })
                .show();
                return true;
        });

        Button joinGroupButton = findViewById(R.id.joinGroupButton);
        joinGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinDialog();
            }
        });

        Button createGroupButton = findViewById(R.id.createGroupButton);
        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog();
            }
        });

        //Display user's email (for now, can change to username maybe)
        auth = FirebaseAuth.getInstance();
        TextView tv = findViewById(R.id.user_details);
        user = auth.getCurrentUser();
        if (user == null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            String name = user.getDisplayName();
            name += "'s Dashboard";
            tv.setText(name);
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                if (item.getItemId() == R.id.courses) {
                    startActivity(new Intent(DashboardActivity.this, EnrolledClassesActivity.class));
                } else if (item.getItemId() == R.id.log_out) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });


    }


    private void joinDialog()
    {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        CollectionReference groupsRef = db.collection("groups");

        groupsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    groupList.clear(); // Clear the list before adding new data
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        String groupName = document.getString("name");
                        List<String> memberList = (List<String>) document.get("memberList");

                        if (groupName != null && (memberList == null || !memberList.contains(userID))) {
                            groupList.add(groupName); // Add group name to the list if user is not a member
                        }
                    }

                    if (adapter != null) {
                        fetchUserData(user.getUid());
                        adapter.notifyDataSetChanged();
                    }

                    // Log the final group list
                    Log.d("Firebase", "Group List (excluding user groups): " + groupList.toString());
                } else {
                    Log.w("FirebaseError", "Error getting groups", task.getException());
                }
            }
        });

        List<Boolean> checkedGroups = new ArrayList<>(Collections.nCopies(groupList.size(), Boolean.FALSE));

        builder.setTitle("Join New Groups")
                .setMultiChoiceItems(groupList.toArray(new CharSequence[0]), toPrimitiveBooleanArray(checkedGroups), new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkedGroups.set(which, isChecked);
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Gather the selected groups
                        List<String> selectedGroups = new ArrayList<>();
                        for (int i = 0; i < groupList.size(); i++) {
                            if (checkedGroups.get(i)) {
                                selectedGroups.add(groupList.get(i));
                            }
                        }

                        if (!selectedGroups.isEmpty()) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference userRef = db.collection("users").document(userID);

                            // Get the current group list from the user document
                            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            List<String> currentGroupList = (List<String>) document.get("groupList");
                                            if (currentGroupList == null) {
                                                currentGroupList = new ArrayList<>();
                                            }
                                            // Add the new groups to the user's current groupList
                                            currentGroupList.addAll(selectedGroups);

                                            // Update the user's groupList in Firestore
                                            userRef.update("groupList", currentGroupList)
                                                    .addOnSuccessListener(aVoid -> Log.d("Firebase", "User's group list updated successfully"))
                                                    .addOnFailureListener(e -> Log.w("FirebaseError", "Error updating group list", e));
                                            // Iterate over selected groups and update their member lists
                                            for (String groupName : selectedGroups) {
                                                // Get the group document by name
                                                CollectionReference groupsRef = db.collection("groups");
                                                groupsRef.whereEqualTo("name", groupName).get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                                                    // Successfully fetched the query results
                                                                    QuerySnapshot querySnapshot = task.getResult();
                                                                    for (QueryDocumentSnapshot groupDoc : querySnapshot) {

                                                                        List<String> memberList = (List<String>) groupDoc.get("memberList");

                                                                        if (memberList == null) {
                                                                            memberList = new ArrayList<>();
                                                                        }

                                                                        // Add the user to the member list if they are not already included
                                                                        if (!memberList.contains(userID)) {
                                                                            memberList.add(userID);

                                                                            // Update the group document's memberList with the new list
                                                                            groupDoc.getReference().update("memberList", memberList)
                                                                                    .addOnSuccessListener(aVoid -> Log.d("Firebase", "Group's member list updated successfully"))
                                                                                    .addOnFailureListener(e -> Log.w("FirebaseError", "Error updating member list", e));
                                                                        }
                                                                    }
                                                                } else {
                                                                    Log.w("FirebaseError", "Error fetching group data", task.getException());
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    } else {
                                        Log.w("FirebaseError", "Error fetching user data", task.getException());
                                    }
                                }
                            });
                        }
                        if (adapter != null) {
                            fetchUserData(user.getUid());
                            adapter.notifyDataSetChanged();
                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

//    private void getGroupsToJoin(ArrayList<String> groupList)
//    {
//        CollectionReference groupsRef = db.collection("groups");
//
//        groupsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        String groupName = document.getString("name");
//                        groupList.add(groupName);
//                    }
//                    }
//                } else {
//                    Log.w("FirebaseError", "Error getting groups", task.getException());
//                }
//            }
//        });
//
//    }

    private void createDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.create_group_dialog, null);
        Spinner dropdownSpinner = dialogView.findViewById(R.id.dropdown_spinner);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();  // Get the current user ID
        DocumentReference userRef = db.collection("users").document(userID);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<String> courseList = (List<String>) document.get("courseList");

                        if (courseList != null && !courseList.isEmpty()) {
                            // Convert the course list to a String[] array
                            String[] items = courseList.toArray(new String[0]);

                            // Use the correct context here (YourActivityName.this)
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(DashboardActivity.this, android.R.layout.simple_spinner_item, courseList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            dropdownSpinner.setAdapter(adapter);

                            EditText inputField = dialogView.findViewById(R.id.input_field);

                            // Use the correct context for the AlertDialog
                            AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
                            builder.setView(dialogView)
                                    .setTitle("Create a Group")
                                    .setPositiveButton("OK", (dialog, id) -> {
                                        String inputText = inputField.getText().toString();
                                        String selectedOption = dropdownSpinner.getSelectedItem().toString();
                                        createGroup(inputText, selectedOption);
                                    })
                                    .setNegativeButton("Cancel", (dialog, id) -> {
                                        dialog.dismiss();
                                    });

                            AlertDialog dialog = builder.create();
                            dialog.show();

                            Log.d("Firebase", "Courses: " + Arrays.toString(items));
                        } else {
                            Log.d("Firebase", "No courses found for this user.");
                        }
                    } else {
                        Log.d("Firebase", "User document does not exist.");
                    }
                } else {
                    Log.w("FirebaseError", "Error getting user document", task.getException());
                }
            }
        });
    }

    private void fetchUserData(String userID) {
        items.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference groupsRef = db.collection("groups");

        groupsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Iterate through all groups in the collection
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Get the group name and the member list for each group
                        String groupName = document.getString("name");
                        List<String> memberList = (List<String>) document.get("memberList");

                        // Check if the memberList contains the userID
                        if (memberList != null && memberList.contains(userID)) {
                            // If the user is a member, add the group name to the items list
                            if (groupName != null) {
                                items.add(groupName);
                            }
                        }
                    }

                    // Notify the adapter that the data has changed
                    adapter.notifyDataSetChanged();
                } else {
                    Log.w(TAG, "Error getting groups", task.getException());
                }
            }
        });
    }


    Boolean createdGroup = true;

    private void createGroup(String groupName, String selectedOption){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference group = db.collection("groups").document(groupName);

        //Add group to database
        group.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        //Exists, dont create new group
                        createdGroup = false;
                    }
                    else {
                        // Group does not exist, create it
                        Map<String, Object> groupInfo = new HashMap<>();
                        List<String> members = new ArrayList<>();
                        members.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        groupInfo.put("memberList", members);
                        groupInfo.put("sessionList", new ArrayList<StudySession>());
                        groupInfo.put("name", groupName);
                        groupInfo.put("course", selectedOption);
                        group.set(groupInfo);
                    }
                }
            }
        });
        //If it was created, also add to user's grouplist.
        if (createdGroup){
            db.runTransaction(new Transaction.Function<Void>() {
                        @Override
                        public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                            DocumentSnapshot snapshot = transaction.get(userDoc);
                            ArrayList<String> newList = (ArrayList<String>) snapshot.get("groupList");
                            newList.add(groupName);
                            transaction.update(userDoc, "groupList", newList);
                            return null;
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Transaction success!");
                            fetchUserData(user.getUid());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Transaction failure.", e);
                        }
                    });
        }
        createdGroup = true;
    }

    private void removeGroup(String name){
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(userDoc);
                ArrayList<String> newList = (ArrayList<String>) snapshot.get("groupList");
                newList.remove(name);
                transaction.update(userDoc, "groupList", newList);
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Transaction success!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Transaction failure.", e);
            }
        });

        DocumentReference groupDoc = groupCol.document(name);
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(groupDoc);
                ArrayList<String> newList = (ArrayList<String>) snapshot.get("memberList");
                newList.remove(user.getUid());

                //If nobody is in the group, just delete it.
                if (newList.isEmpty()){
                    transaction.delete(groupDoc);
                }
                else {
                    transaction.update(groupDoc, "memberList", newList);
                }
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Transaction success!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Transaction failure.", e);
            }
        });
    }


    private void joinGroup(String groupName){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference group = db.collection("groups").document(groupName);

        //Add group to database
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(group);
                ArrayList<String> newList = (ArrayList<String>) snapshot.get("memberList");
                newList.add(user.getUid());
                transaction.update(group, "memberList", newList);
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Transaction success!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Transaction failure.", e);
            }
        });
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(userDoc);
                ArrayList<String> newList = (ArrayList<String>) snapshot.get("groupList");
                newList.add(groupName);
                transaction.update(userDoc, "groupList", newList);
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Transaction success!");
                fetchUserData(user.getUid());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Transaction failure.", e);
            }
        });
    }
}