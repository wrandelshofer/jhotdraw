/* @(#)ColorSliderTextFieldHandler.java 
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.gui.plaf.palette.colorchooser;

import org.jhotdraw.color.ColorSliderModel;

import javax.swing.BoundedRangeModel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
/**
 * This handler adjusts the value of a component in the color slider model,
 * when the user enters text into the text field.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class ColorSliderTextFieldHandler implements DocumentListener, ChangeListener {
    protected JTextField textField;
    protected ColorSliderModel ccModel;
    protected int component;
    
    public ColorSliderTextFieldHandler(JTextField textField, ColorSliderModel ccModel, int component) {
        this.textField = textField;
        this.ccModel = ccModel;
        this.component = component;
        
        textField.getDocument().addDocumentListener(this);
        ccModel.getBoundedRangeModel(component).addChangeListener(this);
    }
    
    @Override
    public void changedUpdate(DocumentEvent evt) {
        docChanged();
    }
    @Override
    public void removeUpdate(DocumentEvent evt) {
        docChanged();
    }
    @Override
    public void insertUpdate(DocumentEvent evt) {
        docChanged();
    }
    protected void docChanged() {
        if (textField.hasFocus()) {
            BoundedRangeModel brm = ccModel.getBoundedRangeModel(component);
            try {
                int value = Integer.decode(textField.getText()).intValue();
                if (brm.getMinimum() <= value && value <= brm.getMaximum()) {
                    brm.setValue(value);
                }
            } catch (NumberFormatException e) {
                // Don't change value if it isn't numeric.
            }
        }
    }
    @Override
    public void stateChanged(ChangeEvent e) {
        if (! textField.hasFocus()) {
            textField.setText(Integer.toString(ccModel.getBoundedRangeModel(component).getValue()));
        }
    }
}

