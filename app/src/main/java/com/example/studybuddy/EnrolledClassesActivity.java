package com.example.studybuddy;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EnrolledClassesActivity extends AppCompatActivity {
    FirebaseUser user;
    FirebaseFirestore db;
    ArrayList<String> items;
    ArrayList<String> allClasses;
    ArrayAdapter<String> adapter;
    DocumentReference userDoc;
    CollectionReference groupCol;

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

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        userDoc = db.collection("users").document(user.getUid());
        groupCol = db.collection("groups");

        allClasses = new ArrayList<>();
        allClasses.add("Course 1: Math");
        allClasses.add("Course 2: English");
        allClasses.add("Course 3: History");
        allClasses.add("Course 4: Human Biology");
        allClasses.add("Course 5: Data Structures");

        // some code here to add the courses to the ListView (this is filler @Alex)
        ListView lv = findViewById(R.id.courseList);
        items = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        lv.setAdapter(adapter);

        Intent intent = getIntent();
        ArrayList<String> courseList = intent.getStringArrayListExtra("com.example.studybuddy.COURSES");
        items.addAll(courseList);

        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        //dont need anything
                    }
                    else {
                        // Group does not exist, create it
                        Map<String, Object> userInfo = new HashMap<>();
                        userInfo.put("groupList", new ArrayList<>());
                        userInfo.put("displayName", user.getDisplayName());
                        userInfo.put("classList", courseList);
                        userDoc.set(userInfo);
                        items.clear();
                        items.addAll(courseList);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        // navigate to dashboard page

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                if (item.getItemId() == R.id.dashboard) {
                    startActivity(new Intent(EnrolledClassesActivity.this, DashboardActivity.class));
                    finish();
                } else if (item.getItemId() == R.id.log_out) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(EnrolledClassesActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                return true;
            }
        });

        lv.setOnItemLongClickListener((parent, view, position, id) -> {
            new AlertDialog.Builder(EnrolledClassesActivity.this)
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete this item?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        String remove = items.get(position);
                        items.remove(position);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(EnrolledClassesActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                        removeClass(remove);

                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
            return true;
        });
        Button joinGroupButton = findViewById(R.id.joinGroupButton);
        joinGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeClassListAndShowDialog();
            }
        });
    }

    void initializeClassListAndShowDialog() {
        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    ArrayList<String> classList = new ArrayList<String>();
                    classList.addAll(allClasses);
                    ArrayList<String> userClasses = (ArrayList<String>) document.get("classList");
                    if (userClasses == null){
                        userClasses = new ArrayList<String>();
                    }
                    for (int i = 0; i < allClasses.size(); i++){
                        if (userClasses.contains(allClasses.get(i))){
                            classList.remove(allClasses.get(i));
                        }
                    }
                    joinDialog(classList);
                } else {
                    Log.w("FirebaseError", "Error fetching group list", task.getException());
                }
            }
        });
    }

    private boolean[] toPrimitiveBooleanArray(List<Boolean> checkedClasses) {
        boolean[] result = new boolean[checkedClasses.size()];
        for (int i = 0; i < checkedClasses.size(); i++) {
            result[i] = checkedClasses.get(i);
        }
        return result;
    }

    private void joinDialog(ArrayList<String> classList) {
        String userID = user.getUid();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        List<Boolean> checkedClasses = new ArrayList<>(Collections.nCopies(classList.size(), Boolean.FALSE));

        builder.setTitle("Join New Groups")
                .setMultiChoiceItems(classList.toArray(new CharSequence[0]), toPrimitiveBooleanArray(checkedClasses), new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkedClasses.set(which, isChecked);
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        List<String> selectedClasses = new ArrayList<>();
                        for (int i = 0; i < classList.size(); i++) {
                            if (checkedClasses.get(i)) {
                                selectedClasses.add(classList.get(i));
                            }
                        }
                        handleGroupSelection(selectedClasses);
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

    public void handleGroupSelection(List<String> selectedClasses) {
        if (!selectedClasses.isEmpty()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(user.getUid());

            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Fetch the current class list
                            List<String> currentClassList = (List<String>) document.get("classList");
                            if (currentClassList == null) {
                                currentClassList = new ArrayList<>();
                            }

                            // Create a new list with the updated groups
                            List<String> updatedClassList = new ArrayList<>(currentClassList);
                            updatedClassList.addAll(selectedClasses);


                            userRef.update("classList", updatedClassList)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Firebase", "User's group list updated successfully");
                                        refreshClassListUI(updatedClassList);
                                    })
                                    .addOnFailureListener(e -> Log.w("FirebaseError", "Error updating group list", e));

                        }
                    } else {
                        Log.w("FirebaseError", "Error fetching user data", task.getException());
                    }
                }
            });
        }
    }

    private void refreshClassListUI(List<String> updatedClassList) {
        items.clear();
        items.addAll(updatedClassList);
        adapter.notifyDataSetChanged();
        Log.d("UI", "Group list UI updated successfully");
    }

    void removeClass(String name){
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(userDoc);
                ArrayList<String> courseList = (ArrayList<String>) snapshot.get("classList");
                ArrayList<String> groupList = (ArrayList<String>) snapshot.get("groupList");
                courseList.remove(name);
                if (groupList != null) {
                    //For each group, if the course = removed course, first remove
                    for (int i = 0; i < groupList.size(); i++) {
                        String groupName = groupList.get(i);
                        DocumentSnapshot groupSnapshot = transaction.get(groupCol.document(groupName));
                        String courseName = (String) groupSnapshot.get("course");
                        if (courseName.equals(name)){
                            groupList.remove(groupList.get(i));
                            ArrayList<String> memberList = (ArrayList<String>) groupSnapshot.get("memberList");
                            memberList.remove(user.getUid());
                            transaction.update(groupCol.document(groupName), "memberList", memberList);
                        }
                    }
                }
                transaction.update(userDoc, "classList", courseList);
                transaction.update(userDoc, "groupList", groupList);
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
}