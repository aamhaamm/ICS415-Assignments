package project1; // Updated package for consistency

import project1.HitTable.HitTable;
import project1.HitTable.HitTableList;
import project1.HitTable.Sphere;
import project1.Material.Dielectric;
import project1.Material.Lambertian;
import project1.Material.Material;
import project1.Material.Metal;

import static project1.RTUtil.*;

public class Scene {
    private final Camera camera;
    private final HitTable world;
    private int imageWidth;
    private int imageHeight;

    public Scene(Camera camera, HitTable world) {
        this.camera = camera;
        this.world = world;
    }

    public Scene(Camera camera, HitTable world, int width, int height) {
        this.camera = camera;
        this.world = world;
        this.imageWidth = width;
        this.imageHeight = height;
    }

    public Camera getCamera() {
        return camera;
    }

    public HitTable getWorld() {
        return world;
    }

    /**
     * Initializes a simple scene with four spheres.
     */
    public static Scene initScene() {
        Point lookFrom = new Point(0, 0, 0);
        Point lookAt = new Point(0, 0, -1);
        Vec3 vup = new Vec3(0, 1, 0);
        Vec3 lookLength = lookFrom.minus(lookAt);
        double distToFocus = lookLength.length();

        Camera camera = new Camera(lookFrom, lookAt, vup, 90,
                0.01, distToFocus, 16 / 9.0);

        HitTableList world = new HitTableList();

        Material groundMaterial = new Lambertian(new Color(0.8, 0.8, 0));
        Material centerMaterial = new Lambertian(new Color(0.8, 0.3, 0.3));
        Material rightMaterial = new Metal(new Color(0.8, 0.8, 0.8), 0.2);
        Material leftMaterial = new Dielectric(new Color(1.0, 1.0, 1.0), 1.6);

        world.add(new Sphere(new Point(0, -100.5, -1), 100, groundMaterial));
        world.add(new Sphere(new Point(0.0, 0.0, -1.0), 0.5, centerMaterial));
        world.add(new Sphere(new Point(1.0, 0.0, -1.0), 0.5, rightMaterial));
        world.add(new Sphere(new Point(-1.0, 0.0, -1.0), 0.5, leftMaterial));

        return new Scene(camera, world);
    }

    /**
     * Creates a complex scene with a ground plane and multiple spheres of different materials.
     */
    public static Scene finalScene() {
        Point lookFrom = new Point(13, 2, 3);
        Point lookAt = new Point(0, 0, 0);
        Vec3 vup = new Vec3(0, 1, 0);
        Vec3 lookLength = lookFrom.minus(lookAt);
        double distToFocus = lookLength.length();

        Camera camera = new Camera(lookFrom, lookAt, vup, 20,
                0.05, distToFocus, 16 / 9.0);

        HitTableList world = new HitTableList();

        // Ground plane
        Material groundMaterial = new Lambertian(new Color(0.5, 0.5, 0.5));
        world.add(new Sphere(new Point(0, -1000, 0), 1000, groundMaterial));

        // Add random spheres
        for (int a = -11; a < 11; ++a) {
            for (int b = -11; b < 11; ++b) {
                double chooseMat = randomDouble();
                Point center = new Point(a + 0.9 * randomDouble(), 0.2, b + 0.9 * randomDouble());

                if (center.minus(new Point(4, 0.2, 0)).length() > 0.9) {
                    Material sphereMaterial;

                    if (chooseMat < 0.8) {
                        // Diffuse material
                        Color albedo = new Color(Vec3.random());
                        sphereMaterial = new Lambertian(albedo);
                        world.add(new Sphere(center, 0.2, sphereMaterial));
                    } else if (chooseMat < 0.95) {
                        // Metal material
                        Color albedo = new Color(Vec3.random(0.5, 1));
                        double fuzz = randomDouble(0, 0.5);
                        sphereMaterial = new Metal(albedo, fuzz);
                        world.add(new Sphere(center, 0.2, sphereMaterial));
                    } else {
                        // Glass material
                        sphereMaterial = new Dielectric(new Color(1), 1.5);
                        world.add(new Sphere(center, 0.2, sphereMaterial));
                    }
                }
            }
        }

        // Three large spheres in the scene
        world.add(new Sphere(new Point(0, 1, 0), 1.0, new Dielectric(new Color(1), 1.5)));
        world.add(new Sphere(new Point(-4, 1, 0), 1.0, new Lambertian(new Color(Vec3.random()))));
        world.add(new Sphere(new Point(4, 1, 0), 1.0, new Metal(new Color(0.7, 0.6, 0.5), 0.0)));

        return new Scene(camera, world);
    }
}
