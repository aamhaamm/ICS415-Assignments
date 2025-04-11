# Project 3 - Minecraft-like Voxel Engine

This project is a Java-based, LWJGL-powered voxel engine inspired by Minecraft. It demonstrates how to procedurally generate a 3D voxel world (a single chunk), and how to interact with it by destroying and placing blocks using raycasting. The engine features basic lighting, textured blocks, and a sky-colored background.

---

## Requirements

- **Java Development Kit (JDK):** Java 21 (or Java 17 if preferred) — ensure your JDK is Apple Silicon compatible if you're on macOS.
- **LWJGL:** Managed via Gradle (dependencies are configured in `app/build.gradle.kts`).
- **Operating System:** macOS (Apple Silicon) is the primary target; other platforms require appropriate LWJGL native libraries.
- **IDE:** Visual Studio Code, IntelliJ IDEA, Eclipse, or any preferred Java IDE.
- **Gradle:** Use the provided Gradle Wrapper (`gradlew`/`gradlew.bat`).

---

## Setup and Installation

### Clone/Download the Project

1. Place the `project3` folder inside your `ICS415-ASSIGNMENTS` folder.

### Build and Run

1. **Open a Terminal** in the project root (where `gradlew` is located).
2. **Clean and Build the Project:**
   ```bash
   ./gradlew clean build
   ```
3. **Run the Application**
   ```bash
   ./gradlew run
   ```

**Note:** The application creates a window with a sky-colored background and renders a procedurally generated 3D voxel chunk. It will keep running until you close the window (or press ESC).

---

## Controls and Gameplay

### Movement

- W/A/S/D: Move forward, left, backward, and right.

- Mouse Movement: Look around (control camera orientation).

- ESC: Close the game window.

### Interactions

- Left Mouse Button: Destroy a block.
  Aim at a block and left-click to remove it from the world.

- Right Mouse Button: Place a block.
  Aim at an empty spot and right-click to place a new SPECIAL block (displayed with a special texture/color).

When you destroy or place a block, the chunk’s mesh is updated in real time.
