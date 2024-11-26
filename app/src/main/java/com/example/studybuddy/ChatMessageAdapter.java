package com.example.studybuddy;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import androidx.annotation.NonNull;
import com.google.firebase.Timestamp;

public class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {

    public ChatMessageAdapter(Context context, List<ChatMessage> messages) {
        super(context, 0, messages);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_layout, parent, false);
        }

        ChatMessage message = getItem(position);

        TextView senderNameTextView = convertView.findViewById(R.id.senderNameTextView);
        TextView messageTextView = convertView.findViewById(R.id.messageTextView);
        TextView timestampTextView = convertView.findViewById(R.id.timestampTextView);

        assert message != null;
        senderNameTextView.setText(message.getSenderName());
        messageTextView.setText(message.getMessageText());

        //Set the date and time of message.
        String date = getDateFromTimestamp(message.getTimestamp());
        timestampTextView.setText(date);

        // Align message based on who sent it
        /*if (message.isFromMe()) {
            messageTextView.setGravity(Gravity.END); // Right-align for messages from you
            senderNameTextView.setGravity(Gravity.END);
        } else {
            messageTextView.setGravity(Gravity.START); // Left-align for messages from others
            senderNameTextView.setGravity(Gravity.START);
        }*/
        messageTextView.setGravity(Gravity.START); // Left-align for messages from others
        senderNameTextView.setGravity(Gravity.START);

        return convertView;
    }

    public static String getDateFromTimestamp(Timestamp t){
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, h:mma");
        String date = sdf.format(t.toDate());
        return date;
    }
}