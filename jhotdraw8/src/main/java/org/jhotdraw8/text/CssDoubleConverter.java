/* @(#)CssDoubleConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.CssTokenizerInterface;
import org.jhotdraw8.io.DefaultUnitConverter;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.io.SimpleIdFactory;
import org.jhotdraw8.io.UnitConverter;

/**
 * CssDoubleConverter.
 * <p>
 * Parses the following EBNF from the
 * <a href="https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html">JavaFX
 * CSS Reference Guide</a>.
 * </p>
 * <pre>
 * Size := Double, [Unit] ;
 * Unit := ("px"|"mm"|"cm"|in"|"pt"|"pc"]"em"|"ex") ;
 * </pre>
 *
 * // FIXME should return a Size object and not just a Double.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssDoubleConverter implements Converter<Double> {

    private final CssSizeConverter sizeConverter;
    private final UnitConverter unitConverter;

    public CssDoubleConverter() {
        this(DefaultUnitConverter.getInstance(), false);
    }

    public CssDoubleConverter(UnitConverter unitConverter, boolean nullable) {
        this.unitConverter = unitConverter;
        sizeConverter = new CssSizeConverter(nullable);
    }

    @Override
    public Double fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        CssSize size = sizeConverter.fromString(buf, idFactory);
        return size == null ? null : unitConverter.convert(size.getValue(), size.getUnits(), "px");
    }

    @Override
    public Double getDefaultValue() {
        return 0.0;
    }

    @Override
    public void toString(Appendable out, IdFactory idFactory, Double value) throws IOException {
        CssSize size = value == null ? null : new CssSize(value, null);
        sizeConverter.toString(out, idFactory, size);
    }

    @Override
    public String getHelpText() {
        return "Format of ⟨Double⟩: ⟨double⟩";
    }

}
