package org.jhotdraw8.geom.offsetline;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.geom.AWTPathBuilder;
import org.jhotdraw8.geom.Geom;
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
    public List<DynamicTest> offsetPathTests() {
        return Arrays.asList(
                dynamicTest("shape with coincident line - angle from seg 0 to 1 > 180°, angle from seg 1 to 2 = 360°", () -> doTest(
                        polylineOf(false, new double[][]{{220.0, 40.0, 0.0}, {190.0, 110.0, 0.0}, {210.0, 90.0, 0.0}, {200.0, 100.0, 0.0}, {280.0, 180.0, 0.0}}),
                        -7,
                        Arrays.asList(polylineOf(false, new double[][]{
                                        {226.4340152101264, 42.75743509005417, 0.0}
                                        , {209.16582399091348, 83.04988126821766, 0.7122671462660679}
                                        , {214.94974746830584, 94.94974746830583, 0.0}
                                        , {209.89949493661166, 100.00000000000001, 0.0}
                                        , {284.9497474683058, 175.05025253169416, 0.0}
                                })
                        )
                )),


                dynamicTest("b shape - last point coincides with another point", () -> doTest(
                        polylineOf(false, new double[][]{{190.0, 170.0, 0.0}, {210.0, 120.0, 0.0}, {230.0, 150.0, 0.0}, {190.0, 170.0, 0.0}}),
                        20.0,
                        Arrays.asList()
                )),

                dynamicTest("u", () -> doTest(
                        polylineOf(false, new double[][]{{130.0, 80.0, 0.0}, {130.0, 190.0, 0.0}, {200.0, 190.0, 0.0}, {200.0, 80.0, 0.0}}),
                        5.0,
                        Arrays.asList(polylineOf(false, new double[][]{
                                {125.0, 80.0, 0.0}
                                , {125.0, 190.0, -0.41421356237309503}
                                , {130.0, 195.0, 0.0}
                                , {200.0, 195.0, -0.41421356237309503}
                                , {205.0, 190.0, 0.0}
                                , {205.0, 80.0, 0.0}
                        }))
                )),

                dynamicTest("h", () -> doTest(
                        polylineOf(false, new double[][]{{130.0, 80.0, 0.0}, {130.0, 180.0, 0.0}, {210.0, 120.0, 0.0}, {260.0, 180.0, 0.0}}),
                        -15.0,
                        Arrays.asList(polylineOf(false, new double[][]{
                                {145.0, 80.0, 0.0}
                                , {145.0, 150.0, 0.0}
                                , {201.0, 108.0, 0.399284935042546}
                                , {221.52331919396065, 110.3972340050328, 0.0}
                                , {271.5233191939606, 170.3972340050328, 0.0}
                        }))
                )),


                dynamicTest("inside open p - medium gap", () -> doTest(
                        polylineOf(false, new double[][]{{110.0, 180.0, 0.0}, {110.0, 70.0, 0.0}, {220.0, 70.0, 0.0}, {220.0, 140.0, 0.0}, {130.0, 140.0, 0.0}}),
                        17.5,
                        Arrays.asList(
                                polylineOf(false, new double[][]{{127.5, 180, 0.0}, {127.5, 157.32050807568876, 0.0}}),
                                polylineOf(false, new double[][]{{127.5, 122.67949192431124, 0.0}, {127.5, 87.5, 0.0}, {202.5, 87.5, 0.0}, {202.5, 122.5, 0.0}, {130.0, 122.5, 0.0}})
                        )
                )),

                dynamicTest("inside open p - small gap", () -> doTest(
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

    private static PolyArcPath polylineOf(double[][] coords) {
        return polylineOf(false, coords);
    }

    private static PolyArcPath polylineOf(boolean closed, double[][] coords) {
        PolyArcPath p = new PolyArcPath();
        for (int i = 0; i < coords.length; i++) {
            double[] v = coords[i];
            p.addVertex(v[0], v[1], v[2]);
        }
        p.isClosed(closed);
        return p;
    }

    private void doTest(PolyArcPath input, double offset, List<PolyArcPath> expected) throws Exception, InterruptedException {
        final ContourBuilder pap = new ContourBuilder();
        //final Polyline raw = pap.createRawOffsetPline(input, offset);
        //final StringBuilder b = new StringBuilder();
        //dumpPline(raw,b);
        //System.err.println(b);

        List<PolyArcPath> actual = pap.parallelOffset(input, offset);
        boolean isEqual = expected.size() == actual.size();
        for (int i = 0; i < expected.size(); i++) {
            PolyArcPath e = expected.get(i);
            PolyArcPath a = actual.get(i);
            isEqual &= e.size() == a.size();
            if (!isEqual) {
                break;
            }
            for (int j = 0; j < e.size(); j++) {
                PlineVertex ev = e.get(j);
                PlineVertex av = a.get(j);
                isEqual &= Geom.almostEqual(ev.bulge(), av.bulge(), 1e-5);
                isEqual &= Geom.almostEqual(ev.getX(), av.getX(), 1e-5);
                isEqual &= Geom.almostEqual(ev.getY(), av.getY(), 1e-5);
                if (!isEqual) {
                    System.err.println("expected: " + ev);
                    System.err.println("actual:   " + av);
                    break;
                }
            }
        }

        if (!isEqual) {
            for (PolyArcPath p : actual) {
                System.out.println("polylineOf(" + p.isClosed() + ",new double[][]{");
                boolean first = true;
                for (PlineVertex v : p) {
                    if (first) {
                        first = false;
                    } else {
                        System.out.print(", ");
                    }
                    System.out.println("{" + v.getX() + "," + v.getY() + "," + v.bulge() + "}");
                }
                System.out.println("})");
            }
            SwingUtilities.invokeAndWait(() -> {
                JDialog f = new JDialog();
                f.setModal(true);
                f.setSize(new Dimension(600, 400));
                JComponent canvas = new JComponent() {
                    @Override
                    public void paint(Graphics gr) {
                        Graphics2D g = (Graphics2D) gr;
                        g.setColor(Color.BLACK);// original line
                        AWTPathBuilder b = new AWTPathBuilder();
                        Shapes.buildFromPathIterator(b, input.getPathIterator(null));
                        g.draw(b.build());

                        b = new AWTPathBuilder();
                        final PolyArcPath rawOffsetPline = pap.createRawOffsetPline(input, offset);
                        Shapes.buildFromPathIterator(b, rawOffsetPline.getPathIterator(null));
                        g.setColor(Color.PINK);// raw offset line
                        g.draw(b.build());


                        g.setColor(Color.MAGENTA);// final offset line
                        final List<OpenPolylineSlice> slices = pap.dualSliceAtIntersectsForOffset(input, pap.createRawOffsetPline(input, offset),
                                pap.createRawOffsetPline(input, -offset), offset);
                        for (OpenPolylineSlice s : slices) {
                            b = new AWTPathBuilder();
                            Shapes.buildFromPathIterator(b, s.pline.getPathIterator(null));
                            g.draw(b.build());
                        }
                    }
                };


                f.getContentPane().add(canvas);
                f.setVisible(true);
            });
        }

        //assertEquals(expected, actual);
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
}
