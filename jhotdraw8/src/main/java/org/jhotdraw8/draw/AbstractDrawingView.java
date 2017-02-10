/* @(#)AbstractDrawingView.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.ReadOnlySetWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import org.jhotdraw8.app.EditableComponent;
import org.jhotdraw8.beans.NonnullProperty;
import org.jhotdraw8.beans.SimplePropertyBean;
import static org.jhotdraw8.draw.DrawingView.ACTIVE_HANDLE_PROPERTY;
import static org.jhotdraw8.draw.DrawingView.HANDLE_TYPE_PROPERTY;
import static org.jhotdraw8.draw.DrawingView.MULTI_HANDLE_TYPE_PROPERTY;
import static org.jhotdraw8.draw.DrawingView.SELECTED_FIGURES_PROPERTY;
import static org.jhotdraw8.draw.DrawingView.TOOL_PROPERTY;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.handle.Handle;
import org.jhotdraw8.draw.handle.HandleType;
import org.jhotdraw8.draw.input.ClipboardInputFormat;
import org.jhotdraw8.draw.input.ClipboardOutputFormat;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.draw.io.BitmapExportOutputFormat;
import org.jhotdraw8.draw.tool.Tool;

/**
 * AbstractDrawingView.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public abstract class AbstractDrawingView extends SimplePropertyBean implements DrawingView {

    private ObjectProperty<ClipboardOutputFormat> clipboardOutputFormat = new SimpleObjectProperty<>(this, CLIPBOARD_OUTPUT_FORMAT_PROPERTY, new BitmapExportOutputFormat());
    private ObjectProperty<ClipboardInputFormat> clipboardInputFormat = new SimpleObjectProperty<>(this, CLIPBOARD_INPUT_FORMAT_PROPERTY);
    /**
     * The selectedFiguresProperty holds the list of selected figures in the
     * sequence they were selected by the user.
     */
    private final ReadOnlySetProperty<Figure> selectedFigures = new ReadOnlySetWrapper<>(this, SELECTED_FIGURES_PROPERTY, FXCollections.observableSet(new LinkedHashSet<Figure>())).getReadOnlyProperty();


    private final ObjectProperty<Tool> tool = new SimpleObjectProperty<>(this, TOOL_PROPERTY);

    {
        tool.addListener((observable, oldValue, newValue) -> updateTool(oldValue, newValue));
    }
    private final ObjectProperty<Handle> activeHandle = new SimpleObjectProperty<>(this, ACTIVE_HANDLE_PROPERTY);
    private final NonnullProperty<HandleType> handleType = new NonnullProperty<>(this, HANDLE_TYPE_PROPERTY, HandleType.RESIZE);
    private final ObjectProperty<HandleType> leadHandleType = new SimpleObjectProperty<>(this, HANDLE_TYPE_PROPERTY, HandleType.RESIZE);

    private final ObjectProperty<HandleType> anchorHandleType = new SimpleObjectProperty<>(this, HANDLE_TYPE_PROPERTY, HandleType.RESIZE);

    {
        InvalidationListener listener=observable-> {
            recreateHandles();
            invalidateHandles();
            repaint();
        };
        selectedFigures.addListener(listener);
        handleType.addListener(listener);
       anchorHandleType.addListener(listener);
        leadHandleType.addListener(listener);
    }
    private final NonnullProperty<HandleType> multiHandleType = new NonnullProperty<>(this, MULTI_HANDLE_TYPE_PROPERTY, HandleType.SELECT);

    {
        multiHandleType.addListener((observable, oldValue, newValue) -> {
            invalidateHandles();
            repaint();
        });
    }

    @Override
    public ObjectProperty<ClipboardInputFormat> clipboardInputFormatProperty() {
        return clipboardInputFormat;
    }

    @Override
    public ObjectProperty<ClipboardOutputFormat> clipboardOutputFormatProperty() {
        return clipboardOutputFormat;
    }

    public void cut() {
        copy();
        final List<Figure> selectedFigures = new ArrayList<>(getSelectedFigures());
        DrawingModel m = getModel();
        for (Figure f : selectedFigures) {
            m.removeFromParent(f);
        }
    }

    public void copy() {
        ClipboardOutputFormat out = getClipboardOutputFormat();
        if (out == null) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        } else {
            Map<DataFormat, Object> content = new LinkedHashMap<DataFormat, Object>() {
                @Override
                public Object put(DataFormat key,Object value) {
                    if (key==null)throw new IllegalArgumentException("key == null");
                    return super.put(key, value); 
                }
                
            };
            try {
                final ObservableSet<Figure> selectedFigures = getSelectedFigures();

                out.write(content, getDrawing(), selectedFigures.isEmpty() ? FXCollections.singletonObservableList(getDrawing()) : selectedFigures);
                Clipboard.getSystemClipboard().setContent(content);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void paste() {
        Clipboard cb = Clipboard.getSystemClipboard();
        ClipboardInputFormat in = getClipboardInputFormat();
        if (in != null) {
            try {
                in.read(cb, getModel(), getDrawing(), getActiveLayer());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    

    @Override
    public ObjectProperty<Tool> toolProperty() {
        return tool;
    }

    @Override
    public ObjectProperty<Handle> activeHandleProperty() {
        return activeHandle;
    }

    @Override
    public NonnullProperty<HandleType> handleTypeProperty() {
        return handleType;
    }
    @Override
    public ObjectProperty<HandleType> leadHandleTypeProperty() {
        return leadHandleType;
    }
    @Override
    public ObjectProperty<HandleType> anchorHandleTypeProperty() {
        return anchorHandleType;
    }

    @Override
    public NonnullProperty<HandleType> multiHandleTypeProperty() {
        return multiHandleType;
    }    

    @Override
    public ReadOnlySetProperty<Figure> selectedFiguresProperty() {
        return selectedFigures;
    }

    protected abstract void invalidateHandles();

    protected abstract void repaint();

    protected abstract void updateTool(Tool oldValue, Tool newValue);
    


}
