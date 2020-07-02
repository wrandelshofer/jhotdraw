package org.jhotdraw8.geom.offsetline;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.geom.AWTPathBuilder;
import org.jhotdraw8.geom.Shapes;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class OffsetPathBuilderTest {
    @NonNull
    @TestFactory
    public List<DynamicTest> rawOffsetSegmentsTests() {
        return Arrays.asList(
                dynamicTest("1", () -> rawOffsetSegmentsTest(
                        polylineOf(new double[][]{{100, 100, 0}, {300, 100, 0}, {300, 200, 0}, {100, 200, 0}}),
                        true,
                        -20,
                        polylineOf(new double[][]{{0, 0, 0}, {20, 0, 0}, {20, 10, 0}, {0, 10, 0}})
                )),
                dynamicTest("1", () -> rawOffsetSegmentsTest(
                        polylineOf(new double[][]{{100, 100, 0}, {300, 100, 0}, {300, 200, 0}, {100, 200, 0}}),
                        true,
                        20,
                        polylineOf(new double[][]{{0, 0, 0}, {20, 0, 0}, {20, 10, 0}, {0, 10, 0}})
                ))
        );
    }

    @NonNull
    @TestFactory
    public List<DynamicTest> rectangleTests() {
        return Arrays.asList(
                dynamicTest("1", () -> testOffsetLine(
                        polylineOf(new double[][]{{0, 0, 0}, {20, 0, 0}, {20, 10, 0}, {0, 10, 0}}),
                        false,
                        2,
                        polylineOf(new double[][]{{0, 0, 0}, {20, 0, 0}, {20, 10, 0}, {0, 10, 0}})
                ))
        );
    }

    private static PolyArcPath polylineOf(double[][] coords) {
        PolyArcPath p = new PolyArcPath();
        for (int i = 0; i < coords.length; i++) {
            double[] v = coords[i];
            p.addVertex(v[0], v[1], v[2]);
        }
        return p;
    }

    private void testOffsetLine(PolyArcPath input, boolean closed, double offset, PolyArcPath expected) throws Exception, InterruptedException {
        input.isClosed(closed);
        SwingUtilities.invokeAndWait(() -> {
            JDialog f = new JDialog();
            f.setModal(true);
            f.setSize(new Dimension(600, 400));
            JComponent canvas = new JComponent() {
                @Override
                public void paint(Graphics gr) {
                    Graphics2D g = (Graphics2D) gr;
                    AWTPathBuilder b = new AWTPathBuilder();
                    Shapes.buildFromPathIterator(b, input.getPathIterator(null));
                    g.draw(b.build());
                }
            };

            f.getContentPane().add(canvas);
            f.setVisible(true);
        });
    }

    private void rawOffsetSegmentsTest(PolyArcPath input, boolean closed, double offset, PolyArcPath expected) throws Exception, InterruptedException {
        input.isClosed(closed);
        PapOffsetPathBuilder cc = new PapOffsetPathBuilder();
        List<PlineOffsetSegment> actual = cc.createUntrimmedOffsetSegments(input, offset);

        SwingUtilities.invokeAndWait(() -> {
            JDialog f = new JDialog();
            f.setModal(true);
            f.setSize(new Dimension(600, 400));
            JComponent canvas = new JComponent() {
                @Override
                public void paint(Graphics gr) {
                    Graphics2D g = (Graphics2D) gr;
                    g.setColor(Color.BLACK);
                    AWTPathBuilder b = new AWTPathBuilder();
                    Shapes.buildFromPathIterator(b, input.getPathIterator(null));
                    g.draw(b.build());
                    g.setColor(Color.MAGENTA);
                    b = new AWTPathBuilder();
                    for (PlineOffsetSegment seg : actual) {
                        b.moveTo(seg.v1.getX(), seg.v1.getY());
                        b.lineTo(seg.v2.getX(), seg.v2.getY());
                    }
                    g.draw(b.build());
                }
            };

            f.getContentPane().add(canvas);
            f.setVisible(true);
        });
    }
}
