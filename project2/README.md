# Project 2 - Shadertoy Cable Shader in Unity

This folder (`project2`) contains a Unity project demonstrating a raymarched cable effect originally adapted from Shadertoy (GLSL). The goal is to show how to port a Shadertoy shader to Unity’s ShaderLab/HLSL and apply it to a 3D object (e.g., a Quad).

---

## Requirements

- **Unity Editor:** 2021.3 LTS or newer (other versions may work).
- **Operating System:** macOS, Windows, or Linux (where Unity is supported).

---

## Setup and Installation

# Open in Unity Hub

- **Launch Unity Hub.**
- **Click Open (or Add).**
- **Browse to the `project2` folder** inside `ICS415-ASSIGNMENTS`.
- **Click Open** to load the Unity project.
- **Wait for Unity to Compile**  
  Unity may import assets and compile shaders on first launch.

---

# Usage

## Open the Sample Scene

- In the **Project** window, go to `Assets/Scenes/`.
- Double-click `SampleScene.unity` to load it.

## Press Play

- Click the **Play** button in the Unity Editor.
- The **Game** view should show a Quad with the cable shader effect.

## Adjust the Quad or Camera

- In the **Hierarchy**, select the **Quad**.
- Modify the **Transform** (Position, Rotation, Scale) to fit your needs.
- Make sure the **Main Camera** can see the Quad.

## Customize the Shader

- Open `Assets/Shaders/ShadertoyCable.shader` in your code editor (e.g., VS Code).
- Tweak brightness, noise, or color values to alter the appearance of the cable.

---

# Shader Explanation

- **Raymarching Loop:** The shader steps along a ray, calculating distance to the cable surface.
- **`path(p)` Function:** Distorts positions to create a wavy cable.
- **Bright Flashes:** Exponential functions plus time-based animation (`frac(...)`) produce flashing highlights.
- **Material Property `_Resolution`:** Controls the coordinate system used by the shader. Increase it for a sharper look.

---

# Troubleshooting

## No Visible Cable

- Ensure `CableMat` is assigned to the Quad’s Mesh Renderer.
- Check the camera’s position and rotation; it must face the Quad.
- View the **Game** tab (not just the **Scene** tab).

## Dim or Pixelated

- Scale the Quad or move the camera closer.
- Increase `_Resolution` in the Material Inspector (e.g., `(1920, 1080, 0, 0)`).
- Adjust brightness factors in the shader code.

## Compile Errors

- Check the **Console**.
- Remove any extra `_Time` definitions (Unity already provides `_Time`).
