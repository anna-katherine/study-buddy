package com.example.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EnrolledClassesActivity extends AppCompatActivity {
    FirebaseUser user;
    FirebaseFirestore db;
    ArrayList<String> items;
    ArrayAdapter<String> adapter;

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
        DocumentReference userDoc = db.collection("users").document(user.getUid());
        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        //if it exists, no need to create it.
                        fetchUserData(user.getUid());
                    }
                    else {
                        // Group does not exist, create it
                        Intent intent = getIntent();
                        ArrayList<String> courseList = intent.getStringArrayListExtra("com.example.studybuddy.COURSES");
                        Map<String, Object> userInfo = new HashMap<>();
                        userInfo.put("groupList", new ArrayList<>());
                        userInfo.put("displayName", user.getDisplayName());
                        userInfo.put("courseList", courseList);
                        userDoc.set(userInfo);
                        items.clear();
                        items.addAll(courseList);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        // some code here to add the courses to the ListView (this is filler @Alex)
        ListView lv = findViewById(R.id.courseList);
        items = new ArrayList<>();
        items.add("Course 1");
        items.add("Course 2");
        items.add("Course 3");
        items.add("Course 4");

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        lv.setAdapter(adapter);

        // navigate to dashboard page

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                if (item.getItemId() == R.id.dashboard) {
                    startActivity(new Intent(EnrolledClassesActivity.this, DashboardActivity.class));
                } else if (item.getItemId() == R.id.log_out) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(EnrolledClassesActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });

    }
    private void fetchUserData(String userID) {
        // Reference to the user document
        DocumentReference userRef = db.collection("users").document(userID);

        // Fetch the document
        userRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e){
                items.clear();
                items.addAll((ArrayList<String>)documentSnapshot.get("courseList"));
                adapter.notifyDataSetChanged();
            }
        });
    }
}