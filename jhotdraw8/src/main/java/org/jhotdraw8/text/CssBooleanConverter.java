/* @(#)CssBooleanConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;

import javax.annotation.Nonnull;
import org.jhotdraw8.io.IdFactory;

/**
 * Converts a {@code Boolean} into the CSS String representation.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssBooleanConverter implements Converter<Boolean> {

    private static final long serialVersionUID = 1L;

    private final String trueString = "true";
    private final String falseString = "false";

    /**
     * Creates a new instance.
     */
    public CssBooleanConverter() {
    }

    @Override
    public void toString(@Nonnull Appendable buf, IdFactory idFactory, Boolean value) throws IOException {
        buf.append(value ? trueString : falseString);
    }

    @Nonnull
    @Override
    public Boolean fromString(@Nonnull CharBuffer in, IdFactory idFactory) throws ParseException {
        int pos = in.position();
        StringBuilder out = new StringBuilder();
        while (in.remaining() > 0 && !Character.isWhitespace(in.charAt(0))) {
            out.append(in.get());
        }
        String str = out.toString();
        switch (str) {
            case trueString:
                return true;
            case falseString:
                return false;
            default:
                in.position(pos);
                throw new ParseException("\"" + trueString + "\" or \"" + falseString + "\" expected instead of \"" + str + "\".", pos);
        }
    }

    @Nonnull
    @Override
    public Boolean getDefaultValue() {
        return false;
    }

    @Nonnull
    @Override
    public String getHelpText() {
        return "Format of ⟨Boolean⟩: true｜false";
    }

}
