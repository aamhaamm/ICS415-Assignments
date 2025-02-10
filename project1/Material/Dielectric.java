package project1.Material; 

import project1.Color;
import project1.HitInfo.HitRecord;
import project1.HitInfo.Radiance;
import project1.Ray;
import project1.Vec3;
import project1.RTUtil;

public class Dielectric implements Material {
    private final Color albedo;
    private final double ir; // Index of Refraction

    public Dielectric(Color color, double ir) {
        this.albedo = color;
        this.ir = ir;
    }

    @Override
    public boolean scatter(Ray r, HitRecord rec, Radiance rad) {
        double refractionRatio = rec.front_face ? (1.0 / ir) : ir;

        Vec3 unitDirection = Vec3.unitVector(r.dir());
        double cosTheta = Math.min(unitDirection.negative().dot(rec.normal), 1.0);
        double sinTheta = Math.sqrt(1.0 - cosTheta * cosTheta);

        boolean cannotRefract = refractionRatio * sinTheta > 1.0;
        boolean shouldReflect = reflectance(cosTheta, refractionRatio) > RTUtil.randomDouble();

        Vec3 direction;
        if (cannotRefract || shouldReflect) {
            direction = Vec3.reflect(unitDirection, rec.normal);
        } else {
            direction = Vec3.refract(unitDirection, rec.normal, refractionRatio);
        }

        rad.scattered = new Ray(rec.p, direction);
        rad.attenuation = albedo;
        return true;
    }

    private double reflectance(double cosine, double refIdx) {
        double r0 = (1.0 - refIdx) / (1.0 + refIdx);
        r0 *= r0;
        return r0 + (1.0 - r0) * Math.pow((1.0 - cosine), 5.0);
    }
}
