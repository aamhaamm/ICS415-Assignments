package project1.Material;

import project1.Color;
import project1.Ray;
import project1.HitInfo.HitRecord;
import project1.HitInfo.Radiance;
import project1.Vec3;

public class Metal implements Material {
    private final Color albedo;
    private final double fuzz;

    public Metal(Color color, double f) {
        this.albedo = color;
        this.fuzz = Math.min(f, 1.0);
    }

    @Override
    public boolean scatter(Ray r, HitRecord rec, Radiance rad) {
        Vec3 unitVector = Vec3.unitVector(r.dir()); 
        Vec3 reflected = Vec3.reflect(unitVector, rec.normal);
        Vec3 fuzzV = Vec3.randomInUnitSphere().multiply(fuzz); 

        rad.scattered = new Ray(rec.p, reflected.add(fuzzV));
        rad.attenuation = albedo;

        return rad.scattered.dir().dot(rec.normal) > 0;
    }
}
