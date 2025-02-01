# Lecture 4 - Flat Shaded Stanford Bunny

This project builds on previous ray tracing techniques by rendering the **Stanford Bunny** using **flat shading**. The bunny is represented as a **triangle mesh**, and a **ground plane** is included for realism.

## Task Description

The goal is to:

1. **Load and render the Stanford Bunny** as a triangle mesh.
2. **Implement flat shading**, where each triangle has a uniform color based on lighting.
3. **Optimize performance** using a bounding box check to avoid unnecessary ray-triangle intersections.
4. **Include a ground plane** for realism.

## Features

- **Triangle-based Stanford Bunny rendering** using ray-triangle intersection.
- **Flat shading** (each triangle shaded based on its normal and light source).
- **Ground plane** for better scene composition.
- **Bounding Box Optimization** for faster rendering.
- **Outputs a PNG image** with the rendered bunny and ground.
