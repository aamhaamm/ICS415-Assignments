# ICS415 - Lectures Repository

This repository contains the source code and explanations for ICS415 lecture tasks. Each lecture has its own folder with specific tasks, code files, and a detailed README file.

## Author

- **Name**: Abdullah Al Matawah
- **ID**: 202034960

## Repository Structure

- **Lecture1/**: Contains the implementation of the ray tracing task explained in Lecture 1.
- **Lecture2/**: Contains the implementation of the shading and lighting task for Lecture 2.
- **Lecture3/**: Contains the implementation of the shadows and reflections task for Lecture 3.

Each lecture folder includes:

- The source code files.
- A lecture-specific README.md explaining the task

## How to Clone the Repository

To get a copy of this repository on your local machine:

1. Open your terminal.
2. Run the following command:
   ```bash
   git clone https://github.com/aamhaamm/ICS415-Assignments.git
   ```

## How to Compile and Run the Code

1. Navigate to the root folder of the project:

```bash
  cd ICS415-Assignments
```

2. Compile all lecture files:

```bash
   javac lecture1/*.java lecture2/*.java lecture3/*.java lecture4/*.java lecture5/*.java project1/*.java
```

3. Run the specific lecture:

Lecture 1:

```bash
   java lecture1.RayTracer
```

Lecture 2:

```bash
   java lecture2.Light
```

Lecture 3:

```bash
   java lecture3.ShadowsAndReflections
```

Lecture 4:

```bash
   java lecture4.BunnyRenderer
```

Lecture 5:

```bash
   java lecture5.RayTracerL5
```

Project 1:

```bash
   java project1.Main
```

4. The output image will be saved with the corresponding lecture number in the root directory (e.g., `Lecture1.png` for Lecture 1, `Lecture2.png` for Lecture 2, and so on).
