package project1.Material;

import project1.Color;
import project1.Ray;
import project1.HitInfo.HitRecord;
import project1.HitInfo.Radiance;
import project1.Vec3;

public class Lambertian implements Material {
    private final Color albedo;

    public Lambertian(Color color) {
        this.albedo = color;
    }

    @Override
    public boolean scatter(Ray r, HitRecord rec, Radiance rad) {
        Vec3 scatterDirection = rec.normal.add(Vec3.randomUnitVector()); // Ensuring correct Vec3 method

        // Catch degenerate scatter direction
        if (scatterDirection.nearZero()) {
            scatterDirection = rec.normal;
        }

        rad.scattered = new Ray(rec.p, scatterDirection);
        rad.attenuation = albedo;

        return true;
    }
}
