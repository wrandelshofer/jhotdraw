/*
 * @(#)UMLApplicationModel.java
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

import static org.jhotdraw.draw.AttributeKeys.END_CONNECTOR_STRATEGY;
import static org.jhotdraw.draw.AttributeKeys.START_CONNECTOR_STRATEGY;

import java.awt.Color;
import java.awt.Font;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JToolBar;
import javax.swing.UIManager;

import org.jhotdraw.app.Application;
import org.jhotdraw.app.DefaultApplicationModel;
import org.jhotdraw.app.View;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.DefaultDrawingEditor;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.ImageFigure;
import org.jhotdraw.draw.TextAreaFigure;
import org.jhotdraw.draw.TextFigure;
import org.jhotdraw.draw.action.ButtonFactory;
import org.jhotdraw.draw.liner.ElbowLiner;
import org.jhotdraw.draw.tool.ConnectionTool;
import org.jhotdraw.draw.tool.CreationTool;
import org.jhotdraw.draw.tool.DelegationSelectionTool;
import org.jhotdraw.draw.tool.ImageTool;
import org.jhotdraw.draw.tool.TextAreaCreationTool;
import org.jhotdraw.draw.tool.TextCreationTool;
import org.jhotdraw.draw.tool.Tool;
import org.jhotdraw.samples.uml.figures.UMLClassFigure;
import org.jhotdraw.samples.uml.figures.UMLInterfaceFigure;
import org.jhotdraw.samples.uml.figures.UMLLabeledLineConnectionFigure;
import org.jhotdraw.util.ResourceBundleUtil;
/**
 * UMLApplicationModel.
 *
 *
 *
 * @author Werner Randelshofer.
 * @version $Id: UMLApplicationModel.java,v 1.4 2009/11/01 20:04:22 cfm1 Exp $
 */
public class UMLApplicationModel extends DefaultApplicationModel {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * This editor is shared by all views.
     */
    private DefaultDrawingEditor sharedEditor;

    /** Creates a new instance. */
    public UMLApplicationModel() {
    }

    public DefaultDrawingEditor getSharedEditor() {
        if (sharedEditor == null) {
            sharedEditor = new DefaultDrawingEditor();
        }
        return sharedEditor;
    }

    public void initView(Application a, View p) {
        if (a.isSharingToolsAmongViews()) {
            ((UMLView) p).setEditor(getSharedEditor());
        }
    }
    /**
     * Creates toolbars for the application.
     * This class always returns an empty list. Subclasses may return other
     * values.
     */
    public List<JToolBar> createToolBars(Application a, View pr) {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.samples.uml.Labels");
        UMLView p = (UMLView) pr;

        DrawingEditor editor;
        if (p == null) {
            editor = getSharedEditor();
        } else {
            editor = p.getEditor();
        }

        LinkedList<JToolBar> list = new LinkedList<JToolBar>();
        JToolBar tb;
        tb = new JToolBar();
        addCreationButtonsTo(tb, editor);
        tb.setName(labels.getString("window.drawToolBar.title"));
        list.add(tb);
        tb = new JToolBar();
        ButtonFactory.addAttributesButtonsTo(tb, editor);
        tb.setName(labels.getString("window.attributesToolBar.title"));
        list.add(tb);
        tb = new JToolBar();
        ButtonFactory.addAlignmentButtonsTo(tb, editor);
        tb.setName(labels.getString("window.alignmentToolBar.title"));
        list.add(tb);
        return list;
    }
    private void addCreationButtonsTo(JToolBar tb, DrawingEditor editor) {
        addDefaultCreationButtonsTo(tb, editor,
                ButtonFactory.createDrawingActions(editor),
                ButtonFactory.createSelectionActions(editor)
                );
    }
    public void addDefaultCreationButtonsTo(JToolBar tb, final DrawingEditor editor,
            Collection<Action> drawingActions, Collection<Action> selectionActions) {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.samples.uml.Labels");

        Tool selectionTool = new DelegationSelectionTool(
                drawingActions, selectionActions);


        ButtonFactory.addSelectionToolTo(tb, editor,selectionTool);
        tb.addSeparator();


        UMLLabeledLineConnectionFigure xx;
        UMLLabeledLineConnectionFigure yy;
        TextCreationTool tct = null;

        Font f = UIManager.getFont("TextField.font");
        Font f2 = f.deriveFont(f.getSize());
        HashMap<AttributeKey,Object> attributes;
        attributes = new HashMap<AttributeKey,Object>();
        attributes.put(AttributeKeys.STROKE_COLOR, Color.black);
        attributes.put(AttributeKeys.TEXT_COLOR, Color.black);
        attributes.put(AttributeKeys.FONT_FACE, f2);
        attributes.put(AttributeKeys.FONT_SIZE, (double)f.getSize2D());


        ButtonFactory.addToolTo(tb, editor, new CreationTool(new UMLClassFigure(), attributes), "edit.createRectangle", labels);
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new UMLInterfaceFigure(), attributes), "edit.createRoundRectangle", labels);
        ButtonFactory.addToolTo(tb, editor, new ConnectionTool(xx=new UMLLabeledLineConnectionFigure(), attributes), "edit.createLineConnection", labels);
        ButtonFactory.addToolTo(tb, editor, new ConnectionTool(yy=new UMLLabeledLineConnectionFigure()), "edit.createElbowConnection", labels);
        ButtonFactory.addToolTo(tb, editor, tct = new TextCreationTool(new TextFigure(), attributes), "edit.createText", labels);
        ButtonFactory.addToolTo(tb, editor, new TextAreaCreationTool(new TextAreaFigure(), attributes), "edit.createTextArea", labels);
        ButtonFactory.addToolTo(tb, editor, new ImageTool(new ImageFigure()), "edit.createImage", labels);


        TextFigure prototypeTextFigure = (TextFigure)tct.getPrototype();
        prototypeTextFigure.set(AttributeKeys.FONT_FACE, f2);
        prototypeTextFigure.set(AttributeKeys.FONT_SIZE, (double)f2.getSize2D());

        xx.createLabel("", f2, true);
        xx.createLabel("", f2, false);
        xx.set(START_CONNECTOR_STRATEGY, "RectilinearConnectorStrategy");
        xx.set(END_CONNECTOR_STRATEGY, "RectilinearConnectorStrategy");

        yy.createLabel("", f2, true);
        yy.createLabel("", f2, false);
        yy.setLiner(new ElbowLiner());
        yy.set(START_CONNECTOR_STRATEGY, "RectilinearConnectorStrategy");
        yy.set(END_CONNECTOR_STRATEGY, "RectilinearConnectorStrategy");
    }
}
