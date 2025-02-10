package project1.HitTable; 

import project1.Ray;
import project1.HitInfo.HitRecord;
import project1.HitInfo.Interval;

import java.util.ArrayList;

public class HitTableList implements HitTable {
    private final ArrayList<HitTable> objects;

    public HitTableList() {
        objects = new ArrayList<>();
    }

    public void add(HitTable object) {
        objects.add(object);
    }

    public void clear() {
        objects.clear();
    }

    @Override
    public boolean hit(Ray r, Interval tRange, HitRecord rec) {
        boolean hitAnything = false;
        double closestSoFar = tRange.max(); 

        for (HitTable object : objects) {
            HitRecord tempRec = new HitRecord(); 
            if (object.hit(r, new Interval(tRange.min(), closestSoFar), tempRec)) {
                hitAnything = true;
                closestSoFar = tempRec.t; 
                rec.t = tempRec.t;
                rec.p = tempRec.p;
                rec.normal = tempRec.normal;
                rec.mat = tempRec.mat;
                rec.front_face = tempRec.front_face;
            }
        }

        return hitAnything;
    }
}
