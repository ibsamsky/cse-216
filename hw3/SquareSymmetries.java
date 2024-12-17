import java.util.Arrays;
import java.util.List;

public class SquareSymmetries implements Symmetries<Square> {

    @Override
    public boolean areSymmetric(Square s1, Square s2) {
        // assumes that point names are important
        return symmetriesOf(s1).contains(s2);

        // if not, compare each set of points by their coordinates
        // (not implemented)
    }

    @Override
    public List<Square> symmetriesOf(Square square) {
        Square id, r1, r2, r3, fv, fh, fd, fc;

        id = square;
        r1 = square.rotateBy(90);
        r2 = square.rotateBy(180);
        r3 = square.rotateBy(270);

        // swap 0 and 2
        Point[] vertices = square.getVertices();
        Point d = vertices[0];
        Point b = vertices[2];

        Point d_new = new Point(d.name, b.x, b.y);
        Point b_new = new Point(b.name, d.x, d.y);

        fc = new Square(b_new, vertices[1], d_new, vertices[3]);
        fv = fc.rotateBy(90);
        fd = fc.rotateBy(180);
        fh = fc.rotateBy(270);

        return Arrays.asList(id, r1, r2, r3, fc, fv, fd, fh);
    }
}
