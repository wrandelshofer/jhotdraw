/* @(#)XmlConnectorConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.CssTokenizerInterface;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.connector.EllipseConnector;
import org.jhotdraw8.draw.connector.LocatorConnector;
import org.jhotdraw8.draw.connector.PathConnector;
import org.jhotdraw8.draw.connector.RectangleConnector;
import org.jhotdraw8.draw.locator.Locator;
import org.jhotdraw8.io.CharBufferReader;
import org.jhotdraw8.io.IdFactory;

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
 */
public class XmlConnectorConverter implements Converter<Connector> {

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
    public void toString(Appendable out, IdFactory idFactory, Connector value) throws IOException {
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

    @Override
    public Connector fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        Connector c;
        CssTokenizerInterface tt = new CssTokenizer(new CharBufferReader(buf));
        tt.setSkipWhitespaces(true);
        c = parseConnector(tt);

        if (!buf.toString().trim().isEmpty()) {
            throw new ParseException("Locator: End expected, found:" + buf.toString(), buf.position());
        }
        return c;
    }

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
     * @throws IOException if IO fails
     */
    public Connector parseConnector(CssTokenizerInterface tt) throws ParseException, IOException {
        Locator locator = null;
        Function<Locator, Connector> supplier;
        tt.setSkipWhitespaces(true);

        switch (tt.nextToken()) {
            case CssTokenizerInterface.TT_IDENT:
                if ("none".equals(tt.currentStringValue())) {
                    return null;
                }

                supplier = choiceToConnectorMap.get(tt.currentStringValue());

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
