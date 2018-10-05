/* @(#)PertFactory.java
 * Copyright © 1996-2017 The authors and contributors of JHotDraw.
 * MIT License, CC-by License, or LGPL License.
 */

package org.jhotdraw.samples.pert;

import org.jhotdraw.draw.locator.RelativeLocator;
import org.jhotdraw.draw.connector.LocatorConnector;
import org.jhotdraw.draw.connector.ChopRectangleConnector;
import org.jhotdraw.draw.decoration.ArrowTip;
import org.jhotdraw.samples.pert.figures.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.xml.*;
/**
 * PertFactory.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PertFactory extends DefaultDOMFactory {
    private static final Object[][] classTagArray = {
        { DefaultDrawing.class, "PertDiagram" },
        { TaskFigure.class, "task" },
        { DependencyFigure.class, "dep" },
        { ListFigure.class, "list" },
        { TextFigure.class, "text" },
        { GroupFigure.class, "g" },
        { TextAreaFigure.class, "ta" },
        { SeparatorLineFigure.class, "separator" },
        
        { ChopRectangleConnector.class, "rectConnector" },
        { LocatorConnector.class, "locConnector" },
        { RelativeLocator.class, "relativeLocator" },
        { ArrowTip.class, "arrowTip" }
    };
    
    /** Creates a new instance. */
    public PertFactory() {
        for (Object[] o : classTagArray) {
            addStorableClass((String) o[1], (Class) o[0]);
        }
    }
}
