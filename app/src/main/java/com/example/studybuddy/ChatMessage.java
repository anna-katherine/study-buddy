package com.example.studybuddy;

public class ChatMessage {
    private String messageText;
    private String timestamp;
    private String senderName;
    private boolean isFromMe; // Flag to indicate if the message is from you

    public ChatMessage(String messageText, String timestamp, String senderName, boolean isFromMe) {
        this.messageText = messageText;
        this.timestamp = timestamp;
        this.senderName = senderName;
        this.isFromMe = isFromMe;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getSenderName() {
        return senderName;
    }

    public boolean isFromMe() {
        return isFromMe;
    }
}