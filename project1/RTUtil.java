package project1;

import java.util.concurrent.ThreadLocalRandom;

public class RTUtil {
    public static final double INFINITY = Double.POSITIVE_INFINITY; // 1.0 / 0.0;
    public static final double PI = 3.1415926535897932385;

    public static double degreesToRadians(double degrees) {
        return degrees * PI / 180;
    }

    public static double randomDouble() {
        return ThreadLocalRandom.current().nextDouble();
    }

    public static double randomDouble(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    public static int randomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    public static double clamp(double x, double min, double max) {
        return Math.max(min, Math.min(x, max));
    }
}
