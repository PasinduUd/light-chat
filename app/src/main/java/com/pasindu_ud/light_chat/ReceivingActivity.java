package com.pasindu_ud.light_chat;

import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_SIMPLE;
import static org.opencv.imgproc.Imgproc.COLOR_RGBA2GRAY;
import static org.opencv.imgproc.Imgproc.RETR_TREE;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.contourArea;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.findContours;
import static org.opencv.imgproc.Imgproc.threshold;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.slider.Slider;

import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReceivingActivity extends CameraActivity {
    CameraBridgeViewBase cameraBridgeViewBase;
    TextView receivedMessage;
    private int binaryThreshold;
    private int contourAreaThreshold;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receiving_activity);
        this.getCameraPermission();

        Slider thresholdSlider = findViewById(R.id.thresholdSlider);
        Slider contourAreaSlider = findViewById(R.id.contourAreaSlider);
        cameraBridgeViewBase = findViewById(R.id.cameraView);
        receivedMessage = findViewById(R.id.receivedMessage);

        int defaultBinaryThreshold = 100;
        int defaultContourArea = 300000;
        thresholdSlider.setValue(defaultBinaryThreshold);
        contourAreaSlider.setValue(defaultContourArea);
        this.binaryThreshold = defaultBinaryThreshold;
        this.contourAreaThreshold = defaultContourArea;

        thresholdSlider.addOnChangeListener((slider, value, fromUser) -> binaryThreshold = (int) value);
        contourAreaSlider.addOnChangeListener((slider, value, fromUser) -> contourAreaThreshold = (int) value);

        cameraBridgeViewBase.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2() {
            private final StringBuilder morseCodeBuilder = new StringBuilder();
            private final MorseCodeHandler morseCodeHandler = new MorseCodeHandler();
            private boolean isFlashlightOn = false;
            private int brightFrameCount = 0;
            private int dimFrameCount = 0;
            private int spaceCount = 0;
            private List<String> words = new ArrayList<>();
            private boolean isStartDetected = true;

            @Override
            public void onCameraViewStarted(int width, int height) {
            }

            @Override
            public void onCameraViewStopped() {
            }

            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

                Mat gray = new Mat();
                cvtColor(inputFrame.rgba(), gray, COLOR_RGBA2GRAY); // Convert the input frame to grayscale

                Mat binary = new Mat();
                threshold(gray, binary, binaryThreshold, 255, THRESH_BINARY); // Convert the grayscale image to binary
                List<MatOfPoint> contours = new ArrayList<>();

                Mat hierarchy = new Mat();
                findContours(binary, contours, hierarchy, RETR_TREE, CHAIN_APPROX_SIMPLE); // Find contours in the binary image

                if (!contours.isEmpty()) {
                    MatOfPoint contour = contours.get(0);
                    double area = contourArea(contour);
                    Log.d("CONTOUR_AREA", "Contour Area : " + area);
                    if (area > contourAreaThreshold) { // Check if the area of the contour exceeds a threshold (flashlight beam size)
                        brightFrameCount++;
                        isFlashlightOn = true;
                        dimFrameCount = 0;
                        spaceCount = 0;
                    } else {
                        if (isFlashlightOn) {
                            if (brightFrameCount >= 3) morseCodeBuilder.append("-");
                            else if (brightFrameCount >= 1) morseCodeBuilder.append(".");
                        } else {
                            if (!morseCodeBuilder.toString().equals("")) {
                                dimFrameCount++;
                                if (dimFrameCount >= 8) {
                                    spaceCount++;
                                    if (spaceCount >= 26) {
                                        morseCodeBuilder.append('_');
                                        spaceCount = 0;
                                    } else if (spaceCount == 1 && !morseCodeBuilder.toString().endsWith("_")) {
                                        morseCodeBuilder.append(' ');

                                        Log.d("MORSE_CODE_LENGTH", "Message : " + morseCodeBuilder + ", Length : " + morseCodeBuilder.length());
                                        String morseCodeBuilderString = morseCodeBuilder.toString();

                                        // Check if the Morse code message initiation failed
                                        if (morseCodeBuilderString.length() < 6) {
                                            // Display a warning dialog if the length of the Morse code is less than 6 characters
                                            runOnUiThread(() -> {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(ReceivingActivity.this);
                                                builder.setTitle("Warning !")
                                                        .setMessage("Message Initiation Failed.")
                                                        .setPositiveButton("BACK TO CHAT", (dialog, which) -> stopReceiving())
                                                        .setCancelable(false)
                                                        .show();
                                            });
                                            isStartDetected = false;
                                            return binary;
                                        }

                                        if (!isStartDetected)
                                            return binary; // Restrict further processing

                                        // Decode the Morse code message
                                        String[] characters = morseCodeBuilderString.substring(6).split(" ");
                                        List<String> decodedCharacters = new ArrayList<>();
                                        for (String character : characters) {
                                            if (character.contains("_")) {
                                                decodedCharacters.add(" ");
                                            }
                                            decodedCharacters.add(this.morseCodeHandler.decodeMessage(character.replaceAll("_", " ")));
                                        }
                                        runOnUiThread(() -> receivedMessage.setText(String.join("", decodedCharacters)));
                                    }
                                }
                            }
                        }
                        Log.d("ILLUMINATED_FRAMES", "Illuminated Frame Count : " + brightFrameCount);
                        brightFrameCount = 0;
                        isFlashlightOn = false;
                    }
                }

                if (!isFlashlightOn) {
                    String morseCode = morseCodeBuilder.toString().trim();

                    if (morseCode.endsWith(" .-.-")) {
                        if (morseCode.contains("-.-.-")) {
                            morseCode = morseCode.substring(6, morseCode.length() - 5);
                            String[] morseCodeList = morseCode.split("_");
                            for (String morseCodeWord : morseCodeList) {
                                words.add(this.morseCodeHandler.decodeMessage(morseCodeWord));
                            }
                            String message = String.join(" ", words);
                            Log.d("MORSE_CODED_MESSAGE", "Message: " + message);
                            runOnUiThread(() -> receivedMessage.setText(message));
                            stopReceiving(); // Successfully received
                        }
                        morseCodeBuilder.setLength(0);
                        words = new ArrayList<>();
                    }
                }
                return binary;
            }
        });

        if (OpenCVLoader.initDebug()) {
            Log.d("LOADED_OPENCV", "Successful");
            cameraBridgeViewBase.enableView();
        } else {
            Log.d("LOADED_OPENCV", "Unsuccessful");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cameraBridgeViewBase != null) cameraBridgeViewBase.enableView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraBridgeViewBase != null) cameraBridgeViewBase.disableView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraBridgeViewBase != null) cameraBridgeViewBase.disableView();
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(cameraBridgeViewBase);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 3 && grantResults.length > 0) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) getCameraPermission();
        }
    }

    void getCameraPermission() {
        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 3);
        }
    }

    private void stopReceiving() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("received_message", this.receivedMessage.getText());
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @SuppressLint("SetTextI18n")
    public void onStopReceivingButtonClick(View view) {
        this.stopReceiving();
    }

    public void onBackToChatButtonClick(View view) {
        finish();
    }
}
