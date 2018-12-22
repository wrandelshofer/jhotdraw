/* @(#)XmlCssFontConverter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.xml.text;

import org.jhotdraw8.css.text.CssFontConverter;

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
