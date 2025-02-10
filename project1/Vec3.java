package project1; 

import static project1.RTUtil.randomDouble;

public sealed class Vec3 permits Color, Point {
    private double x, y, z;

    public Vec3(double e0, double e1, double e2) {
        this.x = e0;
        this.y = e1;
        this.z = e2;
    }

    public Vec3(double s) {
        this.x = s;
        this.y = s;
        this.z = s;
    }

    public double x() { return x; }
    public double y() { return y; }
    public double z() { return z; }

    public Vec3 negative() {
        return new Vec3(-x, -y, -z);
    }

    public double[] toArray() {
        return new double[]{x, y, z};
    }

    public double lengthSquared() {
        return x * x + y * y + z * z;
    }

    public double length() {
        return Math.sqrt(lengthSquared());
    }

    public void plusEqual(Vec3 v) {
        x += v.x;
        y += v.y;
        z += v.z;
    }

    public void plusEqual(double s) {
        x += s;
        y += s;
        z += s;
    }

    public void minusEqual(Vec3 v) {
        x -= v.x;
        y -= v.y;
        z -= v.z;
    }

    public void minusEqual(double s) {
        x -= s;
        y -= s;
        z -= s;
    }

    public void multiplyEqual(Vec3 v) {
        x *= v.x;
        y *= v.y;
        z *= v.z;
    }

    public void multiplyEqual(double s) {
        x *= s;
        y *= s;
        z *= s;
    }

    public void divideEqual(Vec3 v) {
        x /= v.x;
        y /= v.y;
        z /= v.z;
    }

    public void divideEqual(double s) {
        x /= s;
        y /= s;
        z /= s;
    }

    public Vec3 add(Vec3 v) {
        return new Vec3(x + v.x, y + v.y, z + v.z);
    }

    public Vec3 add(double s) {
        return new Vec3(x + s, y + s, z + s);
    }

    public Vec3 minus(Vec3 v) {
        return new Vec3(x - v.x, y - v.y, z - v.z);
    }

    public Vec3 minus(double s) {
        return new Vec3(x - s, y - s, z - s);
    }

    public Vec3 multiply(Vec3 v) {
        return new Vec3(x * v.x, y * v.y, z * v.z);
    }

    public Vec3 multiply(double s) {
        return new Vec3(x * s, y * s, z * s);
    }

    public Vec3 divide(Vec3 v) {
        return new Vec3(x / v.x, y / v.y, z / v.z);
    }

    public Vec3 divide(double s) {
        return new Vec3(x / s, y / s, z / s);
    }

    public double dot(Vec3 v) {
        return x * v.x + y * v.y + z * v.z;
    }

    public Vec3 cross(Vec3 v) {
        return new Vec3(
                y * v.z - z * v.y,
                z * v.x - x * v.z,
                x * v.y - y * v.x
        );
    }

    public Vec3 pow(Vec3 v) {
        return new Vec3(Math.pow(x, v.x), Math.pow(y, v.y), Math.pow(z, v.z));
    }

    public Vec3 pow(double d) {
        return new Vec3(Math.pow(x, d), Math.pow(y, d), Math.pow(z, d));
    }

    public static Vec3 abs(Vec3 v) {
        return new Vec3(Math.abs(v.x), Math.abs(v.y), Math.abs(v.z));
    }

    public static Vec3 exp(Vec3 v) {
        return new Vec3(Math.exp(v.x), Math.exp(v.y), Math.exp(v.z));
    }

    public static Vec3 unitVector(Vec3 v) {
        return v.divide(v.length());
    }

    public static Vec3 random() {
        return new Vec3(randomDouble(), randomDouble(), randomDouble());
    }

    public static Vec3 random(double min, double max) {
        return new Vec3(randomDouble(min, max),
                        randomDouble(min, max),
                        randomDouble(min, max));
    }

    public static Vec3 randomInUnitSphere() {
        while (true) {
            Vec3 p = random(-1, 1);
            if (p.lengthSquared() >= 1) continue;
            return p;
        }
    }

    public static Vec3 randomUnitVector() {
        return unitVector(randomInUnitSphere());
    }

    public static Vec3 randomInHemisphere(Vec3 normal) {
        Vec3 inUnitSphere = randomInUnitSphere();
        return (inUnitSphere.dot(normal) > 0.0) ? inUnitSphere : inUnitSphere.negative();
    }

    public boolean nearZero() {
        double s = 1e-8;
        return (Math.abs(x) < s) && (Math.abs(y) < s) && (Math.abs(z) < s);
    }

    public static Vec3 reflect(Vec3 v, Vec3 n) {
        return v.minus(n.multiply(2 * v.dot(n)));
    }

    public static Vec3 refract(Vec3 uv, Vec3 n, double etaIOverEtaT) {
        double cosTheta = Math.min(uv.negative().dot(n), 1.0);
        Vec3 rOutPerp = uv.add(n.multiply(cosTheta)).multiply(etaIOverEtaT);
        Vec3 rOutParallel = n.multiply(-Math.sqrt(Math.abs(1.0 - rOutPerp.lengthSquared())));
        return rOutPerp.add(rOutParallel);
    }

    public static Vec3 randomInUnitDisk() {
        while (true) {
            Vec3 p = new Vec3(randomDouble(-1, 1), randomDouble(-1, 1), 0);
            if (p.lengthSquared() >= 1) continue;
            return p;
        }
    }
}
