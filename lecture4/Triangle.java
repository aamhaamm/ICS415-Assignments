package lecture4;

import lecture1.Vector;
import lecture1.Ray;

public class Triangle {
    public Vector v0, v1, v2; 
    public Vector normal; 
    public int color; 

    public Triangle(Vector v0, Vector v1, Vector v2, int color) {
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;
        this.color = color;
        this.normal = computeNormal();
    }

    private Vector computeNormal() {
        Vector edge1 = v1.subtract(v0);
        Vector edge2 = v2.subtract(v0);
        return edge1.cross(edge2).normalize();
    }

    public double intersectRay(Ray ray) {
        Vector edge1 = v1.subtract(v0);
        Vector edge2 = v2.subtract(v0);
        Vector h = ray.direction.cross(edge2);
        double a = edge1.dot(h);

        if (Math.abs(a) < 1e-6) return Double.POSITIVE_INFINITY;

        double f = 1.0 / a;
        Vector s = ray.origin.subtract(v0);
        double u = f * s.dot(h);

        if (u < 0.0 || u > 1.0) return Double.POSITIVE_INFINITY;

        Vector q = s.cross(edge1);
        double v = f * ray.direction.dot(q);

        if (v < 0.0 || u + v > 1.0) return Double.POSITIVE_INFINITY;

        double t = f * edge2.dot(q);
        return (t > 1e-6) ? t : Double.POSITIVE_INFINITY;
    }
}
