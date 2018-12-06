/* @(#)FigureSelectionEvent.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.draw.event;

import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;

import java.util.Set;

/**
 * An {@code EventObject} sent to {@link FigureSelectionListener}s.
 *
 * <hr>
 * <b>Design Patterns</b>
 *
 * <p><em>Observer</em><br>
 * Selection changes of {@code DrawingView} are observed by user interface
 * components which act on selected figures.<br>
 * Subject: {@link org.jhotdraw.draw.DrawingView}; Observer:
 * {@link FigureSelectionListener}; Event: {@link FigureSelectionEvent}.
 * <hr>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class FigureSelectionEvent extends java.util.EventObject {
    private static final long serialVersionUID=1L;

    private Set<Figure> oldValue;
    private Set<Figure> newValue;

    /** Creates a new instance. */
    public FigureSelectionEvent(DrawingView source, Set<Figure> oldValue, Set<Figure> newValue) {
        super(source);
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public DrawingView getView() {
        return (DrawingView) source;
    }

    public Set<Figure> getOldSelection() {
        return oldValue;
    }

    public Set<Figure> getNewSelection() {
        return newValue;
    }
}
