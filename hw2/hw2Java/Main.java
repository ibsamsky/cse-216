import java.util.ArrayList;
import java.util.List;

public class Main /* ShapeDriver */ {
    public static void main(String... args) {
        Shape s1 = new Point(0, 0);
        Shape s2 = new Circle((Point) s1, 2); // TODO: correct the type error in this line
        Shape s3 = new Circle(new Point(0, 0), 3);
        List<Shape> shapes = new ArrayList<>();
        shapes.add(s1);
        shapes.add(s2);
        shapes.add(s3);
        for (Shape s : shapes) {
            // TODO: override the required method to make the output human-readable
            System.out.println("center: " + s.center() + "; area: " + s.area());
        }
    }
}