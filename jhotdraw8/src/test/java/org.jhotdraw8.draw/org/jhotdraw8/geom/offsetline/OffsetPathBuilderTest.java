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

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @NonNull
    @TestFactory
    public List<DynamicTest> offsetPathTests() {
        return Arrays.asList(
                dynamicTest("inside open p - split vertical line not done - NOK", () -> doTest(
                        polylineOf(false, new double[][]{{110.0, 180.0, 0.0}, {110.0, 70.0, 0.0}, {220.0, 70.0, 0.0}, {220.0, 140.0, 0.0}, {130.0, 140.0, 0.0}}),
                        17.5,
                        Arrays.asList(
                                polylineOf(false, new double[][]{{127.5, 180, 0.0}, {127.5, 160.5, 0.0}}),
                                polylineOf(false, new double[][]{{127.5, 122.67949192431124, 0.0}, {127.5, 87.5, 0.0}, {202.5, 87.5, 0.0}, {202.5, 122.5, 0.0}, {130.0, 122.5, 0.0}})
                        )
                )),

                dynamicTest("inside open p - split vertical line done", () -> doTest(
                        polylineOf(false, new double[][]{{110.0, 180.0, 0.0}, {110.0, 70.0, 0.0}, {220.0, 70.0, 0.0}, {220.0, 140.0, 0.0}, {130.0, 140.0, 0.0}}),
                        20.0,
                        Arrays.asList(
                                polylineOf(false, new double[][]{{130.0, 180.0, 0.0}, {130.0, 160.0, 0.0}}),
                                polylineOf(false, new double[][]{{130.0, 120.0, 0.0}, {130.0, 90.0, 0.0}, {200.0, 90.0, 0.0}, {200.0, 120.0, 0.0}, {130.0, 120.0, 0.0}}))
                )),
                dynamicTest("knot", () -> doTest(
                        polylineOf(false, new double[][]{{260.0, 180.0, 0.0}, {110.0, 120.0, 0.0}, {220.0, 60.0, 0.0}, {200.0, 220.0, 0.0}}),
                        -15.0,
                        Arrays.asList(polylineOf(false, new double[][]{{254.42913985468846, 193.92715036327888, 0.0}, {220.09266330988987, 180.19255974535946, 0.0}, {214.88416815070502, 221.8605210188381, 0.0}}),
                                polylineOf(false, new double[][]{{191.29888563739502, 168.67504867636148, 0.0}, {104.42913985468844, 133.92715036327888, 0.6345466534187385}, {102.8172180397914, 106.83156640628424, 0.0}, {212.8172180397914, 46.83156640628424, 0.6112142738578111}, {234.88416815070502, 61.86052101883813, 0.0}, {223.93920960070022, 149.4201894188766, 0.0}}))
                )),
                dynamicTest("rectangle outset", () -> doTest(
                        polylineOf(true, new double[][]{{110.0, 200.0, 0.0}, {110.0, 120.0, 0.0}, {210.0, 120.0, 0.0}, {210.0, 200.0, 0.0}}),
                        -12.5,
                        Arrays.asList(polylineOf(true, new double[][]{{97.5, 200.0, 0.0}, {97.5, 120.0, 0.41421356237309503}, {110.0, 107.5, 0.0}, {210.0, 107.5, 0.41421356237309503}, {222.5, 120.0, 0.0}, {222.5, 200.0, 0.41421356237309503}, {210.0, 212.5, 0.0}, {110.0, 212.5, 0.41421356237309503}}))
                )),
                dynamicTest("rectangle inset", () -> doTest(
                        polylineOf(true, new double[][]{{110.0, 200.0, 0.0}, {110.0, 120.0, 0.0}, {210.0, 120.0, 0.0}, {210.0, 200.0, 0.0}}),
                        25.0,
                        Arrays.asList(polylineOf(true, new double[][]{{135.0, 175.0, 0.0}, {135.0, 145.0, 0.0}, {185.0, 145.0, 0.0}, {185.0, 175.0, 0.0}}))
                ))
        );
    }

    private static Polyline polylineOf(double[][] coords) {
        return polylineOf(false, coords);
    }

    private static Polyline polylineOf(boolean closed, double[][] coords) {
        Polyline p = new Polyline();
        for (int i = 0; i < coords.length; i++) {
            double[] v = coords[i];
            p.addVertex(v[0], v[1], v[2]);
        }
        p.isClosed(closed);
        return p;
    }

    private void doTest(Polyline input, double offset, List<Polyline> expected) throws Exception, InterruptedException {
        List<Polyline> actual = new PapOffsetPathBuilder().parallelOffset(input, offset);
        assertEquals(expected, actual);
    }

    private void testOffsetLine(Polyline input, boolean closed, double offset, Polyline expected) throws Exception, InterruptedException {
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

    private void rawOffsetSegmentsTest(Polyline input, boolean closed, double offset, Polyline expected) throws Exception, InterruptedException {
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
