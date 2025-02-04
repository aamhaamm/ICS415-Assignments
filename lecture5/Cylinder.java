package lecture5;

import lecture1.Vector;
import lecture1.Ray;

public class Cylinder {
    public Vector center;       // Center position of the cylinder
    public double radius;       // Radius of the cylinder
    public double height;       // Height of the cylinder
    public int color;           // Color of the cylinder
    public double reflectivity; // Reflectivity (0.0 to 1.0)
    public double transparency; // Transparency (0.0 to 1.0)

    public Cylinder(Vector center, double radius, double height, int color, double reflectivity, double transparency) {
        this.center = center;
        this.radius = radius;
        this.height = height;
        this.color = color;
        this.reflectivity = reflectivity;
        this.transparency = transparency;
    }

    /**
     * Computes the intersection of a ray with a finite-height cylinder.
     * The cylinder is aligned along the Y-axis.
     *
     * @param ray The incoming ray.
     * @return An array containing the nearest intersection distances [t1, t2].
     *         If no intersection, both values are Double.POSITIVE_INFINITY.
     */
    public double[] intersectRay(Ray ray) {
        Vector d = ray.direction;
        Vector o = ray.origin;
        Vector c = center;

        double dx = d.x, dz = d.z;
        double ox = o.x - c.x, oz = o.z - c.z;
        double r = radius;

        // Solving the quadratic equation for the cylinder's side intersection
        double a = dx * dx + dz * dz;
        double b = 2 * (ox * dx + oz * dz);
        double c_val = ox * ox + oz * oz - r * r;

        double discriminant = b * b - 4 * a * c_val;
        if (discriminant < 0) {
            return new double[]{Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY};
        }

        double sqrtDiscriminant = Math.sqrt(discriminant);
        double t1 = (-b - sqrtDiscriminant) / (2 * a);
        double t2 = (-b + sqrtDiscriminant) / (2 * a);

        // Find intersection points in world coordinates
        double y1 = o.y + t1 * d.y;
        double y2 = o.y + t2 * d.y;
        double cyBottom = c.y - height / 2; // Bottom cap
        double cyTop = c.y + height / 2;    // Top cap

        boolean validT1 = (y1 >= cyBottom && y1 <= cyTop);
        boolean validT2 = (y2 >= cyBottom && y2 <= cyTop);

        // If neither intersection is within the cylinder bounds, return no hit
        if (!validT1 && !validT2) {
            return new double[]{Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY};
        }

        // Cap intersections (Top & Bottom)
        double tCap1 = (cyBottom - o.y) / d.y;
        double tCap2 = (cyTop - o.y) / d.y;
        Vector pCap1 = o.add(d.scale(tCap1));
        Vector pCap2 = o.add(d.scale(tCap2));

        boolean hitCap1 = (pCap1.subtract(c).x * pCap1.subtract(c).x + pCap1.subtract(c).z * pCap1.subtract(c).z) <= r * r;
        boolean hitCap2 = (pCap2.subtract(c).x * pCap2.subtract(c).x + pCap2.subtract(c).z * pCap2.subtract(c).z) <= r * r;

        double tMin = Double.POSITIVE_INFINITY;
        double tMax = Double.POSITIVE_INFINITY;

        if (validT1) tMin = t1;
        if (validT2) tMax = (t2 < tMin) ? tMin : t2;
        if (hitCap1 && tCap1 >= 0 && tCap1 < tMin) tMin = tCap1;
        if (hitCap2 && tCap2 >= 0 && tCap2 < tMin) tMin = tCap2;

        return new double[]{tMin, tMax};
    }
}
