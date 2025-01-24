package lecture3;

import lecture1.Sphere;
import lecture1.Vector;

public class Sphere3 extends Sphere {
    public int specular;       // Specular coefficient
    public double reflective; // Reflectiveness (0.0 to 1.0)

    // Constructor
    public Sphere3(Vector center, double radius, int color, int specular, double reflective) {
        super(center, radius, color); // Call the constructor from lecture1.Sphere
        this.specular = specular;    // Set the specular property
        this.reflective = reflective; // Set the reflective property
    }
}
