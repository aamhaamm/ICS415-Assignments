# Lecture 1 - Basic Ray Tracing

This project demonstrates basic ray tracing concepts by rendering spheres in 3D space.

## Task Description

The goal is to:

1. Set up a development environment for raw pixel graphics.
2. Implement ray tracing to render spheres as described in Chapter 2 of the course material.
3. Generate an image (`Lecture1.png`) showing the rendered spheres.

## Features

- Basic ray-sphere intersection.
- Simple color rendering.
- Outputs a PNG image.

## Setup Instructions

### Prerequisites

- Java Development Kit (JDK) version 8 or higher.

### Steps to Run

1. Navigate to the `Lecture1` folder:
   ```bash
   cd ICS415-Lectures/Lecture1
   ```
2. Compile the Java files:

   ```bash
   javac Vector.java Sphere.java Ray.java RayTracer.java
   ```

3. Run the program:

   ```bash
   java RayTracer
   ```

4. The rendered output will be saved as Lecture1.png in the same folder.
