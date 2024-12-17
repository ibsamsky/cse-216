class Rectangle implements Shape {
    private final Point c1; // bottom left
    private final Point c2; // top right

    public Rectangle(Point c1, Point c2) {
        this.c1 = c1;
        this.c2 = c2;
    }

    @Override
    public Point center() {
        // (avg(x1, x2), avg(y1, y2))
        return new Point((c2.getX() + c1.getX()) / 2, (c2.getY() + c1.getY()) / 2);
    }

    @Override
    public double area() {
        // w*h
        return (Math.abs(c2.getX() - c1.getX())) * (Math.abs(c2.getY() - c1.getY()));
    }
}
