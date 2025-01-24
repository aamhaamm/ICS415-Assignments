package lecture3;

import lecture1.Ray;
import lecture1.Vector;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.List;

public class ShadowsAndReflections {
    static int canvasWidth = 500;
    static int canvasHeight = 500;
    static double viewportSize = 1;
    static double projectionPlaneD = 1;
    static int backgroundColor = 0xFFFFFF;
    static int maxRecursionDepth = 3;

    // Define spheres using the extended Sphere3 class
    static List<Sphere3> spheres = List.of(
        new Sphere3(new Vector(0, -1, 3), 1, 0xFF0000, 500, 0.2), // Red sphere
        new Sphere3(new Vector(2, 0, 4), 1, 0x0000FF, 500, 0.3), // Blue sphere
        new Sphere3(new Vector(-2, 0, 4), 1, 0x00FF00, 10, 0.4), // Green sphere
        new Sphere3(new Vector(0, -5001, 0), 5000, 0xFFFF00, 1000, 0.5) // Yellow ground
    );

    public static void main(String[] args) {
        BufferedImage image = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);
        renderScene(image);
        saveImage(image, "Lecture3.png");
    }

    public static void renderScene(BufferedImage image) {
        for (int x = -canvasWidth / 2; x < canvasWidth / 2; x++) {
            for (int y = -canvasHeight / 2; y < canvasHeight / 2; y++) {
                Vector direction = canvasToViewport(x, y);
                Ray ray = new Ray(new Vector(0, 0, 0), direction);
                int color = traceRay(ray, 1, Double.POSITIVE_INFINITY, maxRecursionDepth);
                image.setRGB(x + canvasWidth / 2, canvasHeight / 2 - y - 1, color);
            }
        }
    }

    public static Vector canvasToViewport(int x, int y) {
        return new Vector(x * viewportSize / canvasWidth, y * viewportSize / canvasHeight, projectionPlaneD);
    }

    public static int traceRay(Ray ray, double tMin, double tMax, int depth) {
        if (depth <= 0) return backgroundColor;

        Sphere3 closestSphere = null;
        double closestT = tMax;

        // Find the closest sphere intersected by the ray
        for (Sphere3 sphere : spheres) {
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

        if (closestSphere == null) return backgroundColor;

        Vector P = ray.origin.add(ray.direction.scale(closestT)); // Intersection point
        Vector N = P.subtract(closestSphere.center).normalize(); // Surface normal
        Vector V = ray.direction.scale(-1); // View direction

        int localColor = applyLighting(closestSphere.color, P, N, V, closestSphere.specular);

        double reflective = closestSphere.reflective;
        if (reflective <= 0) return localColor;

        Vector R = reflectRay(V, N); // Reflected ray direction
        int reflectedColor = traceRay(new Ray(P, R), 0.001, Double.POSITIVE_INFINITY, depth - 1);

        return blendColors(localColor, reflectedColor, reflective);
    }

    public static int applyLighting(int color, Vector P, Vector N, Vector V, int specular) {
        double intensity = 0.2; // Ambient light
        Vector lightPos = new Vector(2, 2, 0); // Point light source position
        Vector L = lightPos.subtract(P).normalize(); // Light direction

        // Shadow check
        for (Sphere3 sphere : spheres) {
            double[] shadowT = sphere.intersectRay(new Ray(P, L));
            if (shadowT[0] > 0.001 && shadowT[0] < 1) return backgroundColor; // In shadow
        }

        // Diffuse reflection
        intensity += 0.6 * Math.max(0, N.dot(L));

        // Specular reflection
        if (specular > 0) {
            Vector R = reflectRay(L.scale(-1), N); // Reflected light direction
            double rDotV = Math.max(0, R.dot(V));
            intensity += 0.8 * Math.pow(rDotV, specular);
        }

        return applyIntensity(color, intensity);
    }

    public static Vector reflectRay(Vector R, Vector N) {
        return N.scale(2 * N.dot(R)).subtract(R);
    }

    public static int applyIntensity(int color, double intensity) {
        int r = Math.min(255, (int) (((color >> 16) & 0xFF) * intensity));
        int g = Math.min(255, (int) (((color >> 8) & 0xFF) * intensity));
        int b = Math.min(255, (int) ((color & 0xFF) * intensity));
        return (r << 16) | (g << 8) | b;
    }

    public static int blendColors(int color1, int color2, double ratio) {
        int r = (int) (((color1 >> 16) & 0xFF) * (1 - ratio) + ((color2 >> 16) & 0xFF) * ratio);
        int g = (int) (((color1 >> 8) & 0xFF) * (1 - ratio) + ((color2 >> 8) & (0xFF)) * ratio);
        int b = (int) (((color1 & 0xFF) * (1 - ratio)) + ((color2 & 0xFF) * ratio));
        return (r << 16) | (g << 8) | b;
    }

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
