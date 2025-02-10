package project1.HitInfo;

import project1.Material.Material; 
import project1.Point;
import project1.Ray;
import project1.Vec3;

public class HitRecord {
    public double t;
    public Point p;
    public Vec3 normal;
    public boolean front_face;
    public Material mat;

    public HitRecord() {
        this.t = 0.0;
        this.p = new Point(0, 0, 0);   
        this.normal = new Vec3(0, 0, 0); 
        this.mat = null;
    }

    public void setFaceNormal(Ray r, Vec3 outwardNormal) {
        front_face = r.dir().dot(outwardNormal) < 0;
        normal = front_face ? outwardNormal : outwardNormal.negative();
    }


    // Getter methods for better encapsulation
    public double getT() { return t; }
    public Point getP() { return p; }
    public Vec3 getNormal() { return normal; }
    public boolean isFrontFace() { return front_face; }
    public Material getMaterial() { return mat; }
}
