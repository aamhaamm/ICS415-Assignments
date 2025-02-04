package lecture5;

import lecture1.Vector;
import lecture1.Ray;
import lecture1.Sphere;
import lecture4.Triangle;
import lecture4.OBJLoader;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class RayTracerL5 {
    static int canvasWidth = 800;
    static int canvasHeight = 600;
    static double viewportSize = 1;
    static double projectionPlaneD = 1;
    static int backgroundColor = 0x87CEEB; // Light blue sky
    static int maxDepth = 5; // Max recursion depth for reflections

    static List<Triangle> bunny;
    static Cylinder cylinder = new Cylinder(new Vector(0, 0, 4.2), 1, 3, 0x0000FF, 0.5, 0.0);
    static Sphere sphere = new Sphere(new Vector(1.7, 0, 4.5), 1, 0xFF0000);  

    public static void main(String[] args) {
        // Load the Bunny model
        try {
            bunny = OBJLoader.loadOBJ("lecture4/stanford_bunny.obj");
            Vector bunnyOffset = new Vector(-1.3, -0.3, 4.5); 
            for (Triangle triangle : bunny) {
                triangle.v0 = triangle.v0.add(bunnyOffset);
                triangle.v1 = triangle.v1.add(bunnyOffset);
                triangle.v2 = triangle.v2.add(bunnyOffset);
            }                       
        } catch (IOException e) {
            System.err.println("Error loading model: " + e.getMessage());
            return;
        }

        BufferedImage image = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);
        renderScene(image);
        saveImage(image, "Lecture5.png");
    }

    /**
     * Renders the scene by tracing rays through each pixel.
     */
    public static void renderScene(BufferedImage image) {
        for (int x = -canvasWidth / 2; x < canvasWidth / 2; x++) {
            for (int y = -canvasHeight / 2; y < canvasHeight / 2; y++) {
                Vector direction = canvasToViewport(x, y);
                Ray ray = new Ray(new Vector(0, 2, -10), direction);
                int color = traceRay(ray, 1, Double.POSITIVE_INFINITY, maxDepth);
                image.setRGB(x + canvasWidth / 2, canvasHeight / 2 - y - 1, color);
            }
        }
    }

    /**
     * Converts canvas coordinates to viewport coordinates.
     */
    public static Vector canvasToViewport(int x, int y) {
        return new Vector(x * viewportSize / canvasWidth, y * viewportSize / canvasHeight, projectionPlaneD);
    }

    /**
     * Traces a ray and determines the color of the pixel based on intersections.
     */
    public static int traceRay(Ray ray, double tMin, double tMax, int depth) {
        Object closestObject = null;
        double closestT = tMax;

        // Check for intersection with the sphere
        double[] tSphere = sphere.intersectRay(ray);
        if (tSphere[0] >= tMin && tSphere[0] < closestT) {
            closestT = tSphere[0];
            closestObject = sphere;
        }

        // Check for intersection with the cylinder
        double[] tCylinder = cylinder.intersectRay(ray);
        if (tCylinder[0] >= tMin && tCylinder[0] < closestT) {
            closestT = tCylinder[0];
            closestObject = cylinder;
        }

        // Check for intersection with the bunny model
        Triangle closestTriangle = null;
        for (Triangle triangle : bunny) {
            double t = triangle.intersectRay(ray);
            if (t >= tMin && t < closestT) {
                closestT = t;
                closestTriangle = triangle;
                closestObject = triangle;
            }
        }

        // Return background color if no object was hit
        if (closestObject == null) return backgroundColor;

        // Compute intersection point
        Vector P = ray.origin.add(ray.direction.scale(closestT));

        // Determine color based on the object hit
        if (closestObject instanceof Sphere) {
            return ((Sphere) closestObject).color;
        } else if (closestObject instanceof Cylinder) {
            return ((Cylinder) closestObject).color;
        } else if (closestObject instanceof Triangle) {
            return ((Triangle) closestObject).color;
        }

        return backgroundColor;
    }

    /**
     * Saves the rendered image to a file.
     */
    public static void saveImage(BufferedImage image, String filename) {
        try {
            File output = new File(filename);
            ImageIO.write(image, "png", output);
            System.out.println("Image saved as " + filename);
        } catch (IOException e) {
            System.err.println("Error saving image: " + e.getMessage());
        }
    }
}