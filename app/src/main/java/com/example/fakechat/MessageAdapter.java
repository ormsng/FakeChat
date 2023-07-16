package com.example.fakechat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<Message> messages;
    private String myName;  // this can be set from outside

    public MessageAdapter(List<Message> messages, String myName) { // updated constructor
        this.messages = messages;
        this.myName = myName; // setting the name from the MainActivity
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View messageView = inflater.inflate(R.layout.message_item, parent, false);

        return new ViewHolder(messageView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = messages.get(position);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.messageCardView.getLayoutParams();
        if (message.getAuthor().equals(myName)) {
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
        } else {
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }
        holder.messageCardView.setLayoutParams(params);

        TextView textView = holder.textView;
        TextView authorView = holder.authorView;
        TextView timestampView = holder.timestampView;
        Button decryptButton = holder.decryptButton;

        // Always show fakeText
        textView.setText(message.getFakeText());
        authorView.setText(message.getAuthor());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String formattedDate = formatter.format(message.getTimestamp());
        timestampView.setText(formattedDate);

        if (!message.isDecrypted()) {
            decryptButton.setVisibility(View.VISIBLE);
            decryptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    EditText keyInput = new EditText(context);
                    new AlertDialog.Builder(context)
                            .setTitle("Enter key to decrypt message")
                            .setView(keyInput)
                            .setPositiveButton("Decrypt", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String inputKey = keyInput.getText().toString();
                                    String decryptedText = message.decrypt(inputKey);
                                    if (decryptedText != null) {
                                        // Show decrypted text after decryption
                                        textView.setText(decryptedText);
                                        decryptButton.setVisibility(View.GONE);
                                    } else {
                                        new AlertDialog.Builder(context)
                                                .setTitle("Incorrect Key")
                                                .setMessage("The key you entered is incorrect.")
                                                .setPositiveButton("Ok", null)
                                                .show();
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            });
        } else {
            decryptButton.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CardView messageCardView;
        public TextView textView;
        public TextView authorView;
        public TextView timestampView;
        public Button decryptButton;

        public ViewHolder(View itemView) {
            super(itemView);

            messageCardView = itemView.findViewById(R.id.messageCardView);
            textView = itemView.findViewById(R.id.messageTextView);
            authorView = itemView.findViewById(R.id.messageAuthorView);
            timestampView = itemView.findViewById(R.id.messageTimestampView);
            decryptButton = itemView.findViewById(R.id.messageDecryptButton);
        }
    }
}
