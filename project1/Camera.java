package project1; 

import static project1.RTUtil.*;

public class Camera {
    private final Point origin;
    private final Vec3 horizontal;
    private final Vec3 vertical;
    private final Vec3 lowerLeftCorner;
    private final double lensRadius;
    private final Vec3 u, v, w;

    public Camera(Point lookFrom, Point lookAt, Vec3 vUp,
                  double vFov, double aperture, double focusDist, double aspectRatio) {

        double theta = degreesToRadians(vFov);
        double h = Math.tan(theta / 2);
        double viewportHeight = 2.0 * h;
        double viewportWidth = aspectRatio * viewportHeight;

        w = Vec3.unitVector(lookFrom.minus(lookAt));
        u = Vec3.unitVector(vUp.cross(w));
        v = w.cross(u);

        origin = lookFrom;
        horizontal = u.multiply(focusDist * viewportWidth);
        vertical = v.multiply(focusDist * viewportHeight);
        lowerLeftCorner = origin
                .minus(horizontal.divide(2))
                .minus(vertical.divide(2))
                .minus(w.multiply(focusDist));

        lensRadius = aperture / 2;
    }

    public Ray getRay(double s, double t) {
        Vec3 rd = Vec3.randomInUnitDisk().multiply(lensRadius);
        Vec3 offset = u.multiply(rd.x()).add(v.multiply(rd.y()));

        Point o = origin.add(offset);
        Vec3 l = lowerLeftCorner
                .add(horizontal.multiply(s))
                .add(vertical.multiply(1 - t))
                .minus(origin)
                .minus(offset);

        return new Ray(o, l);
    }
}
