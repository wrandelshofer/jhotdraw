/* @(#)AbstractDrawingView.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw;

import javafx.beans.InvalidationListener;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.ReadOnlySetWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.beans.NonnullProperty;
import org.jhotdraw8.beans.SimplePropertyBean;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.Layer;
import org.jhotdraw8.draw.handle.Handle;
import org.jhotdraw8.draw.handle.HandleType;
import org.jhotdraw8.draw.input.ClipboardInputFormat;
import org.jhotdraw8.draw.input.ClipboardOutputFormat;
import org.jhotdraw8.draw.io.BitmapExportOutputFormat;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.draw.tool.Tool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

/**
 * AbstractDrawingView.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractDrawingView extends SimplePropertyBean implements DrawingView {

    @Nonnull
    private ObjectProperty<String> helpText = new SimpleObjectProperty<String>(this, HELP_TEXT_PROPERTY);
    @Nonnull
    private ObjectProperty<ClipboardOutputFormat> clipboardOutputFormat = new SimpleObjectProperty<>(this, CLIPBOARD_OUTPUT_FORMAT_PROPERTY, new BitmapExportOutputFormat());
    @Nonnull
    private ObjectProperty<ClipboardInputFormat> clipboardInputFormat = new SimpleObjectProperty<>(this, CLIPBOARD_INPUT_FORMAT_PROPERTY);
    /**
     * The selectedFiguresProperty holds the list of selected figures in the
     * sequence they were selected by the user.
     */
    private final ReadOnlySetProperty<Figure> selectedFigures = new ReadOnlySetWrapper<>(this, SELECTED_FIGURES_PROPERTY, FXCollections.observableSet(new LinkedHashSet<Figure>())).getReadOnlyProperty();

    private final ObjectProperty<Tool> tool = new SimpleObjectProperty<>(this, TOOL_PROPERTY);
    private NonnullProperty<CssColor> handleColor = new NonnullProperty<CssColor>(this, HANDLE_COLOR_PROPERTY,
            CssColor.valueOf(Preferences.userNodeForPackage(AbstractDrawingView.class).get(HANDLE_COLOR_PROPERTY, "blue"))) {
        @Override
        public void set(CssColor newValue) {
            super.set(newValue);
            Preferences.userNodeForPackage(AbstractDrawingView.class).put(HANDLE_COLOR_PROPERTY, newValue.getName());
            recreateHandles();
        }
    };
    private IntegerProperty handleSize = new SimpleIntegerProperty(
            this, HANDLE_SIZE_PROPERTY,
            Preferences.userNodeForPackage(AbstractDrawingView.class).getInt(HANDLE_SIZE_PROPERTY, 5)) {
        @Override
        public void set(int newValue) {
            super.set(newValue);
            Preferences.userNodeForPackage(AbstractDrawingView.class).putInt(HANDLE_SIZE_PROPERTY, newValue);
            recreateHandles();
        }
    };
    private IntegerProperty handleStrokeWidth = new SimpleIntegerProperty(
            this, HANDLE_STROKE_WDITH_PROPERTY,
            Preferences.userNodeForPackage(AbstractDrawingView.class).getInt(HANDLE_STROKE_WDITH_PROPERTY, 1)) {
        @Override
        public void set(int newValue) {
            super.set(newValue);
            Preferences.userNodeForPackage(AbstractDrawingView.class).putInt(HANDLE_STROKE_WDITH_PROPERTY, newValue);
            recreateHandles();
        }
    };
    {
        tool.addListener((observable, oldValue, newValue) -> updateTool(oldValue, newValue));
    }

    private final ObjectProperty<Handle> activeHandle = new SimpleObjectProperty<>(this, ACTIVE_HANDLE_PROPERTY);
    private final NonnullProperty<HandleType> handleType = new NonnullProperty<>(this, HANDLE_TYPE_PROPERTY, HandleType.RESIZE);
    private final ObjectProperty<HandleType> leadHandleType = new SimpleObjectProperty<>(this, HANDLE_TYPE_PROPERTY, HandleType.RESIZE);

    private final ObjectProperty<HandleType> anchorHandleType = new SimpleObjectProperty<>(this, HANDLE_TYPE_PROPERTY, HandleType.RESIZE);

    {
        InvalidationListener listener = observable -> {
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

    @Nonnull
    @Override
    public ObjectProperty<ClipboardInputFormat> clipboardInputFormatProperty() {
        return clipboardInputFormat;
    }

    @Nonnull
    @Override
    public ObjectProperty<ClipboardOutputFormat> clipboardOutputFormatProperty() {
        return clipboardOutputFormat;
    }

    public void cut() {
        copy();
        final List<Figure> selectedFigures = new ArrayList<>(getSelectedFigures());
        DrawingModel m = getModel();
        for (Figure f : selectedFigures) {
            if (f.isDeletable()) {
                for (Figure d : f.preorderIterable()) {
                    m.disconnect(d);
                }
                m.removeFromParent(f);
            }
        }
    }

    public void copy() {
        ClipboardOutputFormat out = getClipboardOutputFormat();
        if (out == null) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        } else {
            Map<DataFormat, Object> content = new LinkedHashMap<DataFormat, Object>() {
                private final static long serialVersionUID = 0L;

                @Override
                public Object put(@Nullable DataFormat key, Object value) {
                    if (key == null) {
                        throw new IllegalArgumentException("key == null");
                    }
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
        // Only paste if there is an editable layer.
        Layer layer = getActiveLayer();
        if (layer == null || !layer.isEditable()) {
            layer = null;
            for (Figure f : getDrawing().getChildren()) {
                if (f.isEditable() && (f instanceof Layer)) {
                    layer = (Layer) f;
                }
            }
            if (layer == null) {
                return;
            }
        }
        Clipboard cb = Clipboard.getSystemClipboard();
        ClipboardInputFormat in = getClipboardInputFormat();
        if (in != null) {
            try {
                in.read(cb, getModel(), getDrawing(), layer);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Nonnull
    @Override
    public ObjectProperty<Tool> toolProperty() {
        return tool;
    }

    @Nonnull
    @Override
    public ObjectProperty<Handle> activeHandleProperty() {
        return activeHandle;
    }

    @Nonnull
    @Override
    public NonnullProperty<HandleType> handleTypeProperty() {
        return handleType;
    }

    @Nonnull
    @Override
    public ObjectProperty<HandleType> leadHandleTypeProperty() {
        return leadHandleType;
    }

    @Nonnull
    @Override
    public ObjectProperty<HandleType> anchorHandleTypeProperty() {
        return anchorHandleType;
    }

    @Nonnull
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

    @Override
    public ObjectProperty<String> helpTextProperty() {
        return helpText;
    }

    @Override
    public IntegerProperty handleSizeProperty() {
        return handleSize;
    }

    @Override
    public IntegerProperty handleStrokeWidthProperty() {
        return handleStrokeWidth;
    }

    @Override
    public NonnullProperty<CssColor> handleColorProperty() {
        return handleColor;
    }
}
