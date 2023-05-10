/*
 * @(#)ColorChooserAction.java  2.0  2006-06-07
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */

package org.jhotdraw.draw.action;

import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.event.FigureSelectionEvent;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;

import static org.jhotdraw.draw.action.AbstractViewAction.labels;


/**
 * ColorChooserAction.
 *
 * @author  Werner Randelshofer
 * @version 2.0 2006-06-07 Reworked.
 * <br>1.0 2004-03-02  Created.
 */
public class ColorChooserAction extends AbstractSelectedAction {
    private AttributeKey key;
    private static JColorChooser colorChooser;
    
    /** Creates a new instance. */
    public ColorChooserAction(DrawingEditor editor, AttributeKey key) {
        this(editor, key, null, null);
    }
    /** Creates a new instance. */
    public ColorChooserAction(DrawingEditor editor, AttributeKey key, Icon icon) {
        this(editor, key, null, icon);
    }
    /** Creates a new instance. */
    public ColorChooserAction(DrawingEditor editor, AttributeKey key, String name) {
        this(editor, key, name, null);
    }
    public ColorChooserAction(DrawingEditor editor, final AttributeKey key, String name, Icon icon) {
        super(editor);
        this.key = key;
        putValue(AbstractAction.NAME, name);
        //putValue(AbstractAction.MNEMONIC_KEY, new Integer('V'));
        putValue(AbstractAction.SMALL_ICON, icon);
        setEnabled(true);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent e) {
        if (colorChooser == null) {
            colorChooser = new JColorChooser();
        }
        Color initialColor = (Color) getEditor().getDefaultAttribute(key);
        if (initialColor == null) {
            initialColor = Color.red;
        }
        Color chosenColor = colorChooser.showDialog((Component) e.getSource(), labels.getString("drawColor"), initialColor);
        if (chosenColor != null) {
            changeAttribute(chosenColor);
        }
    }
    
    public void changeAttribute(Color value) {
        Drawing drawing = getDrawing();
        Iterator i = getView().getSelectedFigures().iterator();
        while (i.hasNext()) {
            Figure figure = (Figure) i.next();
            figure.getAttributes().put(key, value);
        }
        getEditor().setDefaultAttribute(key, value);
    }
    public void selectionChanged(FigureSelectionEvent evt) {
        //setEnabled(getView().getSelectionCount() > 0);
    }
}
