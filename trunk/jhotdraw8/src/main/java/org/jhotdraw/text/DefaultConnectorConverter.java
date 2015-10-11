/* @(#)DefaultConnectorConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.text;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.connector.CenterConnector;
import org.jhotdraw.draw.connector.ChopEllipseConnector;
import org.jhotdraw.draw.connector.ChopRectangleConnector;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.io.IdFactory;

/**
 * DefaultConnectorConverter.
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
public class DefaultConnectorConverter implements Converter<Connector> {

    private final PatternConverter formatter = new PatternConverter("{0,word} +{1,choice,0#center|1#rectangle|2#ellipse}", new XMLConverterFactory());

    private final static HashMap<Double, Class<?>> choiceToConnectorMap = new HashMap<>();
    private final static HashMap<Class<?>, Double> connectorToChoiceMap = new HashMap<>();

    static {
        choiceToConnectorMap.put(0.0, CenterConnector.class);
        choiceToConnectorMap.put(1.0, ChopRectangleConnector.class);
        choiceToConnectorMap.put(2.0, ChopEllipseConnector.class);
        for (Map.Entry<Double, Class<?>> entry : choiceToConnectorMap.entrySet()) {
            connectorToChoiceMap.put(entry.getValue(), entry.getKey());
        }
    }

    @Override
    public void toString(Appendable out, IdFactory idFactory, Connector value) throws IOException {
        double choice = connectorToChoiceMap.get(value.getClass());

        formatter.toStr(out, idFactory, idFactory.createId(value.getTarget()), choice);
    }

    @Override
    public Connector fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
        Object[] v = formatter.fromString(buf);
        if (v == null) {
            return null;
        }

        String id = (String) v[0];
        double choice = (Double) v[1];
        Object target = idFactory.getObject(id);
        if (!(target instanceof Figure)) {
            throw new IOException("bad id: " + id);
        }
        Figure targetFigure = (Figure) target;
        Class<?> clazz = choiceToConnectorMap.get(choice);
        try {
            Connector connector = (Connector) clazz.getConstructor(Figure.class).newInstance(targetFigure);
            return connector;
        } catch (NoSuchMethodException | SecurityException //
                | InstantiationException | IllegalAccessException//
                | IllegalArgumentException | InvocationTargetException ex) {
            throw new InternalError(ex);
        }
    }

}
