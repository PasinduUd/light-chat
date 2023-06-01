package com.pasindu_ud.light_chat;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;

public class FlashLightHandler {
    private final CameraManager cameraManager;

    public FlashLightHandler(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    private void turnOffFlashlight() throws CameraAccessException {
        String rearCameraId = cameraManager.getCameraIdList()[0];
        cameraManager.setTorchMode(rearCameraId, false);
    }

    private void turnOnFlashlight(long duration) throws CameraAccessException, InterruptedException {
        String rearCameraId = cameraManager.getCameraIdList()[0];
        cameraManager.setTorchMode(rearCameraId, true);
        Thread.sleep(duration);
        this.turnOffFlashlight();
    }

    public void transmitEncodedMessage(String encodedMessage, long dotDuration) throws CameraAccessException, InterruptedException {
        for (int i = 0; i < encodedMessage.length(); i++) {
            String character = Character.toString(encodedMessage.charAt(i));
            switch (character) {
                case ".":
                    this.turnOnFlashlight(dotDuration);
                    break;
                case "-":
                    this.turnOnFlashlight(dotDuration * 3);
                    break;
                case " ":
                    Thread.sleep(dotDuration * 7);
                    break;
            }
            Thread.sleep(dotDuration * 3);  // Inter-character gap
        }
    }
}
