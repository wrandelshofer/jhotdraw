/* @(#)XmlRectangle2DConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import javafx.geometry.Rectangle2D;
import javax.annotation.Nonnull;
import org.jhotdraw8.io.IdFactory;

/**
 * Converts a {@code javafx.geometry.Rectangle2D} into a {@code String} and vice
 * versa.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class XmlRectangle2DConverter implements Converter<Rectangle2D> {

    private final PatternConverter formatter = new PatternConverter("{0,number} +{1,number} +{2,number} +{3,number}", new XmlConverterFactory());

    @Override
    public void toString(Appendable out, IdFactory idFactory, @Nonnull Rectangle2D value) throws IOException {
        formatter.toStr(out, idFactory, value.getMinX(), value.getMinY(), value.getWidth(), value.getHeight());
    }

    @Nonnull
    @Override
    public Rectangle2D fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        Object[] v = formatter.fromString(buf);

        return new Rectangle2D((double) v[0], (double) v[1], (double) v[2], (double) v[3]);
    }

    @Nonnull
    @Override
    public Rectangle2D getDefaultValue() {
        return new Rectangle2D(0, 0, 1, 1);
    }
}
