/*
 * @(#)UMLFigureFactory.java
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and
 * contributors of the JHotDraw project ("the copyright holders").
 * You may not use, copy or modify this software, except in
 * accordance with the license agreement you entered into with
 * the copyright holders. For details see accompanying license terms.
 */

package org.jhotdraw.samples.uml;

import java.util.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.connector.ChopBezierConnector;
import org.jhotdraw.draw.connector.ChopDiamondConnector;
import org.jhotdraw.draw.connector.ChopEllipseConnector;
import org.jhotdraw.draw.connector.ChopRectangleConnector;
import org.jhotdraw.draw.connector.ChopRoundRectangleConnector;
import org.jhotdraw.draw.connector.ChopTriangleConnector;
import org.jhotdraw.draw.connector.RelativeConnector;
import org.jhotdraw.draw.decoration.ArrowTip;
import org.jhotdraw.draw.liner.CurvedLiner;
import org.jhotdraw.draw.liner.ElbowLiner;
import org.jhotdraw.draw.liner.SelfConnectionLiner;
import org.jhotdraw.draw.locator.BezierLabelLocator;
import org.jhotdraw.samples.uml.figures.UMLClassFigure;
import org.jhotdraw.samples.uml.figures.UMLInterfaceFigure;
import org.jhotdraw.samples.uml.figures.UMLLabeledLineConnectionFigure;
import org.jhotdraw.samples.uml.figures.UMLSeparatorLineFigure;
import org.jhotdraw.xml.DefaultDOMFactory;
/**
 * UMLFigureFactory.
 *
 * @author  Werner Randelshofer
 * @version $Id: UMLFigureFactory.java,v 1.3 2009/11/01 20:04:22 cfm1 Exp $
 */
public class UMLFigureFactory extends DefaultDOMFactory {
    private final static Object[][] classTagArray = {
        { DefaultDrawing.class, "drawing" },
        { QuadTreeDrawing.class, "drawing" },
        { DiamondFigure.class, "diamond" },
        { TriangleFigure.class, "triangle" },
        { BezierFigure.class, "bezier" },
        { RectangleFigure.class, "r" },
        { RoundRectangleFigure.class, "rr" },
        { LineFigure.class, "l" },
        { BezierFigure.class, "b" },
        { LineConnectionFigure.class, "lnk" },
        { EllipseFigure.class, "e" },
        { TextFigure.class, "t" },
        { LabelFigure.class, "lbl" },
        { TextAreaFigure.class, "ta" },
        { ImageFigure.class, "image" },
        { GroupFigure.class, "g" },


        { ArrowTip.class, "arrowTip" },
        { ChopRectangleConnector.class, "rConnector" },
        { ChopEllipseConnector.class, "ellipseConnector" },
        { ChopRoundRectangleConnector.class, "rrConnector" },
        { ChopTriangleConnector.class, "triangleConnector" },
        { ChopDiamondConnector.class, "diamondConnector" },
        { ChopBezierConnector.class, "bezierConnector" },
        { RelativeConnector.class, "relativeConnector" },

        { ElbowLiner.class, "elbowLiner" },
        { CurvedLiner.class, "curvedLiner" },
        { SelfConnectionLiner.class, "selfLiner" },

        { UMLSeparatorLineFigure.class, "separator" },
        { UMLClassFigure.class, "class" },
        { UMLInterfaceFigure.class, "interface" },
        { LabeledLineConnectionFigure.class, "conn" },
        { UMLLabeledLineConnectionFigure.class, "uconn" },
        { BezierLabelLocator.class, "bll"},

    };
    private final static Object[][] enumTagArray = {
        { AttributeKeys.StrokePlacement.class, "strokePlacement" },
        { AttributeKeys.StrokeType.class, "strokeType" },
        { AttributeKeys.Underfill.class, "underfill" },
        { AttributeKeys.Orientation.class, "orientation" },
    };

    /** Creates a new instance. */
    public UMLFigureFactory() {
        for (Object[] o : classTagArray) {
            addStorableClass((String) o[1], (Class) o[0]);
        }
        for (Object[] o : enumTagArray) {
            addEnumClass((String) o[1], (Class) o[0]);
        }
    }
}
