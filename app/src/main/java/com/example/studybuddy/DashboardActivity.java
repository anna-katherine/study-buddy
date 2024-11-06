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
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {

    boolean[] checkedGroups;
    String[] groupList = {"Group 1", "Group 2", "Group 3"};
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;
    DocumentReference userDoc;
    CollectionReference groupCol;
    ArrayList<String> items;
    ArrayAdapter<String> adapter;


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
        items.add("Group 1");
        items.add("Group 2");
        items.add("Group 3");
        items.add("Group 4");

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

        checkedGroups = new boolean[groupList.length];
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
        // replace this with study groups
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Join New Groups")
            .setMultiChoiceItems(groupList, checkedGroups, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    // Save the checked state
                    checkedGroups[which] = isChecked;
                }
            }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Handle positive button click - show selected options
                        StringBuilder selectedOptions = new StringBuilder("Selected: ");
                        for (int i = 0; i < groupList.length; i++) {
                            if (checkedGroups[i]) {
                                selectedOptions.append(checkedGroups[i]).append(", ");
                            }
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

    private void getGroupsToJoin()
    {
        // pull from list of groups to populate the list
    }

    private void createDialog()
    {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.create_group_dialog, null);
        Spinner dropdownSpinner = dialogView.findViewById(R.id.dropdown_spinner);

        // This would be members
        String[] items = new String[]{"Member 1", "Member 2", "Member 3"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownSpinner.setAdapter(adapter);

        EditText inputField = dialogView.findViewById(R.id.input_field);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
            .setTitle("Create a Group")
            .setPositiveButton("OK", (dialog, id) -> {
                String inputText = inputField.getText().toString();
                String selectedOption = dropdownSpinner.getSelectedItem().toString();

                createGroup(inputText);
            })
            .setNegativeButton("Cancel", (dialog, id) -> {
                dialog.dismiss();
            });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void fetchUserData(String userID) {
        // Reference to the user document
        DocumentReference userRef = db.collection("users").document(userID);

        // Fetch the document
        userRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e){
                items.clear();
                items.addAll((ArrayList<String>)documentSnapshot.get("groupList"));
                adapter.notifyDataSetChanged();
            }
        });
    }

    Boolean createdGroup = true;

    private void createGroup(String groupName){
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
                fetchUserData(user.getUid());
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
                transaction.update(groupDoc, "memberList", newList);
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