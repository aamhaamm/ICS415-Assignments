package project1.HitTable;

import project1.Ray;
import project1.HitInfo.Interval;
import project1.HitInfo.HitRecord;

public interface HitTable {
    boolean hit(Ray r, Interval tRange, HitRecord rec);
}
