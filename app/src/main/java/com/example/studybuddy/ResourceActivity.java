package com.example.studybuddy;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class ResourceActivity extends AppCompatActivity {

    private Uri fileUri;
    private AlertDialog dialog;
    private ListView resourceLV;
    private ArrayList<String> resourceNames;
    private ArrayAdapter<String> arrayAdapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.resource);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button createGroupButton = findViewById(R.id.uploadResourceButton);
        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog();
            }
        });

        resourceLV = findViewById(R.id.resourceList);
        resourceNames = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, resourceNames);
        resourceLV.setAdapter(arrayAdapter);

        resourceNames.add("Sample Resource");
        arrayAdapter.notifyDataSetChanged();

        resourceLV.setOnItemClickListener((parent, view, position, id) -> {
            String resourceName = resourceNames.get(position);
            downloadResource(resourceName);
        });

        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                arrayAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                arrayAdapter.getFilter().filter(newText);
                return false;
            }
        });

    }

    private void downloadResource(String resourceName) {

        // do Firebase fetch here to get the resources
        Uri fileUri = Uri.parse("your_file_uri_here");

        new AlertDialog.Builder(this)
            .setTitle("Download Resource")
            .setMessage("Do you want to download " + resourceName + "?")
            .setPositiveButton("Download", (dialog, which) -> {
                // downloadFile(fileUri);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }


    private void createDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.resource_dialog, null);
        builder.setView(dialogView);

        dialog = builder.create();
        dialog.show();

        EditText fileNameEditText = dialogView.findViewById(R.id.editTextFileName);
        EditText descriptionEditText = dialogView.findViewById(R.id.editTextDescription);
        EditText categoryEditText = dialogView.findViewById(R.id.editTextCategory);
        Button chooseFileButton = dialogView.findViewById(R.id.buttonChooseFile);
        TextView fileNameTextView = dialogView.findViewById(R.id.textViewFileName);
        Button uploadButton = dialogView.findViewById(R.id.buttonUpload);

        chooseFileButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");  // Use "*/*" to allow all file types or specify a type like "application/pdf"
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Select File"), 1);
        });

        uploadButton.setOnClickListener(v -> {
            String fileName = fileNameEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            String category = categoryEditText.getText().toString();

            if (fileUri != null)
            {
                Log.d("ResourceActivity", "File uploaded: " + fileName);
                resourceNames.add(fileName);
                arrayAdapter.notifyDataSetChanged();
                // Firebase implementation here eventually
                Toast.makeText(this, "File uploaded", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please select a file first.", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            fileUri = data.getData();  // Get the URI of the selected file

            if (fileUri != null) {
                // Retrieve the selected file name
                String fileName = getFileName(fileUri);

                // Display the file name in the TextView and hide the "Choose File" button
                TextView fileNameTextView = dialog.findViewById(R.id.textViewFileName);
                Button chooseFileButton = dialog.findViewById(R.id.buttonChooseFile);
                fileNameTextView.setText("Selected File: " + fileName);
                fileNameTextView.setVisibility(View.VISIBLE);
                chooseFileButton.setVisibility(View.GONE);

                // Optional: Show a toast for confirmation
                Toast.makeText(this, "File selected: " + fileName, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
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


}