import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * The class impementing squares.
 * Note: you can add more methods if you want, but additional methods must be <code>private</code> or <code>protected</code>.
 *
 * @author Isaac Samsky
 */
public class Square implements Shape {
    private final Point[] vertices = new Point[4];

    protected Point[] getVertices() {
        return vertices;
    }

    /**
     * The constructor accepts an array of <code>Point</code>s to form the vertices of the square. If more than four
     * points are provided, only the first four are considered by this constructor.
     * <p>
     * If less than four points are provided, or if the points do not form a valid square, the constructor throws
     * <code>java.lang.IllegalArgumentException</code>.
     *
     * @param vertices the array of vertices (i.e., <code>Point</code> instances) provided to the constructor.
     */
    public Square(Point... vertices) {
        if (vertices.length < 4) throw new IllegalArgumentException("A square must have four vertices.");

        Point d = vertices[0];
        Point a = vertices[1];

        // calculate the correct position from the first two points
        Point b = new Point(vertices[2].name, a.x + (d.y - a.y), a.y + (a.x - d.x));
        Point c = new Point(vertices[3].name, b.x + (d.x - a.x), b.y + (d.y - a.y));

        Point b_candidate = vertices[2];
        Point c_candidate = vertices[3];

        if (!aboutEqual(b.x, b_candidate.x) || !aboutEqual(b.y, b_candidate.y) || !aboutEqual(c.x, c_candidate.x) || !aboutEqual(c.y, c_candidate.y))
            throw new IllegalArgumentException("The points do not form a square.");

        // additionally, if D must be the top-right vertex, then check that this is the case
        Square square_candidate = _initUnchecked(vertices);
        Point center = square_candidate.center();
        Point minAnglePoint = getMinAnglePoint(square_candidate.vertices, center);
        if (!aboutEqual(minAnglePoint.x, d.x) || !aboutEqual(minAnglePoint.y, d.y) || getMinAngleIndex(square_candidate.vertices, center) != 0)
            throw new IllegalArgumentException("The first point must be the top-right vertex.");

        // copy the validated points to the vertices array
        // arraycopy can cause issues with mutability
        this.vertices[0] = d;
        this.vertices[1] = a;
        this.vertices[2] = b;
        this.vertices[3] = c;

        // round
    }


    private Square _initUnchecked(Point... vertices) {
        System.arraycopy(vertices, 0, this.vertices, 0, 4);
        return this;
    }

    /**
     * Calculates the positive modulo of two integers.
     */
    private static int modulo(int a, int b) {
        return (a % b + b) % b;
    }

    /**
     * Calculates the positive modulo of two doubles.
     */
    private static double dModulo(double a, double b) {
        return (a % b + b) % b;
    }

    /**
     * Determines if two doubles are approximately equal.
     */
    private static boolean aboutEqual(double a, double b) {
        return Math.abs(a - b) < 0.001;
    }

    /**
     * Shifts the given vertices by <code>k</code> positions.
     */
    private static <T> T[] shift(T[] arr, int k) {
        T[] shifted = arr.clone();
        for (int i = 0; i < arr.length; i++) {
            shifted[modulo(i + k, arr.length)] = arr[i];
        }
        return shifted;
    }

    private static Point getMinAnglePoint(Point[] vertices, Point center) {
        assert vertices.length > 0; // satisfy intellij
        return Arrays.stream(vertices).map(p -> new Point(p.name, p.x - center.x, p.y - center.y)).min(Comparator.comparingDouble(p -> dModulo(Math.atan2(p.y, p.x), 2 * Math.PI))).map(p -> new Point(p.name, p.x + center.x, p.y + center.y)).get();
    }

    private static int getMinAngleIndex(Point[] vertices, Point center) {
        return Arrays.asList(vertices).indexOf(getMinAnglePoint(vertices, center));
    }

    /**
     * Shifts the given vertices so that the top-right vertex is first in the array.
     *
     * @return the shifted vertices
     */
    private static Point[] shifted(Point[] vertices, Point center) {
        if (vertices.length == 0) return vertices;
        return shift(vertices, -getMinAngleIndex(vertices, center));
    }


    @Override
    public Square rotateBy(int degrees) {
        // translate the square so that the center is at the origin
        Point center = center();
        Square translated = ((Square) translateBy(-center.x, -center.y));

        // apply matrix rotation
        Point[] rotated = Arrays.stream(translated.vertices).map(v -> new Point(v.name, v.x * Math.cos(Math.toRadians(degrees)) - v.y * Math.sin(Math.toRadians(degrees)), v.x * Math.sin(Math.toRadians(degrees)) + v.y * Math.cos(Math.toRadians(degrees)))).toArray(Point[]::new);

        // translate back to the original position
        return ((Square) new Square(shifted(rotated, new Point("Center", 0, 0))).translateBy(center.x, center.y));
    }

    @Override
    public Shape translateBy(double x, double y) {
        Point[] translated = Arrays.stream(vertices).map(v -> new Point(v.name, v.x + x, v.y + y)).toArray(Point[]::new);
        return new Square(translated);
    }

    @Override
    public String toString() {
        return Arrays.stream(vertices).map(p -> String.format("(%s, %.2f, %.2f)", p.name, p.x, p.y)).collect(Collectors.joining("; ", "[", "]"));
    }

    @Override
    public Point center() {
        // assuming that v[0] is the top-right vertex and v[2] is the bottom-left vertex
        return new Point("Center", (vertices[0].x + vertices[2].x) / 2, (vertices[0].y + vertices[2].y) / 2);
    }

    public static void main(String... args) {
        Point a = new Point("A", 1, 4);
        Point b = new Point("B", 1, 1);
        Point c = new Point("C", 4, 1);
        Point d = new Point("D", 4, 4);

        Point p = new Point("P", 0.3, 0.3);

//        Square sq1 = new Square(a, b, c, d); // throws an IllegalArgumentException
        Square sq2 = new Square(d, a, b, c); // forms a square
        Square sq3 = new Square(p, p, p, p); // forms a "trivial" square (this is a limiting case, but still valid)

        // prints: [(D, 4.0, 4.0); (A, 1.0, 4.0); (B, 1.0, 1.0); (C, 4.0, 1.0)]
        System.out.println(sq2);

        // prints: [(C, 4.0, 4.0); (D, 1.0, 4.0); (A, 1.0, 1.0); (B, 4.0, 1.0)]
        // note that the names denote which point has moved where
        System.out.println(sq2.rotateBy(90));
    }
}
