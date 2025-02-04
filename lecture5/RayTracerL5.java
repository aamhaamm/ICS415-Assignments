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
    static int backgroundColor = 0x87CEEB;
    static int maxDepth = 5;
    
    static List<Triangle> bunny;
    static Cylinder cylinder = new Cylinder(new Vector(0, 0, 4.2), 1, 3, 0x0000FF, 0.5, 0.3);
    static Sphere sphere = new Sphere(new Vector(1.7, 0, 4.5), 1, 0xFF0000);
    static Vector lightSource = new Vector(2, 5, -2);
    
    public static void main(String[] args) {
        try {
            bunny = OBJLoader.loadOBJ("lecture4/stanford_bunny.obj");
            Vector bunnyOffset = new Vector(-1.0, -0.3, 4.5);
            for (Triangle triangle : bunny) {
                triangle.v0 = triangle.v0.add(bunnyOffset);
                triangle.v1 = triangle.v1.add(bunnyOffset);
                triangle.v2 = triangle.v2.add(bunnyOffset);
                triangle.color = 0xFFFFFF; // Ensure bunny is white
            }
        } catch (IOException e) {
            System.err.println("Error loading model: " + e.getMessage());
            return;
        }
        
        BufferedImage image = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);
        renderScene(image);
        saveImage(image, "Lecture5.png");
    }
    
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
    
    public static Vector canvasToViewport(int x, int y) {
        return new Vector(x * viewportSize / canvasWidth, y * viewportSize / canvasHeight, projectionPlaneD);
    }
    
    public static int traceRay(Ray ray, double tMin, double tMax, int depth) {
        if (depth <= 0) return backgroundColor;
        Object closestObject = null;
        double closestT = tMax;

        double[] tSphere = sphere.intersectRay(ray);
        if (tSphere[0] >= tMin && tSphere[0] < closestT) {
            closestT = tSphere[0];
            closestObject = sphere;
        }

        double[] tCylinder = cylinder.intersectRay(ray);
        if (tCylinder[0] >= tMin && tCylinder[0] < closestT) {
            closestT = tCylinder[0];
            closestObject = cylinder;
        }

        for (Triangle triangle : bunny) {
            double t = triangle.intersectRay(ray);
            if (t >= tMin && t < closestT) {
                closestT = t;
                closestObject = triangle;
            }
        }

        if (closestObject == null) return backgroundColor;

        Vector P = ray.origin.add(ray.direction.scale(closestT));
        Vector normal = new Vector(0, 0, 0);
        int baseColor = backgroundColor;
        if (closestObject instanceof Sphere) {
            Sphere obj = (Sphere) closestObject;
            normal = P.subtract(obj.center).normalize();
            baseColor = obj.color;
        } else if (closestObject instanceof Cylinder) {
            Cylinder obj = (Cylinder) closestObject;
            normal = new Vector(P.x - obj.center.x, 0, P.z - obj.center.z).normalize();
            baseColor = obj.color;
        } else if (closestObject instanceof Triangle) {
            Triangle obj = (Triangle) closestObject;
            normal = obj.normal;
            baseColor = obj.color;
        }
        
        int shadedColor = applyLighting(P, normal, baseColor);

        return shadedColor;
    }

    private static int applyLighting(Vector P, Vector normal, int baseColor) {
        Vector lightDir = lightSource.subtract(P).normalize();
        double diffuse = Math.max(0, normal.dot(lightDir));
        int r = (int) (((baseColor >> 16) & 0xFF) * diffuse);
        int g = (int) (((baseColor >> 8) & 0xFF) * diffuse);
        int b = (int) ((baseColor & 0xFF) * diffuse);
        return (r << 16) | (g << 8) | b;
    }
    
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
