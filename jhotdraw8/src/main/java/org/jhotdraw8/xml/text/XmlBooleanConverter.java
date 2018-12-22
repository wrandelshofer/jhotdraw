/* @(#)XmlBooleanConverter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.xml.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;

import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.text.Converter;

/**
 * Converts a {@code Boolean} into the XML String representation.
 * <p>
 * Reference:
 * <a href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#boolean">W3C: XML
 * Schema Part 2: Datatypes Second Edition: 3.2.5 boolean</a>
 * </p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class XmlBooleanConverter implements Converter<Boolean> {

    private static final long serialVersionUID = 1L;

    private final String trueString = "true";
    private final String falseString = "false";
    private final String oneString = "1";
    private final String zeroString = "0";

    /**
     * Creates a new instance.
     */
    public XmlBooleanConverter() {
    }

    @Override
    public void toString(@Nonnull Appendable buf, IdFactory idFactory, Boolean value) throws IOException {
        buf.append(value ? trueString : falseString);
    }

    @Nonnull
    @Override
    public Boolean fromString(@Nullable CharBuffer in, IdFactory idFactory) throws ParseException {
        int pos = in.position();
        StringBuilder out = new StringBuilder();
        while (in.remaining() > 0 && !Character.isWhitespace(in.charAt(0))) {
            out.append(in.get());
        }
        String str = out.toString();
        switch (str) {
            case trueString:
            case oneString:
                return true;
            case falseString:
            case zeroString:
                return false;
        }
        in.position(pos);
        throw new ParseException("\"" + trueString + "\", \"" + falseString + "\"" +
                "\"" + oneString + "\", \"" + zeroString + "\"" +
                " expected instead of \"" + str + "\".", pos);
    }

    @Nonnull
    @Override
    public Boolean getDefaultValue() {
        return false;
    }
}
