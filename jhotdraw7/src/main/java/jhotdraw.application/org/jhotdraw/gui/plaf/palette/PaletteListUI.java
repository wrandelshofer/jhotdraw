/* @(#)PaletteListUI.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.gui.plaf.palette;

import javax.swing.JComponent;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicListUI;
import java.awt.Color;

/**
 * PaletteListUI.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PaletteListUI extends BasicListUI {
    /**
     * Returns a new instance of PaletteListUI.  PaletteListUI delegates are
     * allocated one per JList.
     *
     * @return A new ListUI implementation for the Windows look and feel.
     */
    public static ComponentUI createUI(JComponent list) {
        return new PaletteListUI();
    }
    @Override
    protected void installDefaults()
    {
        super.installDefaults();

        PaletteLookAndFeel.installBorder(list, "List.border");

        PaletteLookAndFeel.installColorsAndFont(list, "List.background", "List.foreground", "List.font");

        PaletteLookAndFeel.installProperty(list, "opaque", Boolean.TRUE);
PaletteLookAndFeel plaf = PaletteLookAndFeel.getInstance();
        if (list.getCellRenderer() == null) {
            @SuppressWarnings("unchecked")
            ListCellRenderer<Object> cellRenderer = (ListCellRenderer<Object>) (plaf.get("List.cellRenderer"));
            list.setCellRenderer(cellRenderer);
        }

        Color sbg = list.getSelectionBackground();
        if (sbg == null || sbg instanceof UIResource) {
            list.setSelectionBackground(plaf.getColor("List.selectionBackground"));
        }

        Color sfg = list.getSelectionForeground();
        if (sfg == null || sfg instanceof UIResource) {
            list.setSelectionForeground(plaf.getColor("List.selectionForeground"));
        }
    }

}
