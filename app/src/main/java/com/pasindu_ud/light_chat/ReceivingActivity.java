package com.pasindu_ud.light_chat;

import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_SIMPLE;
import static org.opencv.imgproc.Imgproc.COLOR_RGBA2GRAY;
import static org.opencv.imgproc.Imgproc.RETR_TREE;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.contourArea;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.findContours;
import static org.opencv.imgproc.Imgproc.resize;
import static org.opencv.imgproc.Imgproc.threshold;
import static org.opencv.imgproc.Imgproc.drawContours;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ReceivingActivity extends CameraActivity {
    CameraBridgeViewBase cameraBridgeViewBase;
    Button button;
    TextView labelField;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receiving_activity);

        getPermission();

        cameraBridgeViewBase = findViewById(R.id.cameraView);
        button = findViewById(R.id.backToChat);
        labelField = findViewById(R.id.receivedMessage);

        cameraBridgeViewBase.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2() {
            private boolean isFlashlightOn = false;
            private long prevTime = 0;
            private int illuminatedFrameCount = 0;
            private int darkFrameCount = 0;
            private final StringBuilder morseCodeBuilder = new StringBuilder();
            private int spaceCount = 0;
            private List<String> morseCodeList = new ArrayList<>();
            private List<String> wordList = new ArrayList<>();
            private static final long DOT_DURATION = 40;
            private final MorseCodeHandler morseCodeHandler = new MorseCodeHandler();
            @Override
            public void onCameraViewStarted(int width, int height) {
            }

            @Override
            public void onCameraViewStopped() {
            }

            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
//                Mat resizedFrame = new Mat();
//                resize(inputFrame.rgba(), resizedFrame, new Size(inputFrame.rgba().cols() / 2, inputFrame.rgba().rows() / 2));

                // Convert input frame to grayscale
                Mat gray = new Mat();
//                cvtColor(resizedFrame, gray, COLOR_RGBA2GRAY);
                cvtColor(inputFrame.rgba(), gray, COLOR_RGBA2GRAY);

                // Apply threshold to convert to binary image
                Mat binary = new Mat();
                threshold(gray, binary, 100, 255, THRESH_BINARY);
//                double brightness = Core.mean(binary).val[0];
//                if (brightness > 120 ) Log.d("Brightness","brightness " + brightness);
                // Find contours in the binary image
                List<MatOfPoint> contours = new ArrayList<>();

                Mat hierarchy = new Mat();
                findContours(binary, contours, hierarchy, RETR_TREE, CHAIN_APPROX_SIMPLE);
//                Mat outputFrame = binary.clone();
//
//                // Draw contours on the output frame
//                drawContours(outputFrame, contours, -1, new Scalar(0, 255, 0), 2);
//
//                // ...
//
//                // Return the output frame
//                return outputFrame;
                long currentTime = System.currentTimeMillis();

                // Iterate through all the detected contours
                if (!contours.isEmpty()) {
                    MatOfPoint contour = contours.get(0);
                    double area = contourArea(contour);
                    Log.d("AreaCont", "Area: " + area);
                    if (area > 300000) { // Adjust this threshold based on flashlight beam size
                        Log.d("AreaContMore", "AreaMore: " + area);
                        illuminatedFrameCount++;
                        isFlashlightOn = true;
                        darkFrameCount = 0;
                        spaceCount = 0;
                    } else {
                        if (isFlashlightOn) {
                            if (illuminatedFrameCount >= 3) {
                                morseCodeBuilder.append('-');
                            } else if (illuminatedFrameCount >= 1) {
                                morseCodeBuilder.append('.');
                            }
                        } else {
                            if (!morseCodeBuilder.toString().equals("")) {
                                darkFrameCount++;
                                if (darkFrameCount >= 8) {
                                    spaceCount++;
                                    if (spaceCount >= 26) {
                                        morseCodeBuilder.append('_');
                                        spaceCount = 0;
                                    } else if (spaceCount == 1 && !morseCodeBuilder.toString().endsWith("_")) {
                                        morseCodeBuilder.append(' ');
                                        Log.d("MorseCodeBuilderLen", "Message: " + morseCodeBuilder.toString() + " Length: " +  morseCodeBuilder.length());
                                        String[] characterList = morseCodeBuilder.toString().substring(6, morseCodeBuilder.length()).split(" ");
//                                        String[] characterList = morseCodeBuilder.toString().substring(0, morseCodeBuilder.length()).split(" ");
                                        List<String> decodedCharacterList = new ArrayList<>();
                                        for (String character : characterList) {
                                            if (character.contains("_")) {
                                                decodedCharacterList.add(" ");
                                            }
                                            decodedCharacterList.add(this.morseCodeHandler.decodeMessage(character.replaceAll("_", " ")));
                                        }
                                        labelField.setText(String.join("", decodedCharacterList));
                                    }
                                }
                            }
                        }
                        if (illuminatedFrameCount > 0) {
                            Log.d("Illuminated", "Illu " + illuminatedFrameCount);
                        }
                        if (morseCodeBuilder.toString().length() == 1) {
                            prevTime = currentTime;
                        }
                        illuminatedFrameCount = 0;
                        isFlashlightOn = false;
                    }
                }

                if (prevTime == 0) {
                    prevTime = currentTime;
                }

                if (!isFlashlightOn) {
                    // Decode Morse code
                    String morseCode = morseCodeBuilder.toString().trim();

                    if (morseCode.endsWith(" .-.-")) {
                        if (morseCode.contains("-.-.-")) {
                            morseCode = morseCode.substring(6, morseCode.length() - 5);
                            morseCodeList = Arrays.asList(morseCode.split("_"));
                            for (String morseCodeWord : morseCodeList) {
                                wordList.add(this.morseCodeHandler.decodeMessage(morseCodeWord));
                            }
                            String message = String.join(" ", wordList);
                            Log.d("MorseCode", "Message: " + message);
                            labelField.setText(message);
                        }
//                        else {
//                            labelField.setText("");
//                        }
                        morseCodeBuilder.setLength(0);
                        wordList = new ArrayList<>();
                        morseCodeList = new ArrayList<>();
                        prevTime = 0;
                    }
                }

                return binary;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReceivingActivity.this, SendingActivity.class);
                startActivity(intent);
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
        if (cameraBridgeViewBase != null) {
            cameraBridgeViewBase.enableView();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraBridgeViewBase != null) {
            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraBridgeViewBase != null) {
            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(cameraBridgeViewBase);

    }

    void getPermission() {
        if(checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 3);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==3 && grantResults.length > 0) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                getPermission();
            }
        }
    }
}
