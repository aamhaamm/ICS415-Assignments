package lecture1;

/**
 * Represents a ray in 3D space with an origin and a direction.
 */
public class Ray {
    public Vector origin;    // Origin point of the ray
    public Vector direction; // Direction of the ray (should be a unit vector)

    // Constructor
    public Ray(Vector origin, Vector direction) {
        this.origin = origin;
        this.direction = direction.normalize(); // Normalize direction to ensure consistency
    }

    @Override
    public String toString() {
        return "Ray(origin=" + origin + ", direction=" + direction + ")";
    }
}
