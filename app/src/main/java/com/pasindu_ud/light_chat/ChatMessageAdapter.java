package com.pasindu_ud.light_chat;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ChatViewHolder> {
    List<ChatMessage> messages;

    public ChatMessageAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View chatBubbleView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_bubble, null);
        return new ChatViewHolder(chatBubbleView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage chatMessage = messages.get(position);
        if (chatMessage.getStatus().equals(ChatMessage.SENT)) {
            holder.leftChatBubble.setVisibility(View.GONE);
            holder.rightChatBubble.setVisibility(View.VISIBLE);
            holder.rightChatBubbleMessage.setText(chatMessage.getMessage());
        } else {
            holder.leftChatBubble.setVisibility(View.VISIBLE);
            holder.rightChatBubble.setVisibility(View.GONE);
            holder.leftChatBubbleMessage.setText(chatMessage.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return this.messages.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftChatBubble, rightChatBubble;
        TextView leftChatBubbleMessage, rightChatBubbleMessage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatBubble = itemView.findViewById(R.id.leftChatBubble);
            rightChatBubble = itemView.findViewById(R.id.rightChatBubble);
            leftChatBubbleMessage = itemView.findViewById(R.id.leftChatBubbleMessage);
            rightChatBubbleMessage = itemView.findViewById(R.id.rightChatBubbleMessage);

        }
    }
}
