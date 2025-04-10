package org.example;

import org.joml.Matrix4f;
import org.joml.Vector3f;
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
    
    private ShaderProgram shader;
    private Texture texture;
    private Chunk chunk;
    private Mesh chunkMesh;

    public static void main(String[] args) {
        new App().run();
    }

    public void run() {
        init();
        loop();

        // Cleanup
        chunkMesh.cleanup();
        shader.cleanup();
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
        
        // Set window hints for an OpenGL 3.3 core profile context (required for GLSL 330)
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
    
        window = glfwCreateWindow(800, 600, "Project 3: Minecraft-like Engine", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }
        
        // Set up callbacks (key callback, mouse callback, etc.)
        glfwSetKeyCallback(window, (win, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                glfwSetWindowShouldClose(win, true);
            }
        });
        
        glfwSetCursorPosCallback(window, (win, xpos, ypos) -> {
            camera.handleMouseMovement(xpos, ypos);
        });
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1); // Enable v-sync
        glfwShowWindow(window);
    
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
    
        // Initialize your camera, shader, texture, chunk, etc.
        camera = new Camera(0, 1.6f, 3, 0, -90);
        lastTime = glfwGetTime();
        shader = new ShaderProgram("shaders/vertex.glsl", "shaders/fragment.glsl");
        texture = new Texture("textures/texture_atlas.png");
        chunk = new Chunk();
        chunkMesh = MeshGenerator.generateMesh(chunk);
    }
    

    private void loop() {
        // Create a projection matrix (perspective)
        Matrix4f projection = new Matrix4f().perspective((float) Math.toRadians(70), 800f/600f, 0.1f, 100f);

        while (!glfwWindowShouldClose(window)) {
            double currentTime = glfwGetTime();
            float deltaTime = (float) (currentTime - lastTime);
            lastTime = currentTime;

            processInput(deltaTime);

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // Prepare view matrix from camera.
            Matrix4f view = camera.getViewMatrix();

            // Model matrix: identity for now.
            Matrix4f model = new Matrix4f().identity();

            shader.bind();
            // Set matrices as uniforms.
            shader.setUniform("projection", projection);
            shader.setUniform("view", view);
            shader.setUniform("model", model);
            // Set up lighting uniforms.
            shader.setUniform("lightPos", new Vector3f(10, 10, 10));
            shader.setUniform("viewPos", new Vector3f(camera.posX, camera.posY, camera.posZ));
            shader.setUniform("lightColor", new Vector3f(1, 1, 1));
            shader.setUniform("objectColor", new Vector3f(1, 1, 1));
            // Bind texture to texture unit 0.
            texture.bind();
            shader.setUniform("textureSampler", 0);
            
            // Render the chunk mesh.
            chunkMesh.render();
            shader.unbind();

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
}
