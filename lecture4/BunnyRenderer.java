package lecture4;

import lecture1.Ray;
import lecture1.Vector;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class BunnyRenderer {
    static int canvasWidth = 800;
    static int canvasHeight = 800;
    static double viewportSize = 1;
    static double projectionPlaneD = 1;
    static int backgroundColor = 0x000000;
    
    static List<Triangle> triangles;
    
    // Ground Plane
    static Triangle ground = new Triangle(
        new Vector(-50, -10, 50), new Vector(50, -10, 50), new Vector(0, -10, -50), 0xCCAA66
    );

    // Bounding Box for Bunny
    static Vector minBunny = new Vector(-10, -10, 1);
    static Vector maxBunny = new Vector(10, 10, 20);

    public static void main(String[] args) {
        try {
            triangles = OBJLoader.loadOBJ("lecture4/stanford_bunny.obj");
        } catch (IOException e) {
            System.err.println("Error loading model: " + e.getMessage());
            return;
        }

        BufferedImage image = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);
        renderScene(image);
        saveImage(image, "Lecture4.png");
    }

    public static void renderScene(BufferedImage image) {
        for (int x = -canvasWidth / 2; x < canvasWidth / 2; x++) {
            for (int y = -canvasHeight / 2; y < canvasHeight / 2; y++) {
                Vector direction = canvasToViewport(x, y);
                Ray ray = new Ray(new Vector(0, 5, -15), direction); // Adjusted camera position
                int color = traceRay(ray);
                image.setRGB(x + canvasWidth / 2, canvasHeight / 2 - y - 1, color);
            }
        }
    }

    public static Vector canvasToViewport(int x, int y) {
        return new Vector(x * viewportSize / canvasWidth, y * viewportSize / canvasHeight, projectionPlaneD);
    }

    public static int traceRay(Ray ray) {
        if (!rayIntersectsBox(ray)) return backgroundColor; // Skip unnecessary checks

        Triangle closestTriangle = null;
        double closestT = Double.POSITIVE_INFINITY;

        for (Triangle triangle : triangles) {
            double t = triangle.intersectRay(ray);
            if (t < closestT) {
                closestT = t;
                closestTriangle = triangle;
            }
        }

        // Check ground intersection
        double tGround = ground.intersectRay(ray);
        if (tGround < closestT) {
            closestT = tGround;
            closestTriangle = ground;
        }

        if (closestTriangle == null) return backgroundColor;

        Vector P = ray.origin.add(ray.direction.scale(closestT));
        return applyFlatShading(closestTriangle, P);
    }

    public static boolean rayIntersectsBox(Ray ray) {
        double tMin = (minBunny.x - ray.origin.x) / ray.direction.x;
        double tMax = (maxBunny.x - ray.origin.x) / ray.direction.x;
        if (tMin > tMax) { double temp = tMin; tMin = tMax; tMax = temp; }

        double tyMin = (minBunny.y - ray.origin.y) / ray.direction.y;
        double tyMax = (maxBunny.y - ray.origin.y) / ray.direction.y;
        if (tyMin > tyMax) { double temp = tyMin; tyMin = tyMax; tyMax = temp; }

        if ((tMin > tyMax) || (tyMin > tMax)) return false;
        return true;
    }

    public static int applyFlatShading(Triangle triangle, Vector P) {
        double intensity = 0.2;
        Vector lightPos = new Vector(2, 10, -5);
        Vector L = lightPos.subtract(P).normalize();

        double diffuse = Math.max(0, triangle.normal.dot(L));
        intensity += 0.6 * diffuse;

        return applyIntensity(triangle.color, intensity);
    }

    public static int applyIntensity(int color, double intensity) {
        int r = Math.min(255, (int) (((color >> 16) & 0xFF) * intensity));
        int g = Math.min(255, (int) (((color >> 8) & 0xFF) * intensity));
        int b = Math.min(255, (int) ((color & 0xFF) * intensity));
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
