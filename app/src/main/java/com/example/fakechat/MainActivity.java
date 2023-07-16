package com.example.fakechat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.LinearLayout;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//...
public class MainActivity extends AppCompatActivity {

    private List<Message> messages = new ArrayList<>();
    private RecyclerView chatRecyclerView;
    private MessageAdapter messageAdapter;
    private DatabaseReference messagesRef;

    private String username;  // this can be set from outside

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = getIntent().getStringExtra("username");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        messagesRef = database.getReference("messages");

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter = new MessageAdapter(messages, username);
        chatRecyclerView.setAdapter(messageAdapter);

        final EditText newMessageInput = findViewById(R.id.newMessageInput);
        Button sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String fakeMessage = newMessageInput.getText().toString();
                if (!fakeMessage.isEmpty()) {
                    Context context = view.getContext();
                    LinearLayout layout = new LinearLayout(context);
                    layout.setOrientation(LinearLayout.VERTICAL);

                    final EditText realMessageInput = new EditText(context);
                    realMessageInput.setHint("Enter real message");
                    layout.addView(realMessageInput);

                    final EditText keyInput = new EditText(context);
                    keyInput.setHint("Enter key");
                    layout.addView(keyInput);

                    new AlertDialog.Builder(context)
                            .setTitle("Enter real message and key")
                            .setView(layout)
                            .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String realMessage = realMessageInput.getText().toString();
                                    String key = keyInput.getText().toString();
                                    Message message = new Message(fakeMessage, realMessage, key, username);
                                    DatabaseReference newMessageRef = messagesRef.push();
                                    newMessageRef.setValue(message.toMap());
                                    newMessageInput.setText("");

                                    // Add message to the list and notify the adapter
                                    messages.add(message);
                                    messageAdapter.notifyDataSetChanged();

                                    // Scroll to the last message
                                    chatRecyclerView.scrollToPosition(messages.size() - 1);
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            }
        });


        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messages.clear();
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    if (messageSnapshot.hasChild("fakeText") &&
                            messageSnapshot.hasChild("key") &&
                            messageSnapshot.hasChild("author") &&
                            messageSnapshot.hasChild("timestamp") &&
                            messageSnapshot.hasChild("isDecrypted") &&
                            messageSnapshot.hasChild("encryptedText")) {

                        String fakeText = messageSnapshot.child("fakeText").getValue(String.class);
                        String encryptedText = messageSnapshot.child("encryptedText").getValue(String.class);
                        String key = messageSnapshot.child("key").getValue(String.class);
                        String author = messageSnapshot.child("author").getValue(String.class);
                        Date timestamp = messageSnapshot.child("timestamp").getValue(Date.class);
                        Boolean isDecrypted = messageSnapshot.child("isDecrypted").getValue(Boolean.class);

                        Message message = new Message(fakeText, "", key, author);  // Replace "" with your actual real text if available
                        message.setTimestamp(timestamp);
                        message.setDecrypted(isDecrypted);
                        message.setEncryptedText(encryptedText); // Add this line
                        messages.add(message);
                    }
                }
                messageAdapter.notifyDataSetChanged();

                // Scroll to the bottom of the RecyclerView
                if (messages.size() > 0) {
                    chatRecyclerView.scrollToPosition(messages.size() - 1);
                }
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("MainActivity", "loadMessages:onCancelled", databaseError.toException());
            }
        });

    }
}
//...
