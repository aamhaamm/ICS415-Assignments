# Lecture 3 - Shadows and Reflections

This project extends the ray tracer from previous lectures by adding support for shadows and reflections, making the 3D scene more realistic.

## Task Description

The goal is to:

1. Enhance the ray tracer to support shadows and reflections.
2. Implement the following features:
   - Shadows: Objects cast shadows when blocking light sources.
   - Reflections: Objects reflect their surroundings based on their reflectiveness.
3. Generate an image (`Lecture2.png`) showcasing the rendered spheres with realistic shadows and reflections.

## Features

- Shadows: Objects block light sources, creating realistic shadow effects.
- Reflections: Recursive ray tracing enables objects to reflect their surroundings.
- Ray-sphere intersection: Built upon the intersection logic from Lecture 1.
- Lighting and shading: Includes ambient, diffuse, and specular lighting effects from Lecture 2.
- Outputs a PNG image with realistic shadows and reflections.
