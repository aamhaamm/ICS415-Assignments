package org.example;

import org.lwjgl.glfw.GLFWErrorCallback;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;

public class App {

    private long window;
    private Camera camera;
    private double lastTime;

    public static void main(String[] args) {
        new App().run();
    }

    public void run() {
        init();
        loop();

        // Free resources and terminate GLFW
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Set error callback
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(800, 600, "Project 3: Minecraft-like Engine", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Set up mouse input callback (for camera look)
        setupMouseCallback();
        // Capture the mouse cursor for FPS control
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);

        GL.createCapabilities();

        // Initialize our camera at a starting position
        camera = new Camera(0, 1.6f, 3, 0, -90);
        lastTime = glfwGetTime();
    }

    private void loop() {
        while (!glfwWindowShouldClose(window)) {
            double currentTime = glfwGetTime();
            float deltaTime = (float) (currentTime - lastTime);
            lastTime = currentTime;

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            processInput(deltaTime);

            // Here you would update the world and render chunks/meshes.

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void processInput(float deltaTime) {
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            float[] forward = camera.getForwardVector();
            camera.posX += forward[0] * camera.movementSpeed * deltaTime;
            camera.posY += forward[1] * camera.movementSpeed * deltaTime;
            camera.posZ += forward[2] * camera.movementSpeed * deltaTime;
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            float[] forward = camera.getForwardVector();
            camera.posX -= forward[0] * camera.movementSpeed * deltaTime;
            camera.posY -= forward[1] * camera.movementSpeed * deltaTime;
            camera.posZ -= forward[2] * camera.movementSpeed * deltaTime;
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            float[] forward = camera.getForwardVector();
            float leftX = -forward[2];
            float leftZ = forward[0];
            camera.posX += leftX * camera.movementSpeed * deltaTime;
            camera.posZ += leftZ * camera.movementSpeed * deltaTime;
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            float[] forward = camera.getForwardVector();
            float rightX = forward[2];
            float rightZ = -forward[0];
            camera.posX += rightX * camera.movementSpeed * deltaTime;
            camera.posZ += rightZ * camera.movementSpeed * deltaTime;
        }
    }

    private void setupMouseCallback() {
        glfwSetCursorPosCallback(window, (win, xpos, ypos) -> {
            camera.handleMouseMovement(xpos, ypos);
        });
    }
}
