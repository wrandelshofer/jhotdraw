/* @(#)BezierNodeList.java
 * Copyright © 2017 by the authors and contributors ofCollection JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
public class XmlBezierNodeListConverter implements Converter<ImmutableList<BezierNode>> {

    private final boolean nullable;

    public XmlBezierNodeListConverter(boolean nullable) {
        this.nullable = nullable;
    }

    @Nullable
    @Override
    public ImmutableList<BezierNode> fromString(@Nonnull CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        String input = buf.toString();
        CssTokenizer tt = new CssTokenizer(new CharBufferReader(buf));

        ImmutableList<BezierNode> p = null;
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
        p = builder.build();

        buf.position(buf.limit());

        return p;
    }

    @Override
    public void toString(@Nonnull Appendable out, IdFactory idFactory,
                         @Nullable ImmutableList<BezierNode> value) throws IOException {
        if (value == null) {
            if (!nullable) {
                throw new IllegalArgumentException("value is null");
            }
            out.append("none");
            return;
        }

        out.append(Shapes.doubleSvgStringFromAWT(new BezierNodePath(value).getPathIterator(null)));// we lose smooth!

    }

    @Override
    public ImmutableList<BezierNode> getDefaultValue() {
        return ImmutableList.emptyList();
    }
}
