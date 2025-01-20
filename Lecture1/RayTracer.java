package lecture1;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

/**
 * A simple ray tracer that renders spheres in a 3D scene.
 * Each pixel is traced from the camera through the viewport,
 * checking for intersections with objects in the scene.
 */
public class RayTracer {

    // Canvas and viewport properties
    static int canvasWidth = 500;         // Width of the canvas in pixels
    static int canvasHeight = 500;        // Height of the canvas in pixels
    static double viewportSize = 1;       // Size of the viewport
    static double projectionPlaneD = 1;   // Distance from the camera to the projection plane
    static int backgroundColor = 0xFFFFFF; // Background color (white)

    // Define the scene with spheres
    static Sphere[] scene = {
        new Sphere(new Vector(0, -1, 3), 1, 0xFF0000), // Red sphere
        new Sphere(new Vector(2, 0, 4), 1, 0x0000FF), // Blue sphere
        new Sphere(new Vector(-2, 0, 4), 1, 0x00FF00) // Green sphere
    };

    public static void main(String[] args) {
        // Create a BufferedImage to store pixel data
        BufferedImage image = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);

        // Render the scene pixel by pixel
        renderScene(image);

        // Save the image to a file
        saveImage(image, "Lecture1.png");
    }

    /**
     * Renders the scene by casting rays from the camera through each pixel.
     *
     * @param image BufferedImage to store the rendered pixels.
     */
    public static void renderScene(BufferedImage image) {
        System.out.println("Rendering started...");
        for (int x = -canvasWidth / 2; x < canvasWidth / 2; x++) {
            for (int y = -canvasHeight / 2; y < canvasHeight / 2; y++) {
                // Convert canvas coordinates to viewport coordinates
                Vector direction = canvasToViewport(x, y);

                // Cast a ray from the camera
                Ray ray = new Ray(new Vector(0, 0, 0), direction);

                // Determine the color of the pixel
                int color = traceRay(ray, 1, Double.POSITIVE_INFINITY);

                // Set the pixel color in the image buffer
                image.setRGB(x + canvasWidth / 2, canvasHeight / 2 - y - 1, color);
            }
        }
        System.out.println("Rendering completed.");
    }

    /**
     * Converts canvas coordinates to viewport coordinates.
     *
     * @param x X-coordinate on the canvas.
     * @param y Y-coordinate on the canvas.
     * @return A Vector representing the direction from the camera through the viewport.
     */
    public static Vector canvasToViewport(int x, int y) {
        return new Vector(
            x * viewportSize / canvasWidth,
            y * viewportSize / canvasHeight,
            projectionPlaneD
        );
    }

    /**
     * Traces a ray to find the closest object it intersects.
     *
     * @param ray The ray being traced.
     * @param tMin The minimum distance to consider.
     * @param tMax The maximum distance to consider.
     * @return The color of the closest intersected object, or the background color if no intersection occurs.
     */
    public static int traceRay(Ray ray, double tMin, double tMax) {
        double closestT = tMax;
        Sphere closestSphere = null;

        // Check for intersections with all spheres in the scene
        for (Sphere sphere : scene) {
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

        // Return the color of the closest sphere or the background color
        if (closestSphere == null) {
            return backgroundColor;
        }
        return closestSphere.color;
    }

    /**
     * Saves the rendered image to a file.
     *
     * @param image The BufferedImage containing the rendered scene.
     * @param filename The name of the output file.
     */
    public static void saveImage(BufferedImage image, String filename) {
        try {
            File output = new File(filename);
            ImageIO.write(image, "png", output);
            System.out.println("Image saved as " + filename);
        } catch (Exception e) {
            System.err.println("Error saving the image: " + e.getMessage());
        }
    }
}
