package lecture1;

/**
 * Represents a sphere in 3D space with a center, radius, and color.
 */
public class Sphere {
    public Vector center; // Center of the sphere
    public double radius; // Radius of the sphere
    public int color;     // Color of the sphere (as an RGB integer)

    // Constructor
    public Sphere(Vector center, double radius, int color) {
        this.center = center;
        this.radius = radius;
        this.color = color;
    }

    /**
     * Checks if a ray intersects this sphere.
     *
     * @param ray The ray to check for intersection.
     * @return An array containing the intersection distances [t1, t2].
     *         If no intersection, both values are Double.POSITIVE_INFINITY.
     */
    public double[] intersectRay(Ray ray) {
        Vector CO = ray.origin.subtract(this.center);

        double a = ray.direction.dot(ray.direction);
        double b = 2 * CO.dot(ray.direction);
        double c = CO.dot(CO) - this.radius * this.radius;

        double discriminant = b * b - 4 * a * c;

        if (discriminant < 0) {
            // No intersection
            return new double[]{Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY};
        }

        // Calculate the two possible intersection points
        double t1 = (-b + Math.sqrt(discriminant)) / (2 * a);
        double t2 = (-b - Math.sqrt(discriminant)) / (2 * a);

        // Ensure t1 is the smaller value
        if (t1 > t2) {
            double temp = t1;
            t1 = t2;
            t2 = temp;
        }

        return new double[]{t1, t2};
    }

    @Override
    public String toString() {
        return "Sphere(center=" + center + ", radius=" + radius + ", color=#" + Integer.toHexString(color) + ")";
    }
}
