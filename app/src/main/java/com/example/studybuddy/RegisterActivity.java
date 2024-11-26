package com.example.studybuddy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {

    TextInputEditText editTextUsername, editTextPassword, fName, lName;
    MaterialAutoCompleteTextView courseDropdown;
    Button buttonReg;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    ProgressBar progressBar;

    // Variables for enrolled courses selection @Alex change these if needed
    private String[] courses = {"Course 1: Math", "Course 2: English", "Course 3: History", "Course 4: Human Biology", "Course 5: Data Structures"};
    private boolean[] selectedItems = new boolean[courses.length];
    private ArrayList<String> selectedCourses = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.register);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextUsername = findViewById(R.id.username);
        editTextPassword = findViewById(R.id.password);
        fName = findViewById(R.id.firstname);
        lName = findViewById(R.id.lastname);
        buttonReg = findViewById(R.id.register_button);
        progressBar = findViewById(R.id.progressBar);


        buttonReg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                progressBar.setVisibility(View.VISIBLE);
                String username, password, firstname, lastname;
                username = String.valueOf(editTextUsername.getText()).trim();
                password = String.valueOf(editTextPassword.getText()).trim();
                firstname = String.valueOf(fName.getText());
                lastname = String.valueOf(lName.getText());

                boolean hasError = false;

                if (TextUtils.isEmpty(username)) {
                    editTextUsername.setError("Enter username");
                    hasError = true;
                }
                if (TextUtils.isEmpty(password)) {
                    editTextPassword.setError("Enter password");
                    hasError = true;
                }
                if (TextUtils.isEmpty(firstname)) {
                    fName.setError("Enter first name");
                    hasError = true;
                }
                if (TextUtils.isEmpty(lastname)) {
                    lName.setError("Enter last name");
                    hasError = true;
                }

                if (hasError) {
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.createUserWithEmailAndPassword(username, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    String displayName = "";
                                    if (user != null) {
                                        displayName = String.valueOf(fName.getText());
                                        char lastInit = String.valueOf(lName.getText()).toUpperCase().charAt(0);
                                        displayName += " " + lastInit;
                                        UserProfileChangeRequest updateName = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(displayName).build();
                                        String finalDisplayName = displayName;
                                        user.updateProfile(updateName)
                                                .addOnCompleteListener(task2 -> {
                                                    if (task2.isSuccessful()) {
                                                        Log.d("Firebase", "Display name updated in Firebase Auth");

                                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                        DocumentReference userRef = db.collection("users").document(user.getUid());

                                                        Map<String, Object> userData = new HashMap<>();
                                                        userData.put("displayName", finalDisplayName);

                                                        userRef.update(userData)
                                                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Display name updated in Firestore"))
                                                                .addOnFailureListener(e -> Log.w("FirestoreError", "Error updating display name in Firestore", e));
                                                    } else {
                                                        Log.w("FirebaseError", "Error updating display name in Firebase Auth", task2.getException());
                                                    }
                                                });
                                    }

                                    Toast.makeText(RegisterActivity.this, "User created.",
                                            Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(RegisterActivity.this, EnrolledClassesActivity.class);
                                    intent.putExtra("com.example.studybuddy.COURSES", selectedCourses);
                                    startActivity(intent);

                                } else {
                                    if (password.length() < 8) {
                                        editTextPassword.setError("Password must be at least 8 characters");
                                        return;
                                    }
                                    String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                    switch (errorCode) {
                                        case "ERROR_EMAIL_ALREADY_IN_USE":
                                            editTextUsername.setError("Email is already registered");
                                            editTextUsername.requestFocus();
                                            break;
                                        case "ERROR_INVALID_EMAIL":
                                            editTextUsername.setError("Invalid email address");
                                            editTextUsername.requestFocus();
                                            break;
                                        default:
                                            Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                            break;
                                    }


                                }
                            }
                        });
            }
        });
        courseDropdown = findViewById(R.id.courseDropdown);
        courseDropdown.setOnClickListener(v -> showCourseSelection());
    }

    private void showCourseSelection()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select your enrolled courses");

        builder.setMultiChoiceItems(courses, selectedItems, new DialogInterface.OnMultiChoiceClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int index, boolean isChecked) {
                if (isChecked) {
                    selectedCourses.add(courses[index]);
                } else {
                    selectedCourses.remove(courses[index]);
                }
            }
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            courseDropdown.setText(String.join(", ", selectedCourses));
            dialog.dismiss();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}