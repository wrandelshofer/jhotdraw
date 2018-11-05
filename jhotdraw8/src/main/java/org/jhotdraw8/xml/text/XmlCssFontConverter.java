/* @(#)XmlCssFontConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.xml.text;

import java.io.IOException;
import java.io.StringReader;
import java.nio.CharBuffer;
import java.text.ParseException;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.StreamCssTokenizer;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.CssFont;
import org.jhotdraw8.css.text.CssFontConverter;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.text.Converter;

/**
 * XmlCssFontConverter.
 * <p>
 * Parses the following EBNF from the
 * <a href="https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html">JavaFX
 * CSS Reference Guide</a>.
 * </p>
 * <pre>
 * CssFont := [FontStyle] [FontWeight] FontSize FontFamily ;
 * FontStyle := normal|italic|oblique;
 * FontWeight := normal|bold|bolder|lighter|100|200|300|400|500|600|700|800|900;
 * FontSize := Size;
 * FontFamily := Word|Quoted;
 * </pre>
 * </p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class XmlCssFontConverter extends CssFontConverter {

    public XmlCssFontConverter(boolean nullable) {
        super(nullable);
    }
}
