package org.example;

public class Camera {
    public float posX, posY, posZ;
    public float pitch, yaw;
    public float movementSpeed = 3.0f;
    public float mouseSensitivity = 0.1f;
    
    private boolean firstMouse = true;
    private double lastMouseX, lastMouseY;

    public Camera(float posX, float posY, float posZ, float pitch, float yaw) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    // Calculate forward vector based on pitch and yaw.
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

    // Update camera angles based on mouse movement.
    public void handleMouseMovement(double xpos, double ypos) {
        if (firstMouse) {
            lastMouseX = xpos;
            lastMouseY = ypos;
            firstMouse = false;
        }
        double offsetX = xpos - lastMouseX;
        double offsetY = lastMouseY - ypos; // reversed Y
        lastMouseX = xpos;
        lastMouseY = ypos;
        offsetX *= mouseSensitivity;
        offsetY *= mouseSensitivity;
        yaw += offsetX;
        pitch += offsetY;
        if (pitch > 89.0f)
            pitch = 89.0f;
        if (pitch < -89.0f)
            pitch = -89.0f;
    }
}
