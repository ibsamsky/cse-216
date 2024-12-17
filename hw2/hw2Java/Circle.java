class Circle implements Shape {
    private final Point center;
    private final double radius;

    public Circle(Point center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    @Override
    public Point center() {
        return center;
    }

    @Override
    public double area() {
        return Math.PI * Math.pow(radius, 2);
    }
}
