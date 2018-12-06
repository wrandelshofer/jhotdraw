/* @(#)PaletteColorSliderModel.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.gui.plaf.palette.colorchooser;

import org.jhotdraw.color.DefaultColorSliderModel;
import org.jhotdraw.gui.plaf.palette.PaletteColorSliderUI;

import javax.swing.BoundedRangeModel;
import javax.swing.JSlider;
import java.awt.color.ColorSpace;

/**
 * PaletteColorSliderModel.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PaletteColorSliderModel extends DefaultColorSliderModel {
    private static final long serialVersionUID = 1L;

    PaletteColorSliderModel(ColorSpace colorSpace) {
        super(colorSpace);
    }
    /**
     * Configures a JSlider for this model.
     * If the JSlider is already configured for another model,
     * it is unconfigured first.
     */
    @Override
    public void configureSlider(int componentIndex, JSlider slider) {
        if (slider.getClientProperty("colorSliderModel") != null) {
            ((DefaultColorSliderModel) slider.getClientProperty("colorSliderModel")).unconfigureSlider(slider);
        }
        if (!(slider.getUI() instanceof PaletteColorSliderUI)) {
            slider.setUI((PaletteColorSliderUI) PaletteColorSliderUI.createUI(slider));
        }
        BoundedRangeModel brm = getBoundedRangeModel(componentIndex);
        slider.setModel(brm);
        
        slider.putClientProperty("colorSliderModel", this);
        slider.putClientProperty("colorComponentIndex", componentIndex);
        addColorSlider(slider);
    }

}
