/* @(#)BezierNodeList.java
 * Copyright © 2017 by the authors and contributors ofCollection JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import javafx.scene.shape.SVGPath;
import org.jhotdraw8.collection.ImmutableList;
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
 * @version $Id$
 */
public class CssBezierNodeListConverter implements Converter<ImmutableList<BezierNode>> {

    private final boolean nullable;

    public CssBezierNodeListConverter(boolean nullable) {
        this.nullable = nullable;
    }

    @Override
    public ImmutableList<BezierNode> fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        CssTokenizer tt = new CssTokenizer(new CharBufferReader(buf));

        ImmutableList<BezierNode> p = null;
        if (tt.nextToken() == CssTokenizer.TT_IDENT) {
            if (!nullable) {
                throw new ParseException("String expected. " + tt.currentToken(), buf.position());
            }
            if (!"none".equals(tt.currentStringValue())) {
                throw new ParseException("none or String expected. " + tt.currentToken(), buf.position());
            }
            p = null;
        } else {
            if (tt.currentToken() != CssTokenizer.TT_STRING) {
                throw new ParseException("Css String expected. " + tt.currentToken(), buf.position());
            }
            BezierNodePathBuilder builder=new BezierNodePathBuilder();
            Shapes.buildFromSvgString(builder, tt.currentStringValue());
            p = builder.getNodes();
        }
        buf.position(buf.limit());

        return p;
    }

    @Override
    public void toString(Appendable out, IdFactory idFactory, ImmutableList<BezierNode> value) throws IOException {
        if (value == null) {
            if (!nullable) {
                throw new IllegalArgumentException("value is null");
            }
            out.append("none");
            return;
        }
        
        out.append('"');
        out.append(Shapes.doubleSvgStringFromAWT(new BezierNodePath(value).getPathIterator(null)));// we lose smooth!
        out.append('"');
    }

    @Override
    public ImmutableList<BezierNode> getDefaultValue() {
        return ImmutableList.emptyList();
    }
}
