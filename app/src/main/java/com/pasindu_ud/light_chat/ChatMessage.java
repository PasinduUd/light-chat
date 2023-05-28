package com.pasindu_ud.light_chat;

public class ChatMessage {
    public static String SENT = "sent";
    public static String RECEIVED = "received";

    private final String message;
    private final String status;

    public ChatMessage(String message, String status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
}
