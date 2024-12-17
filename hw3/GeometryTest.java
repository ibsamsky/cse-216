import java.util.List;

/**
 * This class is given to you as an outline for testing your code. You can modify this as you want, but please keep in
 * mind that the lines already provided here as expected to work exactly as they are.
 *
 * @author Ritwik Banerjee
 */
public class GeometryTest {

    public static void main(String... args) {
        testSquareSymmetries();
    }

    private static void testSquareSymmetries() {
        // piazza https://piazza.com/class/m09ypc49yuw1ws/post/169
        Square s1 = new Square(
                new Point("OneTwo", 1, 2), // Top-Right (D)
                new Point("ZeroTwo", 0, 2), // Top-Left (A)
                new Point("ZeroOne", 0, 1), // Bottom-Left (B)
                new Point("OneOne", 1, 1) // Bottom-Right (C)
        );
        Square s2 = s1.rotateBy(30);
        Square s3 = s1.rotateBy(180);

        SquareSymmetries squareSymmetries = new SquareSymmetries();
        squareSymmetries.areSymmetric(s1, s2); // expected to return false
        squareSymmetries.areSymmetric(s1, s3); // expected to return true
        List<Square> symmetries = squareSymmetries.symmetriesOf(s1);

        // Your code must ensure that s1.toString() abides by the followin:
        // 1. Any non-integer coordinate value must be correctly rounded and represented with two decimal places.
        // 2. Keep in mind that the order of the four vertices of a square is important! Consult the examples in the
        //    assignment PDF for details.
        for (Square s : symmetries)
            System.out.println(s.toString());


        /*---*/
        Point  origin       = new Point("origin", 0, 0);
        Point  right        = new Point("right", 1, 0);
        Point  upright      = new Point("upright", 1, 1);
        Point  up           = new Point("up", 0, 1);



        Square sq = new Square(
                upright,
                up,
                origin,
                right
        );
        System.out.println(sq.toString());

        Square sq90cw = sq.rotateBy(90);
        System.out.println(sq90cw.toString());

        System.out.println(squareSymmetries.symmetriesOf(sq));

        System.out.println(squareSymmetries.areSymmetric(sq, sq90cw)); // expected to return true

//        Square sqfv
    }
}