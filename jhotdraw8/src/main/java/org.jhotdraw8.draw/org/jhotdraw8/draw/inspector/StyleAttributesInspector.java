/* @(#)StyleAttributesInspector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.inspector;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import org.jhotdraw8.css.SelectorModel;
import org.jhotdraw8.css.StylesheetsManager;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;

/**
 * FXML Controller class
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class StyleAttributesInspector extends AbstractStyleAttributesInspector<Figure>
        implements Inspector<DrawingView> {

    @Override
    protected Iterable<Figure> breadthFirstIterable() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void fireInvalidated(Figure f) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Object get(Figure f, WriteableStyleableMapAccessor<Object> finalSelectedAccessor) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected WriteableStyleableMapAccessor<?> getAccessor(SelectorModel<Figure> fsm, Figure f, String propertyNamespace, String propertyName) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Converter<?> getConverter(SelectorModel<Figure> selectorModel, Figure f, String namespace, String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected StylesheetsManager<Figure> getStyleManager() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected TextArea getTextArea() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void remove(Figure f, WriteableStyleableMapAccessor<Object> finalSelectedAccessor) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void set(Figure f, WriteableStyleableMapAccessor<Object> finalSelectedAccessor, Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void setHelpText(String helpText) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void showSelection() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Figure getRoot() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ObjectProperty<DrawingView> subjectProperty() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node getNode() {
        throw new UnsupportedOperationException();
    }
}
