package org.jhotdraw8.geom;

import javafx.collections.ObservableList;
import javafx.scene.shape.Polyline;
import org.jhotdraw8.annotation.NonNull;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class OffsetPathBuilderTest {
    //5138.5,360.0, 5025.0,360.0, 5025.0,360.0, 5015.0,360.0, 5015.0,360.0, 4845.0,360.0, 4845.0,360.0, 4835.0,360.0, 4835.0,360.0, 4645.0,360.0, 4645.0,360.0, 4635.0,360.0, 4635.0,360.0, 4455.0,360.0, 4455.0,360.0, 4445.0,360.0, 4445.0,360.0, 4361.5,360.0
    @TestFactory
    public @NonNull List<DynamicTest> dynamicTestsOffsetPath() {
        return Arrays.asList(
                dynamicTest("test case from editor", () -> testOffsetPath(
                        polylineOf(new double[]{
                                5138.5, 345.0,
                                5025.0, 345.0,
                                5015.0, 345.0,
                                4845.0, 345.0,
                                4835.0, 345.0,
                                4645.0, 345.0,
                                4635.0, 345.0,
                                4455.0, 345.0,
                                4445.0, 345.0,
                                4361.5, 345.0,
                        }),
                        15,
                        polylineOf(new double[]{
                                5138.5, 360.0, 5025.0, 360.0, 5025.0, 360.0, 5015.0, 360.0, 5015.0, 360.0, 4845.0, 360.0, 4845.0, 360.0, 4835.0, 360.0, 4835.0, 360.0, 4645.0, 360.0, 4645.0, 360.0, 4635.0, 360.0, 4635.0, 360.0, 4455.0, 360.0, 4455.0, 360.0, 4445.0, 360.0, 4445.0, 360.0, 4361.5, 360.0
                        })
                        )
                )
        );
    }


    private static Polyline polylineOf(double[] coords) {
        Polyline p = new Polyline();
        ObservableList<Double> ps = p.getPoints();
        for (int i = 0; i < coords.length; i++) {
            ps.add(coords[i]);
        }
        return p;
    }

    private void testOffsetPath(Polyline input, double offset, Polyline expected) throws Exception, InterruptedException {
        AwtPathBuilder ab = new AwtPathBuilder();
        OffsetPathBuilder instance = new OffsetPathBuilder(ab, offset);
        SvgPaths.buildFromPathIterator(instance, Shapes.awtShapeFromFX(input).getPathIterator(null));
        Path2D built = ab.build();
        Polyline actual = new Polyline();
        ObservableList<Double> pp = actual.getPoints();
        PathIterator it = built.getPathIterator(null);
        double[] coords = new double[6];
        while (!it.isDone()) {
            it.currentSegment(coords);
            pp.add(coords[0]);
            pp.add(coords[1]);
            it.next();
        }
        assertEquals(expected.getPoints(), actual.getPoints());
    }

}

