package com.example.studybuddy;

import static com.example.studybuddy.ChatActivity.setupChatLogs;
import static org.junit.Assert.*;

import com.google.firebase.Timestamp;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatActivityTest {

    @org.junit.Test
    public void setupChatLogsIsCorrect(){
        ArrayList<HashMap<String, Object>> logs = new ArrayList<>();
        ArrayList<ChatMessage> expected = new ArrayList<>();
        Timestamp ts = Timestamp.now();
        String testUserID = "Tester4";

        HashMap<String, Object> newChat = new HashMap<>();
        newChat.put("messageText", "Hi1");
        newChat.put("sender", "Tester1");
        newChat.put("timestamp", ts);
        newChat.put("userID", "Tester1");
        ChatMessage message1 = new ChatMessage("Hi1", ts, "Tester1", false);

        HashMap<String, Object> newChat2 = new HashMap<>();
        newChat2.put("messageText", "Hi2");
        newChat2.put("sender", "Tester2");
        newChat2.put("timestamp", ts);
        newChat2.put("userID", "Tester2");
        ChatMessage message2 = new ChatMessage("Hi2", ts, "Tester2", false);

        HashMap<String, Object> newChat3 = new HashMap<>();
        newChat3.put("messageText", "Hi3");
        newChat3.put("sender", "Tester3");
        newChat3.put("timestamp", ts);
        newChat3.put("userID", "Tester3");
        ChatMessage message3 = new ChatMessage("Hi3", ts, "Tester3", false);

        HashMap<String, Object> newChat4 = new HashMap<>();
        newChat4.put("messageText", "Hi4");
        newChat4.put("sender", "Tester4");
        newChat4.put("timestamp", ts);
        newChat4.put("userID", "Tester4");
        ChatMessage message4 = new ChatMessage("Hi4", ts, "Tester4 (You)", true);

        logs.add(newChat);
        logs.add(newChat2);
        logs.add(newChat3);
        logs.add(newChat4);

        expected.add(message1);
        expected.add(message2);
        expected.add(message3);
        expected.add(message4);

        ArrayList<ChatMessage> actual = setupChatLogs(logs, testUserID);

        for (int i = 0; i < (expected.size() + actual.size()) / 2; i++){
            ChatMessage expectedChat = expected.get(i);
            ChatMessage actualChat = actual.get(i);
            String expMessage = expectedChat.getMessageText();
            String actMessage = actualChat.getMessageText();
            String expSender = expectedChat.getSenderName();
            String actSender = actualChat.getSenderName();
            Timestamp expTimestamp = expectedChat.getTimestamp();
            Timestamp actTimestamp = actualChat.getTimestamp();

            assertEquals(expMessage, actMessage);
            assertEquals(expSender, actSender);
            assertEquals(expTimestamp, actTimestamp);
        }
    }
}