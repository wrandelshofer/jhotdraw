/* @(#)FigureSelectionListener.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */


package org.jhotdraw.draw.event;

/**
 * Interface implemented by observers of selection changes in 
 * {@link org.jhotdraw.draw.DrawingView} objects.
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
public interface FigureSelectionListener extends java.util.EventListener {
    public void selectionChanged(FigureSelectionEvent evt);
}
