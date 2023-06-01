package com.pasindu_ud.light_chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SendingActivity extends AppCompatActivity {
    private final double defaultDotDuration = 100;
    private final MorseCodeHandler morseCodeHandler = new MorseCodeHandler();

    private RecyclerView chatContainer;
    private TextView welcomeMessage;
    private List<ChatMessage> chatMessages;
    private ChatMessageAdapter chatMessageAdapter;
    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sending_activity);

        TextInputEditText dotDurationInput = findViewById(R.id.dotDurationInput);
        dotDurationInput.setText(String.valueOf(defaultDotDuration));

        this.chatContainer = findViewById(R.id.chatContainer);
        this.welcomeMessage = findViewById(R.id.welcomeMessage);
        this.chatMessages = new ArrayList<>();

        // Setup recycler view
        this.chatMessageAdapter = new ChatMessageAdapter(this.chatMessages);
        this.chatContainer.setAdapter(this.chatMessageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        this.chatContainer.setLayoutManager(linearLayoutManager);
    }

    public void onSendButtonClick(View view) {
        TextInputEditText messageInput = findViewById(R.id.messageInput);
        String message = Objects.requireNonNull(messageInput.getText()).toString().trim();
        TextInputEditText dotDurationInput = findViewById(R.id.dotDurationInput);
        String dotDuration = Objects.requireNonNull(dotDurationInput.getText()).toString().trim();

        if (dotDuration.isEmpty()) {
            dotDuration = String.valueOf(defaultDotDuration);
            dotDurationInput.setText(dotDuration);
        }

        if (!message.isEmpty()) {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                String encodedMessage = morseCodeHandler.encodeMessage(message);
                try {
                    FlashLightHandler flashLightHandler = new FlashLightHandler((CameraManager) getSystemService(Context.CAMERA_SERVICE));
                    flashLightHandler.transmitEncodedMessage(encodedMessage, (long) Double.parseDouble(dotDuration));
                    dotDurationInput.setText(dotDuration);
                    this.addMessageToChatContainer(message, ChatMessage.SENT);
                    this.welcomeMessage.setVisibility(View.GONE);
                } catch (CameraAccessException e) {
                    Toast.makeText(SendingActivity.this, "Camera is not available.", Toast.LENGTH_LONG).show();
                } catch (InterruptedException e) {
                    Toast.makeText(SendingActivity.this, "Restart the application.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(SendingActivity.this, "Camera flash is not available.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onReceiveButtonClick(View view) {
        Intent receivingIntent = new Intent(this, ReceivingActivity.class);
//        startActivity(receivingIntent);
        startActivityForResult(receivingIntent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // Get the data passed from the current intent
            String passedMessage = data.getStringExtra("received_message");
            if ((!passedMessage.isEmpty()) && (!passedMessage.equals("Focus the Camera on the Flashlight."))) {
                if (passedMessage.length() > 1) passedMessage = passedMessage.charAt(0) + passedMessage.substring(1).toLowerCase();
                this.addMessageToChatContainer(passedMessage, ChatMessage.RECEIVED);
                this.welcomeMessage.setVisibility(View.GONE);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addMessageToChatContainer(String message, String status) {
        runOnUiThread(() -> {
            chatMessages.add(new ChatMessage(message, status));
            chatMessageAdapter.notifyDataSetChanged();
            chatContainer.smoothScrollToPosition(chatMessageAdapter.getItemCount());
        });
    }
}