package com.example.studybuddy;

import android.net.Uri;

public class ResourceData {

    private String title;
    private Uri fileUri;

    public ResourceData(String title, Uri fileUri) {
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
