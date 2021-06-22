/*
 * @(#)IntersectPathIteratorLineTest.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.geom.intersect;

import javafx.scene.shape.Line;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.geom.SvgPaths;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class IntersectLinePathIteratorTest {
    @TestFactory
    public @NonNull List<DynamicTest> dynamicTestsIntersectLinePathIterator() {
        return Arrays.asList(
                dynamicTest("intersection", () -> testIntersectLinePathIterator(
                        new Line(5925, 425, 6085, 34),
                        "M5930,425 C5930,422.2404174804688,5927.759765625,420,5925,420 5922.240234375,420,5920,422.2404174804688,5920,425 5920,427.7595825195313,5922.240234375,430,5925,430 5927.759765625,430,5930,427.7595825195313,5930,425 Z",
                        IntersectionStatus.INTERSECTION)),
                dynamicTest("intersection", () -> testIntersectLinePathIterator(
                        new Line(5925, 425, 6085, 345),
                        "M5930,425 C5930,422.2404174804688,5927.759765625,420,5925,420 5922.240234375,420,5920,422.2404174804688,5920,425 5920,427.7595825195313,5922.240234375,430,5925,430 5927.759765625,430,5930,427.7595825195313,5930,425 Z",
                        IntersectionStatus.INTERSECTION)),
                dynamicTest("intersection", () -> testIntersectLinePathIterator(
                        new Line(5925 ,425,5765 ,505),
                        "M5930,425 C5930,422.2404174804688,5927.759765625,420,5925,420 5922.240234375,420,5920,422.2404174804688,5920,425 5920,427.7595825195313,5922.240234375,430,5925,430 5927.759765625,430,5930,427.7595825195313,5930,425 Z",
                        IntersectionStatus.INTERSECTION))
        );
    }



    public void testIntersectLinePathIterator(Line line, String path, IntersectionStatus expectedStatus) throws ParseException {
        IntersectionResultEx actual = IntersectLinePathIterator.intersectLinePathIteratorEx(
                line.getStartX(), line.getStartY(),
                line.getEndX(), line.getEndY(),
                SvgPaths.awtShapeFromSvgString(path).getPathIterator(null));
        assertEquals(expectedStatus,actual.getStatus());
    }
}
