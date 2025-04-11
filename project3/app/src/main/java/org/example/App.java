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
    
    // New field to track left mouse click state.
    private boolean leftMousePressed = false;

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
        // Set error callback.
        GLFWErrorCallback.createPrint(System.err).set();
    
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        
        // Set window hints for an OpenGL 3.3 Core context.
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
        
        // Set key callback to close window when ESC is pressed.
        glfwSetKeyCallback(window, (win, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                glfwSetWindowShouldClose(win, true);
            }
        });
        
        // Set up mouse callback for camera control.
        glfwSetCursorPosCallback(window, (win, xpos, ypos) -> {
            camera.handleMouseMovement(xpos, ypos);
        });
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1); // Enable V-Sync
        glfwShowWindow(window);
    
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
    
        // Initialize the camera.  
        // Position the camera so it can see the whole chunk. Adjust as needed.
        camera = new Camera(8, 20, -20, -30, 0);
        lastTime = glfwGetTime();
    
        // Load shader program.
        shader = new ShaderProgram("shaders/vertex.glsl", "shaders/fragment.glsl");
    
        // Load texture atlas.
        texture = new Texture("textures/texture_atlas.png");
    
        // Create a chunk with random terrain.
        chunk = new Chunk();
        chunkMesh = MeshGenerator.generateMesh(chunk);
    }
    
    private void loop() {
        // Create a perspective projection matrix.
        Matrix4f projection = new Matrix4f().perspective((float) Math.toRadians(70), 800f / 600f, 0.1f, 100f);

        while (!glfwWindowShouldClose(window)) {
            double currentTime = glfwGetTime();
            float deltaTime = (float) (currentTime - lastTime);
            lastTime = currentTime;
    
            processInput(deltaTime);
    
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    
            // Prepare view and model matrices.
            Matrix4f view = camera.getViewMatrix();
            Matrix4f model = new Matrix4f().identity();
    
            shader.bind();
            shader.setUniform("projection", projection);
            shader.setUniform("view", view);
            shader.setUniform("model", model);
            shader.setUniform("lightPos", new Vector3f(10, 10, 10));
            shader.setUniform("viewPos", new Vector3f(camera.posX, camera.posY, camera.posZ));
            shader.setUniform("lightColor", new Vector3f(1, 1, 1));
            shader.setUniform("objectColor", new Vector3f(1, 1, 1));
            
            // Bind texture and set uniform.
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
        // WASD movement.
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
        
        // Check for left mouse button for destroying a block.
        if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS) {
            if (!leftMousePressed) {
                leftMousePressed = true;
                raycastAndDestroyBlock();
            }
        } else {
            leftMousePressed = false;
        }
    }
    
    // Raycasts from the camera and destroys the first block hit.
    private void raycastAndDestroyBlock() {
        float maxDistance = 10f; // Maximum distance to look
        float step = 0.1f;       // Step size along the ray
        
        float[] forward = camera.getForwardVector();
        float originX = camera.posX;
        float originY = camera.posY;
        float originZ = camera.posZ;
        
        for (float t = 0; t < maxDistance; t += step) {
            float x = originX + forward[0] * t;
            float y = originY + forward[1] * t;
            float z = originZ + forward[2] * t;
            int blockX = (int)Math.floor(x);
            int blockY = (int)Math.floor(y);
            int blockZ = (int)Math.floor(z);
            
            BlockType block = chunk.getBlock(blockX, blockY, blockZ);
            if (block != BlockType.AIR) {
                // "Destroy" the block by setting it to AIR.
                chunk.destroyBlock(blockX, blockY, blockZ);
                // Regenerate the mesh.
                chunkMesh.cleanup();
                chunkMesh = MeshGenerator.generateMesh(chunk);
                break;
            }
        }
    }
}
