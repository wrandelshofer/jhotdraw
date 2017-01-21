/* @(#)BezierNodeList.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import javafx.scene.shape.SVGPath;
import org.jhotdraw8.collection.ImmutableObservableList;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.geom.BezierNode;
import org.jhotdraw8.geom.BezierNodePath;
import org.jhotdraw8.geom.BezierNodePathBuilder;
import org.jhotdraw8.geom.Shapes;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.io.CharBufferReader;

/**
 * Converts an BezierNodeList path to a CSS String.
 * <p>
 * The null value will be converted to the CSS identifier "none".
 *
 * @author Werner Randelshofer
 * @version $Id: XmlBezierNodeListConverter.java 1336 2017-01-21 16:56:49Z
 * rawcoder $
 */
public class XmlBezierNodeListConverter implements Converter<ImmutableObservableList<BezierNode>> {

    private final boolean nullable;

    public XmlBezierNodeListConverter(boolean nullable) {
        this.nullable = nullable;
    }

    @Override
    public ImmutableObservableList<BezierNode> fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        String input = buf.toString();
        CssTokenizer tt = new CssTokenizer(new CharBufferReader(buf));

        ImmutableObservableList<BezierNode> p = null;
        if (tt.nextToken() == CssTokenizer.TT_IDENT) {
            if (!nullable) {
                throw new ParseException("String expected. " + tt.currentToken(), buf.position());
            }
            if ("none".equals(tt.currentStringValue())) {
                buf.position(buf.limit());

                return p;
            }
        }
        BezierNodePathBuilder builder = new BezierNodePathBuilder();
        Shapes.buildFromSvgString(builder, input);
        p = builder.getNodes();

        buf.position(buf.limit());

        return p;
    }

    @Override
    public void toString(Appendable out, IdFactory idFactory,
             ImmutableObservableList<BezierNode> value) throws IOException {
        if (value == null) {
            if (!nullable) {
                throw new IllegalArgumentException("value is null");
            }
            out.append("none");
            return;
        }

        out.append(Shapes.svgStringFromAWT(new BezierNodePath(value).getPathIterator(null)));// we lose smooth!

    }

    @Override
    public ImmutableObservableList<BezierNode> getDefaultValue() {
        return ImmutableObservableList.emptyList();
    }
}
