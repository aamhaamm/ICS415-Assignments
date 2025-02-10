package project1.HitInfo;

import project1.Color;
import project1.Point;
import project1.Ray;
import project1.Vec3;

public class Radiance { // to simulate a pointer for passing values back
    public Ray scattered;
    public Color attenuation;

    public Radiance() {
        this.scattered = new Ray(new Point(0, 0, 0), new Vec3(0, 0, 0)); 
        this.attenuation = new Color(0, 0, 0);  
    }

    // Setter methods (optional for flexibility)
    public void setScattered(Ray scattered) {
        this.scattered = scattered;
    }

    public void setAttenuation(Color attenuation) {
        this.attenuation = attenuation;
    }

    // Getter methods (optional)
    public Ray getScattered() {
        return scattered;
    }

    public Color getAttenuation() {
        return attenuation;
    }
}
