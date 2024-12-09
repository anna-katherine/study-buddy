package com.example.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText editTextUsername, editTextPassword;
    Button loginButton;
    Button registerButton;
    ProgressBar progressBar;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        editTextUsername = findViewById(R.id.username);
        editTextPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);

        loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String username, password;
                username = String.valueOf(editTextUsername.getText()).trim();
                password = String.valueOf(editTextPassword.getText()).trim();

//                Log.d("LoginActivity", "Username: '" + username + "', Password: '" + password + "'");

                if (TextUtils.isEmpty(username)){
                    progressBar.setVisibility(View.GONE);
                    editTextUsername.setError("Enter username");
                    editTextUsername.requestFocus();
                    Toast.makeText(LoginActivity.this, "Enter username", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    progressBar.setVisibility(View.GONE);
                    editTextPassword.setError("Enter password");
                    editTextPassword.requestFocus();
                    Toast.makeText(LoginActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(username, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    String userID = mAuth.getCurrentUser().getUid();
                                    DocumentReference userDoc = db.collection("users").document(userID);
                                    userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document != null && document.exists()) {
                                                    Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(LoginActivity.this, EnrolledClassesActivity.class);
                                                    intent.putExtra("com.example.studybuddy.COURSES", (ArrayList<String>) document.get("classList"));
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }
                                        }
                                    });
                                } else {
                                    // If sign in fails, display a message to the user.
                                    editTextUsername.setError("Login failed");
                                    editTextPassword.setError("Login failed");
                                    editTextPassword.requestFocus();
                                    Toast.makeText(LoginActivity.this, "Login failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Move to registration page
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}