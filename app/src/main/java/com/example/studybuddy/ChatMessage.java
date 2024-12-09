package com.example.studybuddy;

import com.google.firebase.Timestamp;

import java.util.HashMap;

public class ChatMessage {
    String messageText;
    Timestamp timestamp;
    String senderName;
    boolean isFromMe; // Flag to indicate if the message is from you

    public ChatMessage(String messageText, Timestamp timestamp, String senderName, boolean isFromMe) {
        this.messageText = messageText;
        this.timestamp = timestamp;
        this.senderName = senderName;
        this.isFromMe = isFromMe;
    }

    public String getMessageText() {
        return messageText;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getSenderName() {
        return senderName;
    }

    private HashMap<String, Object> fileInfo;

    public void setFileInfo(HashMap<String, Object> fileInfo) {
        this.fileInfo = fileInfo;
    }

    public HashMap<String, Object> getFileInfo() {
        return fileInfo;
    }

    public boolean isFromMe() {
        return isFromMe;
    }

    public void setMessage(String message) {
        this.messageText = message;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}