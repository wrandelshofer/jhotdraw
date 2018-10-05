/* @(#)Arrangeable.java
 * Copyright © 1996-2017 The authors and contributors of JHotDraw.
 * MIT License, CC-by License, or LGPL License.
 */

package org.jhotdraw.gui;

import java.beans.*;

/**
 * Arrangeable.
 * 
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Arrangeable {
    enum Arrangement { VERTICAL, HORIZONTAL, CASCADE };
    
    public void setArrangement(Arrangement newValue);
    public Arrangement getArrangement();
    
    public void addPropertyChangeListener(PropertyChangeListener l);
    public void removePropertyChangeListener(PropertyChangeListener l);
}
