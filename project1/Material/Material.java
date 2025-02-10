package project1.Material;

import project1.Ray;
import project1.HitInfo.HitRecord;
import project1.HitInfo.Radiance;

public interface Material {
    boolean scatter(Ray r, HitRecord rec, Radiance rad);
}
