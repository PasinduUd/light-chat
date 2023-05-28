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
        morseCodeSignals.put("A", ".-");
        morseCodeSignals.put("B", "-...");
        morseCodeSignals.put("C", "-.-.");
        morseCodeSignals.put("D", "-..");
        morseCodeSignals.put("E", ".");
        morseCodeSignals.put("F", "..-.");
        morseCodeSignals.put("G", "--.");
        morseCodeSignals.put("H", "....");
        morseCodeSignals.put("I", "..");
        morseCodeSignals.put("J", ".---");
        morseCodeSignals.put("K", "-.-");
        morseCodeSignals.put("L", ".-..");
        morseCodeSignals.put("M", "--");
        morseCodeSignals.put("N", "-.");
        morseCodeSignals.put("O", "---");
        morseCodeSignals.put("P", ".--.");
        morseCodeSignals.put("Q", "--.-");
        morseCodeSignals.put("R", ".-.");
        morseCodeSignals.put("S", "...");
        morseCodeSignals.put("T", "-");
        morseCodeSignals.put("U", "..-");
        morseCodeSignals.put("V", "...-");
        morseCodeSignals.put("W", ".--");
        morseCodeSignals.put("X", "-..-");
        morseCodeSignals.put("Y", "-.--");
        morseCodeSignals.put("Z", "--..");
        morseCodeSignals.put("0", "-----");
        morseCodeSignals.put("1", ".----");
        morseCodeSignals.put("2", "..---");
        morseCodeSignals.put("3", "...--");
        morseCodeSignals.put("4", "....-");
        morseCodeSignals.put("5", ".....");
        morseCodeSignals.put("6", "-....");
        morseCodeSignals.put("7", "--...");
        morseCodeSignals.put("8", "---..");
        morseCodeSignals.put("9", "----.");
        morseCodeSignals.put("?", "..--..");
        morseCodeSignals.put("!", "-.-.--");
        morseCodeSignals.put(".", ".-.-.-");
        morseCodeSignals.put(",", "--..--");
        morseCodeSignals.put(";", "-.-.-.");
        morseCodeSignals.put(":", "---...");
        morseCodeSignals.put("+", ".-.-.");
        morseCodeSignals.put("-", "-....-");
        morseCodeSignals.put("/", "-..-.");
        morseCodeSignals.put("=", "-...-");
        morseCodeSignals.put(" ", " ");
        morseCodeSignals.put("<start>", "...... ");
        morseCodeSignals.put("<end>", " ......");
    }

    public String encodeMessage(String message) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(this.morseCodeSignals.get("<start>"));
        message = message.trim().toUpperCase();
        for (int i = 0; i < message.length(); i++) {
            String character = Character.toString(message.charAt(i));
            if (this.morseCodeSignals.containsKey(character)){
                messageBuilder.append(this.morseCodeSignals.get(character));
                if (i != message.length() - 1) {
                    messageBuilder.append(" ");
                }
            }
        }
        messageBuilder.append(this.morseCodeSignals.get("<end>"));
        Log.d("EncodedMessage", "Encoded Message: " + messageBuilder);
        return messageBuilder.toString();
    }
}
