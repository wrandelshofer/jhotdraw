package org.jhotdraw.draw.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;

import org.jhotdraw.app.action.ActionUtil;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.LineConnectionFigure;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.connector.ConnectorSubTracker;
import org.jhotdraw.draw.connector.RelativeConnector;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * @author C.F.Morrison
 *         <p>
 *         July 1, 2009
 *         <p>
 *         <i> Code line length 120 </i>
 *         <p>
 */
public class ConnectorStrategyAction extends AttributeAction {

    private static final long    serialVersionUID = 1L;
    private ArrayList<Connector> storedConnectors;

    /** Creates a new instance. */
    /** Creates a new instance. */
    public ConnectorStrategyAction(DrawingEditor editor, AttributeKey key, Object value) {
        super(editor, key, value, null, null);
    }

    /** Creates a new instance. */
    public ConnectorStrategyAction(DrawingEditor editor, AttributeKey key, Object value, Icon icon) {
        super(editor, key, value, null, icon);
    }

    /** Creates a new instance. */
    public ConnectorStrategyAction(DrawingEditor editor, AttributeKey key, Object value, String name) {
        super(editor, key, value, name, null);
    }

    public ConnectorStrategyAction(DrawingEditor editor, AttributeKey key, Object value, String name, Icon icon) {
        super(editor, key, value, name, icon, null);
    }

    public ConnectorStrategyAction(DrawingEditor editor, AttributeKey key, Object value, String name, Icon icon,
            Action compatibleTextAction) {
        super(editor, key, value, name, icon, compatibleTextAction);
    }

    public ConnectorStrategyAction(DrawingEditor editor, Map<AttributeKey, Object> attributes, String name, Icon icon) {
        super(editor, attributes, name, icon);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jhotdraw.draw.action.AttributeAction#actionPerformed(java.awt.event
     * .ActionEvent)
     */
    @Override
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        storedConnectors = new ArrayList<Connector>();
        if (checkCompatility(attributes, getView().getSelectedFigures())) {
            applyAttributesTo(attributes, getView().getSelectedFigures());
        }
    }

    /**
     * Applies the specified attributes to the currently selected figures of the
     * drawing.
     *
     * @param a
     *            The attributes.
     * @param figures
     *            The figures to which the attributes are applied.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void applyAttributesTo(final Map<AttributeKey, Object> a, Set<Figure> figures) {
        for (final Map.Entry<AttributeKey, Object> entry : a.entrySet()) {
            getEditor().setDefaultAttribute(entry.getKey(), entry.getValue());
        }

        final ArrayList<Figure> selectedFigures = new ArrayList<Figure>(figures);
        final ArrayList<Object> restoreData = new ArrayList<Object>(selectedFigures.size());
        for (final Figure figure : selectedFigures) {
            restoreData.add(figure.getAttributesRestoreData());
            figure.willChange();
            for (final Map.Entry<AttributeKey, Object> entry : a.entrySet()) {
                figure.set(entry.getKey(), entry.getValue());
            }
            figure.changed();
        }
        final UndoableEdit edit = new AbstractUndoableEdit() {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public String getPresentationName() {
                String name = (String) getValue(ActionUtil.UNDO_PRESENTATION_NAME_KEY);
                if (name == null) {
                    name = (String) getValue(Action.NAME);
                }
                if (name == null) {
                    final ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
                    name = labels.getString("attribute.text");
                }
                return name;
            }

            @Override
            public void redo() {
                super.redo();
                for (final Figure figure : selectedFigures) {
                    restoreData.add(figure.getAttributesRestoreData());
                    figure.willChange();
                    for (final Map.Entry<AttributeKey, Object> entry : a.entrySet()) {
                        figure.set(entry.getKey(), entry.getValue());
                    }
                    figure.changed();
                }
            }

            @Override
            public void undo() {
                super.undo();
                final ArrayList<Figure> oldSelections = new ArrayList<Figure>();
                for (final Connector connector : storedConnectors) {
                    oldSelections.add(connector.getOwner());
                }

                ConnectorSubTracker connectorSubTracker = getEditor().getConnectorSubTracker();
                connectorSubTracker.restoreConnectors(oldSelections, storedConnectors);

                final Iterator<Object> iRestore = restoreData.iterator();
                for (final Figure figure : selectedFigures) {
                    figure.willChange();
                    figure.restoreAttributesTo(iRestore.next());
                    figure.changed();
                }
            }
        };
        getDrawing().fireUndoableEditHappened(edit);
    }

    /**
     * Checks the strategy changes for compatibility
     *
     * @param a
     *            The attributes.
     * @param figures
     *            The figures to which the attributes are to be applied.
     */
    @SuppressWarnings("unchecked")
    public boolean checkCompatility(final Map<AttributeKey, Object> a, Set<Figure> figures) {
        final Map.Entry<AttributeKey, Object> attrEntry = a.entrySet().iterator().next();
        final String key = attrEntry.getKey().getKey();
        final String newValue = (String) attrEntry.getValue();
        final boolean isStartStrategy = key.equals("startConnectorStrategy");

        final ArrayList<LineConnectionFigure> selectedFigures = new ArrayList<LineConnectionFigure>(figures.size());
        for (final Figure f : figures) {
            if (f instanceof LineConnectionFigure)
                selectedFigures.add((LineConnectionFigure) f);
        }

        final ArrayList<String> compatibleMsgs = new ArrayList<String>();
        final Collection<Figure> incompatibleFigures = ConnectorSubTracker.checkStrategyCompatibility(selectedFigures,
                newValue, isStartStrategy, compatibleMsgs);

        if (incompatibleFigures.size() != 0) {
            getView().clearSelection();
            getView().addToSelection(incompatibleFigures);
            final StringBuffer buff = new StringBuffer("ALL CHANGES REJECTED - INCOMPATIBLE LINES REMAIN SELECTED");
            buff.append("\n\n");
            for (final String msg : compatibleMsgs) {
                buff.append(msg).append("\n");
            }

            final String title = isStartStrategy ? "Start Connector Strategy Change" : "End Connector Strategy Change";

            JOptionPane.showMessageDialog(getView().getComponent(), buff.toString(), title, JOptionPane.ERROR_MESSAGE);
            return false;
        }

        for (final Figure f : selectedFigures) {
            final LineConnectionFigure conn = (LineConnectionFigure) f;
            if (isStartStrategy)
                storedConnectors.add(((RelativeConnector) conn.getStartConnector()).clone());
            else
                storedConnectors.add(((RelativeConnector) conn.getEndConnector()).clone());

        }

        return true;
    }
}
