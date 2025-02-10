package project1.HitTable; 

import project1.Material.Material;
import project1.Ray;
import project1.HitInfo.HitRecord;
import project1.HitInfo.Interval;
import project1.Vec3;

public class Sphere implements HitTable {
    private final Vec3 center;
    private final double radius;
    private final Material mat;

    public Sphere(Vec3 center, double radius, Material mat) {
        this.center = center;
        this.radius = radius;
        this.mat = mat;
    }

    @Override
    public boolean hit(Ray r, Interval tRange, HitRecord rec) {
        Vec3 oc = r.orig().minus(center);
        double a = r.dir().lengthSquared();
        double halfB = oc.dot(r.dir());
        double c = oc.lengthSquared() - radius * radius;

        double discriminant = halfB * halfB - a * c;

        if (discriminant < 0) return false;

        double sqrtD = Math.sqrt(discriminant);
        double root = (-halfB - sqrtD) / a;

        // Check if root is in the valid range
        if (root < tRange.min() || root > tRange.max()) {
            root = (-halfB + sqrtD) / a;
            if (root < tRange.min() || root > tRange.max()) {
                return false;
            }
        }

        rec.t = root;
        rec.p = r.at(root);
        Vec3 outwardNormal = rec.p.minus(center).divide(radius);
        rec.setFaceNormal(r, outwardNormal);
        rec.mat = mat;

        return true;
    }
}
