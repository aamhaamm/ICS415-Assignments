package project1;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class RayTracerP1 {
    // Image and sampling settings
    private static final int WIDTH = 1024;
    private static final int HEIGHT = 768;
    private static final int SAMPLES_PER_PIXEL = 16; // Increased for less noise
    private static final int MAX_DEPTH = 15;           // Increased ray depth
    private static final double GAMMA = 2.2;           // Gamma correction

    private static final Random rand = new Random();

    private static final List<Sphere> spheres = new ArrayList<>();
    private static Light light;  // Our area light

    public static void main(String[] args) {
        setupScene();
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        render(image);
        // Optional: apply a simple denoising filter to smooth out residual noise.
        BufferedImage denoised = denoiseImage(image);
        saveImage(denoised, "Project1.png");
    }

    /**
     * Sets up the scene with spheres and an area light.
     */
    private static void setupScene() {
        // Create an area light (spherical light source) for soft shadows.
        light = new Light(new Vector(5, 10, -5), 2.0, new Vector(1, 1, 1), 1.0);

        // Define materials (colors in [0,1]).
        // Metallic spheres: full reflectivity.
        Material metal = new Material(new Vector(0.7, 0.7, 0.7), 1.0, false, 1.0, 1.0);
        // Matte spheres: purely diffuse.
        Material matte = new Material(new Vector(0.8, 0.5, 0.2), 0.0, false, 1.0, 0.1);
        // Glass spheres: transparent with proper refraction.
        Material glass = new Material(new Vector(0.95, 0.95, 0.95), 0.0, true, 1.5, 0.9);
        // Ground: smooth, matte.
        Material groundMat = new Material(new Vector(0.8, 0.8, 0.8), 0.0, false, 1.0, 0.05);

        // Main spheres.
        spheres.add(new Sphere(new Vector(0, -0.5, 4), 1, metal));
        spheres.add(new Sphere(new Vector(-2.5, -0.5, 5), 1, matte));
        spheres.add(new Sphere(new Vector(2.5, -0.5, 5), 1, glass));

        // Ground: large sphere acting as a ground plane (matte, low reflectivity).
        spheres.add(new Sphere(new Vector(0, -1001, 0), 1000, groundMat));

        // Optionally, add fewer small detail spheres (to keep the ground smooth).
        for (int i = 0; i < 100; i++) {
            double x = (rand.nextDouble() - 0.5) * 14;
            double z = rand.nextDouble() * 12 + 3;
            double y = -0.6;
            double radius = rand.nextDouble() * 0.2 + 0.05;
            // Use subdued random colors to reduce texture noise.
            Vector col = new Vector(rand.nextDouble() * 0.8, rand.nextDouble() * 0.8, rand.nextDouble() * 0.8);
            Material m = new Material(col, 0.0, false, 1.0, 0.1);
            spheres.add(new Sphere(new Vector(x, y, z), radius, m));
        }
    }

    /**
     * Renders the scene using a Monte Carlo path tracing approach.
     */
    private static void render(BufferedImage img) {
        // Camera setup: position, target, and coordinate system.
        Vector cameraPos = new Vector(0, 1, -7);
        Vector cameraLook = new Vector(0, 1, 0);
        Vector forward = cameraLook.sub(cameraPos).normalize();
        Vector right = forward.cross(new Vector(0, 1, 0)).normalize();
        Vector up = right.cross(forward).normalize();

        double fov = Math.toRadians(60);
        double scale = Math.tan(fov / 2);
        double aspectRatio = (double) WIDTH / HEIGHT;

        // Loop over each pixel.
        for (int j = 0; j < HEIGHT; j++) {
            for (int i = 0; i < WIDTH; i++) {
                Vector pixelColor = new Vector(0, 0, 0);
                // Anti-aliasing: take multiple samples per pixel.
                for (int s = 0; s < SAMPLES_PER_PIXEL; s++) {
                    double u = (i + rand.nextDouble()) / (WIDTH - 1);
                    double v = (j + rand.nextDouble()) / (HEIGHT - 1);
                    double px = (2 * u - 1) * aspectRatio * scale;
                    double py = (1 - 2 * v) * scale;
                    Vector rayDir = forward.add(right.mul(px)).add(up.mul(py)).normalize();
                    Ray ray = new Ray(cameraPos, rayDir);
                    pixelColor = pixelColor.add(trace(ray, 0));
                }
                // Average the samples and apply gamma correction.
                pixelColor = pixelColor.div(SAMPLES_PER_PIXEL);
                pixelColor = new Vector(Math.pow(pixelColor.x, 1 / GAMMA),
                                        Math.pow(pixelColor.y, 1 / GAMMA),
                                        Math.pow(pixelColor.z, 1 / GAMMA));
                img.setRGB(i, j, pixelColor.toRGB());
            }
        }
    }

    /**
     * Recursively traces a ray and returns the computed color (as a Vector in [0,1]).
     */
    private static Vector trace(Ray ray, int depth) {
        if (depth >= MAX_DEPTH) return new Vector(0, 0, 0);

        HitRecord hit = findClosest(ray);
        if (hit == null) {
            // Background: vertical gradient.
            double t = 0.5 * (ray.direction.y + 1.0);
            return new Vector(0.7, 0.8, 1.0).mul(1 - t).add(new Vector(1, 1, 1).mul(t));
        }

        Vector hitPoint = hit.point;
        Vector normal = hit.normal;
        Material m = hit.sphere.material;
        Vector offset = normal.mul(1e-4); // Avoid self-intersection.

        // Direct illumination from the area light.
        Vector directLight = sampleDirectLight(hitPoint, normal);

        // Reflection & refraction for reflective and transparent materials.
        Vector specularColor = new Vector(0, 0, 0);
        if (m.reflectivity > 0 || m.isRefractive) {
            if (m.isRefractive) {
                double kr = fresnel(ray.direction, normal, 1.0, m.refractiveIndex);
                Vector reflectDir = reflect(ray.direction, normal).normalize();
                Vector refractDir = refract(ray.direction, normal, 1.0, m.refractiveIndex);
                Vector reflectColor = trace(new Ray(hitPoint.add(offset), reflectDir), depth + 1);
                Vector refractColor = (refractDir != null)
                        ? trace(new Ray(hitPoint.sub(offset), refractDir.normalize()), depth + 1)
                        : new Vector(0, 0, 0);
                specularColor = reflectColor.mul(kr).add(refractColor.mul(1 - kr));
            } else {
                Vector reflectDir = reflect(ray.direction, normal).normalize();
                specularColor = trace(new Ray(hitPoint.add(offset), reflectDir), depth + 1);
            }
        }

        // Indirect global illumination via one diffuse bounce.
        Vector indirect = new Vector(0, 0, 0);
        if (!m.isRefractive && m.reflectivity < 1.0) {
            Vector diffuseDir = randomHemisphereDirection(normal);
            indirect = trace(new Ray(hitPoint.add(offset), diffuseDir), depth + 1);
        }

        // Combine contributions: direct light, indirect GI, and specular (reflection/refraction).
        double diffuseWeight = 1.0 - m.reflectivity;
        Vector finalColor = m.color.multiply(directLight.mul(diffuseWeight)
                            .add(indirect.mul(0.5)))   // Scale indirect bounce.
                            .add(specularColor.mul(m.reflectivity));
        return finalColor;
    }

    /**
     * Samples the area light for direct illumination using multiple samples.
     */
    private static Vector sampleDirectLight(Vector point, Vector normal) {
        int samples = 32; // Increased samples for smoother soft shadows.
        Vector direct = new Vector(0, 0, 0);
        for (int i = 0; i < samples; i++) {
            Vector lightSample = light.samplePoint();
            Vector lightDir = lightSample.sub(point).normalize();
            double distance = lightSample.sub(point).length();
            Ray shadowRay = new Ray(point.add(normal.mul(1e-4)), lightDir);
            HitRecord shadowHit = findClosest(shadowRay);
            boolean inShadow = false;
            if (shadowHit != null && shadowHit.t < distance)
                inShadow = true;
            if (!inShadow) {
                double ndotl = Math.max(0, normal.dot(lightDir));
                double attenuation = 1.0 / (distance * distance);
                direct = direct.add(light.color.mul(light.intensity * ndotl * attenuation));
            }
        }
        return direct.div(samples);
    }

    /**
     * Finds the closest sphere intersected by the ray.
     */
    private static HitRecord findClosest(Ray ray) {
        HitRecord closest = null;
        double closestT = Double.POSITIVE_INFINITY;
        for (Sphere s : spheres) {
            Double t = s.intersect(ray);
            if (t != null && t < closestT) {
                closestT = t;
                Vector hitPoint = ray.origin.add(ray.direction.mul(t));
                Vector normal = hitPoint.sub(s.center).normalize();
                closest = new HitRecord(s, hitPoint, normal, t);
            }
        }
        return closest;
    }

    // --- Reflection, Refraction, and Fresnel Helpers ---

    private static Vector reflect(Vector I, Vector N) {
        return I.sub(N.mul(2 * I.dot(N)));
    }

    private static Vector refract(Vector I, Vector N, double etaI, double etaT) {
        double cosi = clamp(-1, 1, I.dot(N));
        double etai = etaI, etat = etaT;
        Vector n = N;
        if (cosi < 0) {
            cosi = -cosi;
        } else {
            double temp = etai;
            etai = etat;
            etat = temp;
            n = N.mul(-1);
        }
        double eta = etai / etat;
        double k = 1 - eta * eta * (1 - cosi * cosi);
        if (k < 0) return null; // Total internal reflection.
        return I.mul(eta).add(n.mul(eta * cosi - Math.sqrt(k)));
    }

    private static double fresnel(Vector I, Vector N, double etaI, double etaT) {
        double cosi = clamp(-1, 1, I.dot(N));
        if (cosi > 0) {
            double temp = etaI;
            etaI = etaT;
            etaT = temp;
        }
        double sint = etaI / etaT * Math.sqrt(Math.max(0, 1 - cosi * cosi));
        if (sint >= 1) {
            return 1;
        } else {
            double cost = Math.sqrt(Math.max(0, 1 - sint * sint));
            cosi = Math.abs(cosi);
            double Rs = ((etaT * cosi) - (etaI * cost)) / ((etaT * cosi) + (etaI * cost));
            double Rp = ((etaI * cosi) - (etaT * cost)) / ((etaI * cosi) + (etaT * cost));
            return (Rs * Rs + Rp * Rp) / 2;
        }
    }

    private static double clamp(double min, double max, double val) {
        return Math.max(min, Math.min(max, val));
    }

    /**
     * Returns a random direction in the hemisphere defined by the given normal.
     */
    private static Vector randomHemisphereDirection(Vector normal) {
        Vector inUnitSphere;
        do {
            inUnitSphere = new Vector(rand.nextDouble() * 2 - 1,
                                      rand.nextDouble() * 2 - 1,
                                      rand.nextDouble() * 2 - 1);
        } while (inUnitSphere.dot(inUnitSphere) >= 1);
        if (inUnitSphere.dot(normal) < 0)
            inUnitSphere = inUnitSphere.mul(-1);
        return inUnitSphere.normalize();
    }

    /**
     * Applies a simple 3x3 box blur to the rendered image to reduce noise.
     */
    private static BufferedImage denoiseImage(BufferedImage img) {
        BufferedImage result = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        for (int y = 1; y < HEIGHT - 1; y++) {
            for (int x = 1; x < WIDTH - 1; x++) {
                int r = 0, g = 0, b = 0;
                for (int j = -1; j <= 1; j++) {
                    for (int i = -1; i <= 1; i++) {
                        int rgb = img.getRGB(x + i, y + j);
                        r += (rgb >> 16) & 0xFF;
                        g += (rgb >> 8) & 0xFF;
                        b += rgb & 0xFF;
                    }
                }
                r /= 9;
                g /= 9;
                b /= 9;
                result.setRGB(x, y, (r << 16) | (g << 8) | b);
            }
        }
        return result;
    }

    /**
     * Saves the BufferedImage as a PNG file.
     */
    private static void saveImage(BufferedImage img, String filename) {
        try {
            ImageIO.write(img, "png", new File(filename));
            System.out.println("Saved: " + filename);
        } catch (Exception e) {
            System.err.println("Error saving image: " + e.getMessage());
        }
    }

    // --- Supporting Classes ---

    /**
     * A 3D vector class used for geometry and as a color representation.
     */
    static class Vector {
        final double x, y, z;
        Vector(double x, double y, double z) {
            this.x = x; this.y = y; this.z = z;
        }
        Vector add(Vector o) { return new Vector(x + o.x, y + o.y, z + o.z); }
        Vector sub(Vector o) { return new Vector(x - o.x, y - o.y, z - o.z); }
        Vector mul(double s) { return new Vector(x * s, y * s, z * s); }
        Vector div(double s) { return new Vector(x / s, y / s, z / s); }
        double dot(Vector o) { return x * o.x + y * o.y + z * o.z; }
        Vector cross(Vector o) {
            return new Vector(
                y * o.z - z * o.y,
                z * o.x - x * o.z,
                x * o.y - y * o.x
            );
        }
        double length() { return Math.sqrt(x * x + y * y + z * z); }
        Vector normalize() {
            double len = length();
            return new Vector(x / len, y / len, z / len);
        }
        // Component-wise multiplication (useful for colors).
        Vector multiply(Vector o) { return new Vector(x * o.x, y * o.y, z * o.z); }
        // Clamps a value between 0 and 1.
        static double clamp(double val) { return Math.max(0, Math.min(1, val)); }
        // Converts the vector (as an RGB color in [0,1]) to a 0xRRGGBB integer.
        int toRGB() {
            int r = (int)(255 * clamp(x));
            int g = (int)(255 * clamp(y));
            int b = (int)(255 * clamp(z));
            return (r << 16) | (g << 8) | b;
        }
    }

    /**
     * A ray defined by an origin and a normalized direction.
     */
    static class Ray {
        final Vector origin, direction;
        Ray(Vector origin, Vector direction) {
            this.origin = origin;
            this.direction = direction.normalize();
        }
    }

    /**
     * A record for a ray-sphere hit.
     */
    static class HitRecord {
        final Sphere sphere;
        final Vector point, normal;
        final double t;
        HitRecord(Sphere sphere, Vector point, Vector normal, double t) {
            this.sphere = sphere;
            this.point = point;
            this.normal = normal;
            this.t = t;
        }
    }

    /**
     * Material properties for a sphere.
     */
    static class Material {
        final Vector color;
        final double reflectivity;  // 0 = diffuse; 1 = mirror.
        final boolean isRefractive;
        final double refractiveIndex;
        final double specular;      // Specular strength.
        Material(Vector color, double reflectivity, boolean isRefractive, double refractiveIndex, double specular) {
            this.color = color;
            this.reflectivity = reflectivity;
            this.isRefractive = isRefractive;
            this.refractiveIndex = refractiveIndex;
            this.specular = specular;
        }
    }

    /**
     * A sphere in the scene.
     */
    static class Sphere {
        final Vector center;
        final double radius;
        final Material material;
        Sphere(Vector center, double radius, Material material) {
            this.center = center;
            this.radius = radius;
            this.material = material;
        }
        /**
         * Computes the intersection parameter 't' along the ray.
         * Returns null if no valid intersection occurs.
         */
        Double intersect(Ray ray) {
            Vector oc = ray.origin.sub(center);
            double a = ray.direction.dot(ray.direction);
            double b = 2.0 * oc.dot(ray.direction);
            double c = oc.dot(oc) - radius * radius;
            double disc = b * b - 4 * a * c;
            if (disc < 0) return null;
            double sqrtDisc = Math.sqrt(disc);
            double t = (-b - sqrtDisc) / (2 * a);
            if (t < 1e-4) t = (-b + sqrtDisc) / (2 * a);
            return t < 1e-4 ? null : t;
        }
    }

    /**
     * A simple area light represented as a sphere.
     */
    static class Light {
        final Vector position;
        final double radius;
        final Vector color;
        final double intensity;
        Light(Vector position, double radius, Vector color, double intensity) {
            this.position = position;
            this.radius = radius;
            this.color = color;
            this.intensity = intensity;
        }
        /**
         * Returns a random point on the light’s surface.
         */
        Vector samplePoint() {
            return position.add(randomInUnitSphere().mul(radius));
        }
    }

    /**
     * Returns a random point inside a unit sphere.
     */
    private static Vector randomInUnitSphere() {
        Vector p;
        do {
            p = new Vector(rand.nextDouble() * 2 - 1,
                           rand.nextDouble() * 2 - 1,
                           rand.nextDouble() * 2 - 1);
        } while (p.dot(p) >= 1);
        return p;
    }
}
