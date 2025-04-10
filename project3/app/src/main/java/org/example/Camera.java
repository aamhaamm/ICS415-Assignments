package org.example;

public class Camera {
    public float posX, posY, posZ; // Camera position
    public float pitch, yaw;       // Pitch: up/down, Yaw: left/right

    public Camera(float posX, float posY, float posZ, float pitch, float yaw) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    // Compute forward vector (direction camera is looking)
    public float[] getForwardVector() {
        float cosPitch = (float) Math.cos(Math.toRadians(pitch));
        float sinPitch = (float) Math.sin(Math.toRadians(pitch));
        float cosYaw = (float) Math.cos(Math.toRadians(yaw));
        float sinYaw = (float) Math.sin(Math.toRadians(yaw));

        return new float[] {
            cosPitch * sinYaw,
            sinPitch,
            cosPitch * cosYaw
        };
    }
    
}
