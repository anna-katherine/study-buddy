package com.example.studybuddy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
    private String[] courses = {"Course 1: Math", "Course 2: English", "Course 3: History", "Course 4: Biology", "Course 5: Data Structures"};
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
                username = String.valueOf(editTextUsername.getText());
                password = String.valueOf(editTextPassword.getText());
                firstname = String.valueOf(fName.getText());
                lastname = String.valueOf(lName.getText());

                if (TextUtils.isEmpty(username)){
                    Toast.makeText(RegisterActivity.this, "Enter username", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(firstname)){
                    Toast.makeText(RegisterActivity.this, "Enter first name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(lastname)){
                    Toast.makeText(RegisterActivity.this, "Enter first name", Toast.LENGTH_SHORT).show();
                    return;
                }


                mAuth.createUserWithEmailAndPassword(username, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {

                                    //Display name will now be email name before '@' character
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null){
                                        String displayName = String.valueOf(fName.getText());
                                        char lastInit = String.valueOf(lName.getText()).toUpperCase().charAt(0);
                                        displayName += " " + lastInit;
                                        UserProfileChangeRequest updateName = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(displayName).build();
                                        user.updateProfile(updateName);
                                    }

                                    Toast.makeText(RegisterActivity.this, "User created.",
                                            Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(RegisterActivity.this, EnrolledClassesActivity.class);
                                    intent.putExtra("com.example.studybuddy.COURSES", selectedCourses);
                                    startActivity(intent);

                                } else {
                                    if (password.length() >= 8){
                                        Toast.makeText(RegisterActivity.this, "Registration failed. Invalid email address.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(RegisterActivity.this, "Password must be at least 8 characters.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }
        });

        // For the course selection
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