package com.pasindu_ud.light_chat;

import android.util.Log;

import java.util.HashMap;

public class MorseCodeHandler {

    private HashMap<String, String> morseCodeSignals;

    public MorseCodeHandler() {
        this.buildMorseCodeSignals();
    }

    private void buildMorseCodeSignals() {
        this.morseCodeSignals = new HashMap<>();
        this.morseCodeSignals.put("A", ".-");
        this.morseCodeSignals.put("B", "-...");
        this.morseCodeSignals.put("C", "-.-.");
        this.morseCodeSignals.put("D", "-..");
        this.morseCodeSignals.put("E", ".");
        this.morseCodeSignals.put("F", "..-.");
        this.morseCodeSignals.put("G", "--.");
        this.morseCodeSignals.put("H", "....");
        this.morseCodeSignals.put("I", "..");
        this.morseCodeSignals.put("J", ".---");
        this.morseCodeSignals.put("K", "-.-");
        this.morseCodeSignals.put("L", ".-..");
        this.morseCodeSignals.put("M", "--");
        this.morseCodeSignals.put("N", "-.");
        this.morseCodeSignals.put("O", "---");
        this.morseCodeSignals.put("P", ".--.");
        this.morseCodeSignals.put("Q", "--.-");
        this.morseCodeSignals.put("R", ".-.");
        this.morseCodeSignals.put("S", "...");
        this.morseCodeSignals.put("T", "-");
        this.morseCodeSignals.put("U", "..-");
        this.morseCodeSignals.put("V", "...-");
        this.morseCodeSignals.put("W", ".--");
        this.morseCodeSignals.put("X", "-..-");
        this.morseCodeSignals.put("Y", "-.--");
        this.morseCodeSignals.put("Z", "--..");
        this.morseCodeSignals.put("0", "-----");
        this.morseCodeSignals.put("1", ".----");
        this.morseCodeSignals.put("2", "..---");
        this.morseCodeSignals.put("3", "...--");
        this.morseCodeSignals.put("4", "....-");
        this.morseCodeSignals.put("5", ".....");
        this.morseCodeSignals.put("6", "-....");
        this.morseCodeSignals.put("7", "--...");
        this.morseCodeSignals.put("8", "---..");
        this.morseCodeSignals.put("9", "----.");
        this.morseCodeSignals.put(" ", " ");
        this.morseCodeSignals.put("<start>", "-.-.- ");
        this.morseCodeSignals.put("<end>", " .-.-");
    }

    public String encodeMessage(String message) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(this.morseCodeSignals.get("<start>"));
        message = message.trim().toUpperCase();
        for (int i = 0; i < message.length(); i++) {
            String character = Character.toString(message.charAt(i));
            if (this.morseCodeSignals.containsKey(character)) {
                messageBuilder.append(this.morseCodeSignals.get(character));
                if (i != message.length() - 1) {
                    messageBuilder.append(" ");
                }
            }
        }
        messageBuilder.append(this.morseCodeSignals.get("<end>"));
        Log.d("MESSAGE", "Message: " + messageBuilder);
        Log.d("ENCODED_MESSAGE", "Encoded Message: " + messageBuilder);
        return messageBuilder.toString();
    }

    public String decodeMessage(String encodedMessage) {
        StringBuilder messageBuilder = new StringBuilder();
        String[] morseCharacters = encodedMessage.split(" ");

        for (String morseCharacter : morseCharacters) {
            if (this.morseCodeSignals.containsValue(morseCharacter)) {
                messageBuilder.append(getKeyByValue(this.morseCodeSignals, morseCharacter));
            }
        }
        String decodedMessage = messageBuilder.toString();
        Log.d("RECEIVED_MESSAGE", "Received Message: " + encodedMessage);
        Log.d("DECODED_MESSAGE", "Decoded Message: " + decodedMessage);
        return decodedMessage;
    }

    private <K, V> K getKeyByValue(HashMap<K, V> map, V value) {
        for (HashMap.Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
