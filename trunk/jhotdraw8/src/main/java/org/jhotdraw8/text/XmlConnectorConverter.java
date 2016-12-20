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
import java.util.function.Supplier;
import org.jhotdraw8.draw.connector.CenterConnector;
import org.jhotdraw8.draw.connector.ChopEllipseConnector;
import org.jhotdraw8.draw.connector.ChopRectangleConnector;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.io.IdFactory;

/**
 * XmlConnectorConverter.
 * <p>
 * This converter supports the following connectors:
 * <ul>
 * <li>CenterConnector</li>
 * <li>ChopRectangleConnector</li>
 * <li>ChopEllipseConnector</li>
 * </ul>
 *
 * @author Werner Randelshofer
 */
public class XmlConnectorConverter implements Converter<Connector> {

    private final PatternConverter formatter = new PatternConverter("{0,choice,0#center|1#rectangle|2#ellipse}", new XmlConverterFactory());

    private final static HashMap<Double, Supplier<Connector>> choiceToConnectorMap = new HashMap<>();
    private final static HashMap<Class<? extends Connector>, Double> connectorToChoiceMap = new HashMap<>();

    static {
        choiceToConnectorMap.put(0.0, CenterConnector::new);
        choiceToConnectorMap.put(1.0, ChopRectangleConnector::new);
        choiceToConnectorMap.put(2.0, ChopEllipseConnector::new);
        for (Map.Entry<Double, Supplier<Connector>> entry : choiceToConnectorMap.entrySet()) {
            connectorToChoiceMap.put(entry.getValue().get().getClass(), entry.getKey());
        }
    }

    @Override
    public void toString(Appendable out, IdFactory idFactory, Connector value) throws IOException {
        double choice = connectorToChoiceMap.get(value.getClass());

        formatter.toStr(out, idFactory, choice);
    }

    @Override
    public Connector fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        Object[] v = formatter.fromString(buf);
        if (v == null) {
            return null;
        }

        double choice = (Double) v[0];
        Supplier<Connector> clazz = choiceToConnectorMap.get(choice);
        if (clazz == null) {
            throw new ParseException("no connector found for " + buf, 0);
        }
        Connector connector = clazz.get();
        return connector;
    }

    @Override
    public Connector getDefaultValue() {
        return null;
    }
}
