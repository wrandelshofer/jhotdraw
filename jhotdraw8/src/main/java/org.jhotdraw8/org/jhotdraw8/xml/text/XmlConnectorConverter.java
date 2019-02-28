/* @(#)XmlConnectorConverter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.xml.text;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.StreamCssTokenizer;
import org.jhotdraw8.css.text.CssLocatorConverter;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.connector.EllipseConnector;
import org.jhotdraw8.draw.connector.LocatorConnector;
import org.jhotdraw8.draw.connector.PathConnector;
import org.jhotdraw8.draw.connector.RectangleConnector;
import org.jhotdraw8.draw.locator.Locator;
import org.jhotdraw8.io.CharBufferReader;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.text.Converter;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.HashMap;
import java.util.function.Function;

/**
 * XmlConnectorConverter.
 * <p>
 * This converter supports the following connectors:
 * <ul>
 * <li>PathConnector</li>
 * <li>RectangleConnector</li>
 * <li>EllipseConnector</li>
 * </ul>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class XmlConnectorConverter implements Converter<Connector> {

    @Nonnull
    private CssLocatorConverter locatorConverter = new CssLocatorConverter();

    private final static HashMap<String, Function<Locator, Connector>> choiceToConnectorMap = new HashMap<>();
    private final static HashMap<Class<? extends Connector>, String> connectorToChoiceMap = new HashMap<>();

    static {
        connectorToChoiceMap.put(PathConnector.class, "path");
        connectorToChoiceMap.put(RectangleConnector.class, "rect");
        connectorToChoiceMap.put(EllipseConnector.class, "ellipse");
        choiceToConnectorMap.put("path", PathConnector::new);
        choiceToConnectorMap.put("rect", RectangleConnector::new);
        choiceToConnectorMap.put("ellipse", EllipseConnector::new);
    }

    @Override
    public void toString(@Nonnull Appendable out, IdFactory idFactory, @Nullable Connector value) throws IOException {
        if (value == null) {
            out.append("none");
        }
        String name = connectorToChoiceMap.get(value.getClass());
        if (name == null) {
            throw new IllegalArgumentException("unsupported connector:" + value);
        }
        out.append(name);
        if (value instanceof LocatorConnector) {
            out.append(" ");
            LocatorConnector lc = (LocatorConnector) value;
            locatorConverter.toString(out, idFactory, lc.getLocator());
        }
    }

    @Nullable
    @Override
    public Connector fromString(@Nullable CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        Connector c;
        CssTokenizer tt = new StreamCssTokenizer(new CharBufferReader(buf));
        c = parseConnector(tt);

        if (!buf.toString().trim().isEmpty()) {
            throw new ParseException("Locator: End expected, found:" + buf.toString(), buf.position());
        }
        return c;
    }

    @Nullable
    @Override
    public Connector getDefaultValue() {
        return null;
    }

    /**
     * Parses a Locator.
     *
     * @param tt the tokenizer
     * @return the parsed color
     * @throws ParseException if parsing fails
     * @throws IOException    if IO fails
     */
    @Nullable
    public Connector parseConnector(@Nonnull CssTokenizer tt) throws ParseException, IOException {
        Locator locator = null;
        Function<Locator, Connector> supplier;

        switch (tt.next()) {
            case CssTokenType.TT_IDENT:
                if ("none".equals(tt.currentString())) {
                    return null;
                }

                supplier = choiceToConnectorMap.get(tt.currentString());

                if (supplier == null) {
                    throw new ParseException("Connector: unsupported connector, found:" + tt.currentValue(), tt.getStartPosition());
                }
                break;
            default:
                throw new ParseException("Connector: identifier expected, found:" + tt.currentValue(), tt.getStartPosition());
        }

        locator = locatorConverter.parseLocator(tt);

        return supplier.apply(locator);
    }

}
