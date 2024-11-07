package com.example.studybuddy;

import android.net.Uri;

public class Resource {

    private String title;
    private Uri fileUri;

    public Resource(String title, Uri fileUri) {
        this.title = title;
        this.fileUri = fileUri;
    }

    public String getTitle() {
        return title;
    }

    public Uri getFileUri() {
        return fileUri;
    }
}
