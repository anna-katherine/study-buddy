package com.example.studybuddy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {

    boolean[] checkedGroups;
    String[] groupList = {"Group 1", "Group 2", "Group 3"};
    FirebaseAuth auth;
    FirebaseUser user;

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

        ListView lv = findViewById(R.id.groupList);
        ArrayList<String> items = new ArrayList<>();
        items.add("Group 1");
        items.add("Group 2");
        items.add("Group 3");
        items.add("Group 4");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Intent intent = new Intent(DashboardActivity.this, StudyGroupActivity.class);
                startActivity(intent);
            }
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
                // Create this in the backend and add user
            })
            .setNegativeButton("Cancel", (dialog, id) -> {
                dialog.dismiss();
            });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }
}