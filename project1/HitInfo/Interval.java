package project1.HitInfo;

public record Interval(double min, double max) {

    public Interval {
        if (min > max) {
            throw new IllegalArgumentException("min cannot be greater than max");
        }
    }

    public boolean contains(double value) {
        return value >= min && value <= max;
    }

    public double size() {
        return max - min;
    }
}
