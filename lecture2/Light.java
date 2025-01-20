package lecture2;

import lecture1.Vector;
import lecture1.Ray;
import lecture1.Sphere;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.List;

public class Light {
    // Canvas and viewport properties
    static int canvasWidth = 500;   // Width of the canvas in pixels
    static int canvasHeight = 500;  // Height of the canvas in pixels
    static double viewportSize = 1; // Size of the viewport in world units
    static double projectionPlaneD = 1; // Distance from the camera to the projection plane
    static int backgroundColor = 0xFFFFFF; // Background color (white)

    // Define the scene with spheres
    static List<Sphere> spheres = List.of(
        new Sphere(new Vector(0, -1, 3), 1, 0xFF0000), // Red sphere
        new Sphere(new Vector(2, 0, 4), 1, 0x0000FF),  // Blue sphere
        new Sphere(new Vector(-2, 0, 4), 1, 0x00FF00), // Green sphere
        new Sphere(new Vector(0, -5001, 0), 5000, 0xFFFF00) // Yellow ground sphere simulating a plane
    );

    public static void main(String[] args) {
        // Create an empty image
        BufferedImage image = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);

        // Render the scene and populate the image with pixel data
        renderScene(image);

        // Save the rendered image to a file
        saveImage(image, "Lecture2.png");
    }

    // Renders the scene by iterating over all pixels on the canvas
    public static void renderScene(BufferedImage image) {
        for (int x = -canvasWidth / 2; x < canvasWidth / 2; x++) {
            for (int y = -canvasHeight / 2; y < canvasHeight / 2; y++) {
                // Convert canvas coordinates to a direction vector on the viewport
                Vector direction = canvasToViewport(x, y);

                // Create a ray originating from the camera (0, 0, 0) and passing through the viewport
                Ray ray = new Ray(new Vector(0, 0, 0), direction);

                // Determine the color for this ray
                int color = traceRay(ray, 1, Double.POSITIVE_INFINITY);

                // Set the pixel color in the image
                image.setRGB(x + canvasWidth / 2, canvasHeight / 2 - y - 1, color);
            }
        }
    }

    // Converts canvas coordinates to a viewport direction vector
    public static Vector canvasToViewport(int x, int y) {
        return new Vector(x * viewportSize / canvasWidth, y * viewportSize / canvasHeight, projectionPlaneD);
    }

    // Traces a ray and determines the color of the pixel based on intersections and lighting
    public static int traceRay(Ray ray, double tMin, double tMax) {
        Sphere closestSphere = null;
        double closestT = tMax;

        // Find the closest sphere that intersects with the ray
        for (Sphere sphere : spheres) {
            double[] ts = sphere.intersectRay(ray);
            if (ts[0] >= tMin && ts[0] < closestT) {
                closestT = ts[0];
                closestSphere = sphere;
            }
            if (ts[1] >= tMin && ts[1] < closestT) {
                closestT = ts[1];
                closestSphere = sphere;
            }
        }

        // If no sphere is intersected, return the background color
        if (closestSphere == null) return backgroundColor;

        // Calculate the intersection point and normal
        Vector P = ray.origin.add(ray.direction.scale(closestT)); // Intersection point
        Vector N = P.subtract(closestSphere.center).normalize(); // Surface normal at the intersection
        Vector V = ray.direction.scale(-1).normalize(); // View vector

        // Compute the lighting at the intersection point
        double lighting = computeLighting(P, N, V);

        // Apply lighting to the sphere's color
        return applyLighting(closestSphere.color, lighting);
    }

    // Computes the lighting intensity at a point using ambient, diffuse, and specular components
    public static double computeLighting(Vector P, Vector N, Vector V) {
        double intensity = 0.2; // Ambient light intensity
        Vector lightPos = new Vector(2, 2, 0); // Position of the light source
        Vector L = lightPos.subtract(P).normalize(); // Direction from point to light

        // Diffuse reflection (based on angle between light direction and surface normal)
        double nDotL = Math.max(0, N.dot(L));
        intensity += 0.6 * nDotL;

        // Specular reflection (based on the angle between reflected light and view direction)
        Vector R = N.scale(2 * N.dot(L)).subtract(L).normalize(); // Reflected light direction
        double rDotV = Math.max(0, R.dot(V));
        intensity += 0.8 * Math.pow(rDotV, 50); // Specular intensity with shininess factor

        return intensity;
    }

    // Modifies a color based on lighting intensity
    public static int applyLighting(int color, double intensity) {
        int r = Math.min(255, (int) (((color >> 16) & 0xFF) * intensity)); // Red channel
        int g = Math.min(255, (int) (((color >> 8) & 0xFF) * intensity));  // Green channel
        int b = Math.min(255, (int) ((color & 0xFF) * intensity));         // Blue channel
        return (r << 16) | (g << 8) | b; // Combine channels into a single RGB color
    }

    // Saves the rendered image to a file
    public static void saveImage(BufferedImage image, String filename) {
        try {
            File output = new File(filename);
            ImageIO.write(image, "png", output);
            System.out.println("Image saved as " + filename);
        } catch (Exception e) {
            System.err.println("Error saving image: " + e.getMessage());
        }
    }
}
