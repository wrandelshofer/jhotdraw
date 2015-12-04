/* @(#)FigureFactory.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.io;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Set;
import org.jhotdraw.collection.MapAccessor;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.Figure;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * FigureFactory.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface FigureFactory extends IdFactory {

    /**
     * Returns a comment which will be added to the beginning of the file.
     *
     * @return a comment or null
     */
    String createFileComment();

    /**
     * Maps a figure to an XML element name.
     *
     * @param f the figure
     * @return the name
     *
     * @throws java.io.IOException if the factory does not support this figure
     */
    String figureToName(Figure f) throws IOException;

    /**
     * Maps an XML element name to a figure.
     *
     * @param name the name
     * @return the figure
     *
     * @throws java.io.IOException if the factory does not support this name
     */
    Figure nameToFigure(String name) throws IOException;

    /**
     * Maps a key to a XML attribute name. The name used for persistent storage
     * may be different from the name defined in the key.
     *
     * @param f the figure
     * @param key the key
     * @return The name.
     *
     * @throws java.io.IOException if the factory does not support the key for
     * the specified figure
     */
    String keyToName(Figure f, MapAccessor<?> key) throws IOException;

    /**
     * Maps an XML attribute name to a key.
     *
     * @param f the figure
     * @param name the name
     * @return the key
     *
     * @throws java.io.IOException if the factory does not support the name for
     * the specified figure
     */
    MapAccessor<?> nameToKey(Figure f, String name) throws IOException;

    /**
     * Maps a key to a XML element name. The name used for persistent storage
     * may be different from the name defined in the key.
     * <p>
     * The name can be an empty String {@code ""} for the key returned by
     * {@link #figureNodeListKeys} if the figure has exactly one node list key.
     * In this case, the node list will be added directly as children to the
     * figure. The name of a node list key must not be equal to the name of a
     * figure, because child figure elements are also added as child elements.
     *
     * @param f the figure
     * @param key the key
     * @return The name.
     *
     * @throws java.io.IOException if the factory does not support the key for
     * the specified figure
     */
    String keyToElementName(Figure f, MapAccessor<?> key) throws IOException;

    /**
     * Maps an XML element name to a key.
     *
     * @param f the figure
     * @param name the name
     * @return the key
     *
     * @throws java.io.IOException if the factory does not support the name for
     * the specified figure
     */
    MapAccessor<?> elementNameToKey(Figure f, String name) throws IOException;

    /**
     * Maps a value to an XML attribute value.
     *
     * @param <T> the type of the value
     * @param key the key
     * @param value the value
     * @return the mapped attribute value
     *
     * @throws java.io.IOException if the factory does not support a mapping for
     * the specified key
     */
    <T> String valueToString(MapAccessor<T> key, T value) throws IOException;

    /**
     * Maps a value to a XML node list.
     * <p>
     * The node list may not contain elements with a name that conflicts with
     * the names returned by {@link #figureToName}.
     *
     * @param key the key
     * @param value the value
     * @param document the document for creating the node list.
     * @return the mapped attribute value
     *
     * @throws java.io.IOException if the factory does not support a mapping for
     * the specified key
     */
    List<Node> valueToNodeList(MapAccessor<?> key, Object value, Document document) throws IOException;

    /**
     * Maps a XML node list to a value.
     * <p>
     * The node list does not contain elements with a name that conflicts with
     * the names returned by {@link #figureToName}.
     *
     * @param <T> the type of the value
     * @param key the key
     * @param nodeList the nodeList
     * @return the mapped attribute value.
     *
     * @throws java.io.IOException if the factory does not support a mapping for
     * the specified key
     */
    <T> T nodeListToValue(MapAccessor<T> key, List<Node> nodeList) throws IOException;

    /**
     * Maps an XML attribute value to a value.
     *
     * @param <T> the type of the value
     * @param key the key
     * @param cdata the XML attribute value
     * @return the mapped value
     *
     * @throws java.io.IOException if the factory does not support a mapping for
     * the specified key
     */
    <T> T stringToValue(MapAccessor<T> key, String cdata) throws IOException;

    /**
     * Returns the default for the key. The default value used for persistent
     * storage may be different from the default value defined in the key.
     *
     * @param <T> The type of the value
     * @param key The key
     * @return the default value
     */
    <T> T getDefaultValue(MapAccessor<T> key);

    /**
     * Returns true if the specified value is the default for the given key.
     *
     * @param <T> The type of the value
     * @param key The key
     * @param value the value
     * @return true if the value is the default value
     */
    default <T> boolean isDefaultValue(MapAccessor<T> key, T value) {
        T defaultValue = key.getDefaultValue();
        return defaultValue == null ? value == null : (value == null ? false : defaultValue.equals(value));
    }

    /**
     * Returns all keys for the specified figure which should be converted into
     * element attributes.
     *
     * @param f The figure
     * @return an immutable set
     */
    Set<MapAccessor<?>> figureAttributeKeys(Figure f);

    /**
     * Returns all keys for the specified figure which should be converted into
     * a node list.
     *
     * @param f The figure
     * @return an immutable set
     */
    Set<MapAccessor<?>> figureNodeListKeys(Figure f);

    /**
     * Creates an external representation of the drawing.
     * <p>
     * Note: this method must not change the provided internal drawing.
     * <p>
     * The default implementation returns the same drawing.
     *
     * @param internal an internal representation of the drawing
     * @return An external representation of the drawing.
     * @throws java.io.IOException if no external representation can be created
     */
    default Drawing toExternalDrawing(Drawing internal) throws IOException {
        return internal;
    }

    /**
     * Creates an internal representation of the drawing.
     * <p>
     * Note: this method may change the provided external drawing.
     * <p>
     * The default implementation returns the same drawing.
     *
     * @param external an external representation of the drawing
     * @return An internal representation of the drawing.
     * @throws java.io.IOException if no internal representation can be created
     */
    default Drawing fromExternalDrawing(Drawing external) throws IOException {
        return external;
    }

    /**
     * Returns the stylesheets keys.
     *
     * @return The stylesheets key of the Drawing object. Return null if
     * stylesheets shall not be supported. The default implementation returns
     * {@link org.jhotdraw.draw.Drawing#AUTHOR_STYLESHEETS}.
     */
    default MapAccessor<List<URI>> getStylesheetsKey() {
        return Drawing.AUTHOR_STYLESHEETS;
    }

    /**
     * Returns the name of the object id attribute. The object id attribute is
     * used for referencing other objects in the XML file.
     *
     * @return name of the object id attribute
     */
    String getObjectIdAttribute();
}
