/* @(#)CssPaintConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.draw.key.CssRadialGradient;
import org.jhotdraw8.draw.key.CssColor;
import org.jhotdraw8.draw.key.CssLinearGradient;
import org.jhotdraw8.draw.key.Paintable;
import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;
import javafx.scene.paint.Color;
import org.jhotdraw8.io.IdFactory;

/**
 * CssPaintableConverter.
 * <p>
 * Parses the following EBNF from the
 * <a href="https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html">JavaFX
 * CSS Reference Guide</a>.
 * </p>
 * <pre>
 * Paintable := (Color|LinearGradient|RadialGradient|ImagePattern RepeatingImagePattern) ;
 * </pre>
 * <p>
 * FIXME currently only parses the Color and the LinearGradient productions
 * </p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssPaintableConverter implements Converter<Paintable> {

    @Nonnull
    private CssColorConverter colorConverter = new CssColorConverter(false);
    @Nonnull
    private CssLinearGradientConverter linearGradientConverter = new CssLinearGradientConverter();
    @Nonnull
    private CssRadialGradientConverter radialGradientConverter = new CssRadialGradientConverter();
    @Nonnull
    private XmlNumberConverter doubleConverter = new XmlNumberConverter();

    public void toString(@Nonnull Appendable out, IdFactory idFactory, @Nullable Paintable value) throws IOException {
        if (value == null) {
            out.append("none");
        } else if (Color.TRANSPARENT.equals(value)) {
            out.append("transparent");
        } else if (value instanceof CssColor) {
            CssColor c = (CssColor) value;
            colorConverter.toString(out, idFactory, c);
        } else if (value instanceof CssLinearGradient) {
            CssLinearGradient lg = (CssLinearGradient) value;
            linearGradientConverter.toString(out, idFactory, lg);
        } else if (value instanceof CssRadialGradient) {
            CssRadialGradient lg = (CssRadialGradient) value;
            radialGradientConverter.toString(out, idFactory, lg);
        } else {
            throw new UnsupportedOperationException("not yet implemented");
        }
    }

    @Nullable
    @Override
    public Paintable fromString(@Nullable CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        String str = buf.toString();

        if ("none".equals(str)) {
            buf.position(buf.length());
            return null;
        }

        int pos = buf.position();
        ParseException pe = null;
        try {
            return colorConverter.fromString(buf, idFactory);
        } catch (ParseException e) {
            pe = e;
        }
        try {
            buf.position(pos);
            return linearGradientConverter.fromString(buf, idFactory);
        } catch (ParseException e) {
            if (e.getErrorOffset() > pe.getErrorOffset()) {
                pe = e;
            }
        }
        try {
            buf.position(pos);
            return radialGradientConverter.fromString(buf, idFactory);
        } catch (ParseException e) {
            if (e.getErrorOffset() > pe.getErrorOffset()) {
                pe = e;
            }
        }
        throw pe;
    }

    @Nullable
    @Override
    public Paintable getDefaultValue() {
        return null;
    }

    @Nonnull
    @Override
    public String getHelpText() {
        String[] lines = ("Format of ⟨Paint⟩: none｜（⟨Color⟩｜ ⟨LinearGradient⟩｜ ⟨RadialGradient⟩"
                + "\n" + colorConverter.getHelpText()
                + "\n" + linearGradientConverter.getHelpText()
                + "\n" + radialGradientConverter.getHelpText()).split("\n");
        ;
        StringBuilder buf = new StringBuilder();
        Set<String> duplicateLines = new HashSet<>();
        for (String line : lines) {
            if (duplicateLines.add(line)) {
                if (buf.length() != 0) {
                    buf.append('\n');
                }
                buf.append(line);
            }
        }
        return buf.toString();
    }
}
