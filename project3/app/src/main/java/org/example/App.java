package org.example;

import org.lwjgl.glfw.GLFWErrorCallback;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;

public class App {

    private long window;

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
        // Print errors to System.err
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // Keep window hidden until ready
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Create the window
        window = glfwCreateWindow(800, 600, "Project 3: Minecraft-like Engine", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);  // Enable v-sync
        glfwShowWindow(window);

        // Create OpenGL bindings
        GL.createCapabilities();
    }

    private void loop() {
        // Set the clear color (background)
        glClearColor(0.2f, 0.3f, 0.4f, 1.0f);

        // Main loop
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            // Render code goes here...
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    // Added to satisfy the generated test.
    public String getGreeting() {
        return "Hello, LWJGL!";
    }
}
