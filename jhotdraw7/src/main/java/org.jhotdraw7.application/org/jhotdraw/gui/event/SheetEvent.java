/* @(#)SheetEvent.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.gui.event;

import org.jhotdraw.gui.JSheet;
import org.jhotdraw.gui.URIChooser;

import org.jhotdraw.annotation.Nullable;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.util.EventObject;

/**
 * SheetEvent.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class SheetEvent extends EventObject {
    private static final long serialVersionUID = 1L;
    private Object pane;
    private int option;
    @Nullable private Object value;
    @Nullable private Object inputValue;
    
    /**
     * Creates a new instance.
     */
    public SheetEvent(JSheet source) {
        super(source);
    }
    /**
     * Creates a new instance.
     */
    public SheetEvent(JSheet source, JFileChooser fileChooser, int option, @Nullable Object value) {
        super(source);
        this.pane = fileChooser;
        this.option = option;
        this.value = value;
    }
    /**
     * Creates a new instance.
     */
    public SheetEvent(JSheet source, URIChooser chooser, int option, @Nullable Object value) {
        super(source);
        this.pane = chooser;
        this.option = option;
        this.value = value;
    }
    /**
     * Creates a new instance.
     */
    public SheetEvent(JSheet source, JOptionPane optionPane, int option, @Nullable Object value, @Nullable Object inputValue) {
        super(source);
        this.pane = optionPane;
        this.option = option;
        this.value = value;
        this.inputValue = inputValue;
    }
    
    /**
     * Returns the pane on the sheet. This is either a JFileChooser, a URIChooser or a
     * JOptionPane.
     */
    public Object getPane() {
        return pane;
    }
    /**
     * Returns the JFileChooser pane on the sheet.
     */
    public JFileChooser getFileChooser() {
        return (JFileChooser) pane;
    }
    /**
     * Returns the URIChooser pane on the sheet.
     */
    public URIChooser getChooser() {
        return (URIChooser) pane;
    }
    /**
     * Returns the JOptionPane pane on the sheet.
     */
    public JOptionPane getOptionPane() {
        return (JOptionPane) pane;
    }
    /**
     * Returns the option that the JFileChooser or JOptionPane returned.
     */
    public int getOption() {
        return option;
    }
    /**
     * Returns the value that the JFileChooser or JOptionPane returned.
     */
    public Object getValue() {
        return value;
    }    
    /**
     * Returns the input value that the JOptionPane returned, if it wants input.
     */
    public Object getInputValue() {
        return inputValue;
    }    
}
