/*
 * @(#)SvgTinySceneGraphReaderTest.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.svg.io;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.svg.figure.SvgDefaultableFigure;
import org.jhotdraw8.svg.figure.SvgRectFigure;
import org.jhotdraw8.svg.key.SvgDefaultablePaintStyleableMapAccessor;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class FigureSvgTinyReaderTest {

    @TestFactory
    public @NonNull List<DynamicTest> dynamicTestsSvgWithDefaultableAttributes() {
        return Arrays.asList(
                dynamicTest("rect with fill value", () -> testDefaultable(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                                "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" baseProfile=\"tiny\" version=\"1.2\">\n" +
                                "  <rect id=\"r\" fill=\"#ff0000\" height=\"200\" width=\"100\" x=\"10\" y=\"20\"/>\n" +
                                "</svg>\n", "r", SvgRectFigure.FILL_KEY,
                        CssColor.valueOf("#ff0000"))),
                dynamicTest("rect inherits fill value from svg element", () -> testDefaultable(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                                "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"" +
                                " baseProfile=\"tiny\" version=\"1.2\"" +
                                " fill=\"#ff0000\">\n" +
                                "  <rect id=\"r\" height=\"200\" width=\"100\" x=\"10\" y=\"20\"/>\n" +
                                "</svg>\n", "r", SvgRectFigure.FILL_KEY,
                        CssColor.valueOf("#ff0000"))),
                dynamicTest("rect inherits fill value from g element", () -> testDefaultable(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                                "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"" +
                                " baseProfile=\"tiny\" version=\"1.2\">\n" +
                                " <g fill=\"#ff0000\">\n" +
                                "  <rect id=\"r\" height=\"200\" width=\"100\" x=\"10\" y=\"20\"/>\n" +
                                "</g>\n"
                                + "</svg>\n",
                        "r", SvgRectFigure.FILL_KEY,
                        CssColor.valueOf("#ff0000")))
        );
    }

    private <T extends Paintable> void testDefaultable(String svg, String id, SvgDefaultablePaintStyleableMapAccessor<T> key, T expected) throws IOException {
        FigureSvgTinyReader instance = new FigureSvgTinyReader();
        Figure drawing = instance.read(new StreamSource(new StringReader(svg)));
        for (Figure f : drawing.depthFirstIterable()) {
            if (f instanceof SvgDefaultableFigure) {
                SvgDefaultableFigure df = (SvgDefaultableFigure) f;
                if (id.equals(f.getId())) {
                    Paintable actual = df.getDefaultableStyled(key);
                    assertEquals(expected, actual);
                }
            }
        }
    }

}
