import java.math.BigDecimal;
import java.math.RoundingMode;

class Point implements Shape {
    private final double x;
    private final double y;

    public Point(double x, double y) {
        this.x = round(x);
        this.y = round(y);
    }

    private static double round(double value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    // public is probably okay here
    protected double getX() {
        return x;
    }

    protected double getY() {
        return y;
    }

    @Override
    public Point center() {
        return this;
    }

    @Override
    public double area() {
        return 0;
    }

    @Override
    public String toString() {
        return String.format("(%.2f, %.2f)", x, y);
    }
}
