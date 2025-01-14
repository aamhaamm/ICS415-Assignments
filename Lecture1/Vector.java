/**
 * Represents a 3D vector and provides basic vector operations.
 */
public class Vector {
    public double x, y, z;

    // Constructor
    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // Vector addition
    public Vector add(Vector other) {
        return new Vector(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    // Vector subtraction
    public Vector subtract(Vector other) {
        return new Vector(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    // Scalar multiplication
    public Vector scale(double scalar) {
        return new Vector(this.x * scalar, this.y * scalar, this.z * scalar);
    }

    // Dot product
    public double dot(Vector other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    // Cross product
    public Vector cross(Vector other) {
        return new Vector(
            this.y * other.z - this.z * other.y,
            this.z * other.x - this.x * other.z,
            this.x * other.y - this.y * other.x
        );
    }

    // Magnitude of the vector
    public double magnitude() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    // Normalize the vector (convert to unit vector)
    public Vector normalize() {
        double magnitude = this.magnitude();
        if (magnitude == 0) {
            throw new ArithmeticException("Cannot normalize a zero vector.");
        }
        return this.scale(1 / magnitude);
    }

    // Override toString for easier debugging
    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }
}
