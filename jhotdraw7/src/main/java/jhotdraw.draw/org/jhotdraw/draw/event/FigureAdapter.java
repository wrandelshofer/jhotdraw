/* @(#)FigureAdapter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.draw.event;

/**
 * An abstract adapter class for receiving {@link FigureEvent}s. This class
 * exists as a convenience for creating {@link FigureListener} objects.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public class FigureAdapter implements FigureListener {
    @Override
    public void areaInvalidated(FigureEvent e) {
    }
    
    @Override
    public void attributeChanged(FigureEvent e) {
    }
    
    @Override
    public void figureAdded(FigureEvent e) {
    }
    
    @Override
    public void figureChanged(FigureEvent e) {
    }
    
    @Override
    public void figureRemoved(FigureEvent e) {
    }
    
    @Override
    public void figureRequestRemove(FigureEvent e) {
    }

    @Override
    public void figureHandlesChanged(FigureEvent e) {
    }
    
}
