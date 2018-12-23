/* @(#)PaletteColorChooserUI.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.gui.plaf.palette;

import org.jhotdraw.gui.plaf.palette.colorchooser.PaletteColorChooserMainPanel;
import org.jhotdraw.gui.plaf.palette.colorchooser.PaletteColorChooserPreviewPanel;

import org.jhotdraw.annotation.Nullable;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ColorChooserUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessControlException;
import java.util.ArrayList;

/**
 * PaletteColorChooserUI.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class PaletteColorChooserUI extends ColorChooserUI {
    @Nullable protected PaletteColorChooserMainPanel mainPanel;
    @Nullable protected JColorChooser chooser;
    protected ChangeListener previewListener;
    protected PropertyChangeListener propertyChangeListener;
    @Nullable protected AbstractColorChooserPanel[] defaultChoosers;
    @Nullable protected JComponent previewPanel;
    private static TransferHandler defaultTransferHandler = new ColorTransferHandler();
    private MouseListener previewMouseListener;
    
    public static ComponentUI createUI(JComponent c) {
        return new PaletteColorChooserUI();
    }
    
    @Override
    public void installUI( JComponent c ) {
        chooser = (JColorChooser)c;
                AbstractColorChooserPanel[] oldPanels = chooser.getChooserPanels();
        
        installDefaults();
        
        chooser.setLayout( new BorderLayout() );
        mainPanel = new PaletteColorChooserMainPanel();
        chooser.add(mainPanel);
        defaultChoosers = createDefaultChoosers();
        chooser.setChooserPanels(defaultChoosers);
        
        installPreviewPanel();
                AbstractColorChooserPanel[] newPanels = chooser.getChooserPanels();
                updateColorChooserPanels(oldPanels, newPanels);
        
        // Note: install listeners only after we have fully installed
        //       all chooser panels. If we do it earlier, we send property
        //       events too early.
        installListeners();

        chooser.applyComponentOrientation(c.getComponentOrientation());
    }
    
    protected AbstractColorChooserPanel[] createDefaultChoosers() {
        String[] defaultChooserNames = (String[]) PaletteLookAndFeel.getInstance().get("ColorChooser.defaultChoosers");
        ArrayList<AbstractColorChooserPanel> panels = new ArrayList<AbstractColorChooserPanel>(defaultChooserNames.length);
        for (int i=0; i < defaultChooserNames.length; i++) {
            try {
                
                panels.add((AbstractColorChooserPanel) Class.forName(defaultChooserNames[i]).getDeclaredConstructor().newInstance());
                
            } catch (AccessControlException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                // suppress
                System.err.println("PaletteColorChooserUI warning: unable to instantiate "+defaultChooserNames[i]);
                e.printStackTrace();
            }
        }
        //AbstractColorChooserPanel[] panels = new AbstractColorChooserPanel[defaultChoosers.length];
        return panels.toArray(new AbstractColorChooserPanel[panels.size()]);
    }
    
    
    @Override
    public void uninstallUI( JComponent c ) {
        chooser.remove(mainPanel);
        
        uninstallListeners();
        uninstallDefaultChoosers();
        uninstallDefaults();
        
        mainPanel.setPreviewPanel(null);
        if (previewPanel instanceof UIResource) {
            chooser.setPreviewPanel(null);
        }
        
        mainPanel = null;
        previewPanel = null;
        defaultChoosers = null;
        chooser = null;
    }
    protected void installDefaults() {
        PaletteLookAndFeel.installColorsAndFont(chooser, "ColorChooser.background",
                "ColorChooser.foreground",
                "ColorChooser.font");
        TransferHandler th = chooser.getTransferHandler();
        if (th == null || th instanceof UIResource) {
            chooser.setTransferHandler(defaultTransferHandler);
        }
    }
    
    protected void uninstallDefaults() {
        if (chooser.getTransferHandler() instanceof UIResource) {
            chooser.setTransferHandler(null);
        }
    }
    
    
    protected void installListeners() {
        propertyChangeListener = createPropertyChangeListener();
        chooser.addPropertyChangeListener( propertyChangeListener );
        
        previewListener = new PreviewListener();
        chooser.getSelectionModel().addChangeListener(previewListener);

        previewMouseListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (chooser.getDragEnabled()) {
                    TransferHandler th = chooser.getTransferHandler();
                    th.exportAsDrag(chooser, e, TransferHandler.COPY);
                }
            }
        };
    }
    
    protected void uninstallListeners() {
        chooser.removePropertyChangeListener( propertyChangeListener );
        chooser.getSelectionModel().removeChangeListener(previewListener);

        previewPanel.removeMouseListener(previewMouseListener);
    }
    
    protected PropertyChangeListener createPropertyChangeListener() {
        return new PropertyHandler();
    }
    
    protected void installPreviewPanel() {
        if (previewPanel != null) {
            previewPanel.removeMouseListener(previewMouseListener);
        }
        if (previewPanel != null) {
            mainPanel.setPreviewPanel(null);
        }
        
        previewPanel = chooser.getPreviewPanel();
        if ((previewPanel != null) && (mainPanel != null) //
                && (previewPanel.getSize().getHeight()+previewPanel.getSize().getWidth() == 0)) {
            mainPanel.setPreviewPanel(null);
            return;
        }
        if (previewPanel == null || previewPanel instanceof UIResource) {
            //previewPanel = ColorChooserComponentFactory.getPreviewPanel(); // get from table?
            previewPanel = new PaletteColorChooserPreviewPanel();
            chooser.setPreviewPanel(previewPanel);
        }
        previewPanel.setForeground(chooser.getColor());
        mainPanel.setPreviewPanel(previewPanel);
        previewPanel.addMouseListener(previewMouseListener);
    }
    
    class PreviewListener implements ChangeListener {
        @Override
        public void stateChanged( ChangeEvent e ) {
            ColorSelectionModel model = (ColorSelectionModel)e.getSource();
            if (previewPanel != null) {
                previewPanel.setForeground(model.getSelectedColor());
                previewPanel.repaint();
            }
        }
    }
    protected void uninstallDefaultChoosers() {
        for( int i = 0 ; i < defaultChoosers.length; i++) {
            chooser.removeChooserPanel( defaultChoosers[i] );
        }
    }
    private void updateColorChooserPanels(
            AbstractColorChooserPanel[] oldPanels,  
            AbstractColorChooserPanel[] newPanels) {
        for (int i = 0; i < oldPanels.length; i++) {  // remove old panels
            Container wrapper = oldPanels[i].getParent();
            if (wrapper != null) {
                Container parent = wrapper.getParent();
                if (parent != null)
                    parent.remove(wrapper);  // remove from hierarchy
                oldPanels[i].uninstallChooserPanel(chooser); // uninstall
            }
        }
        
        mainPanel.removeAllColorChooserPanels();
        for (int i = 0; i < newPanels.length; i++) {
            if (newPanels[i] != null) {
                mainPanel.addColorChooserPanel(newPanels[i]);
            }
        }
        
        for (int i = 0; i < newPanels.length; i++) {
            if (newPanels[i] != null) {
                newPanels[i].installChooserPanel(chooser);
            }
        }
    }
    
    public class PropertyHandler implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            String name = e.getPropertyName();
            if (name.equals( JColorChooser.CHOOSER_PANELS_PROPERTY ) ) {
                AbstractColorChooserPanel[] oldPanels = (AbstractColorChooserPanel[]) e.getOldValue();
                AbstractColorChooserPanel[] newPanels = (AbstractColorChooserPanel[]) e.getNewValue();

                for (int i = 0; i < oldPanels.length; i++) {  // remove old panels
                    if (oldPanels[i] != null) {
                        Container wrapper = oldPanels[i].getParent();
                        if (wrapper != null) {
                            Container parent = wrapper.getParent();
                            if (parent != null)
                                parent.remove(wrapper);  // remove from hierarchy
                            oldPanels[i].uninstallChooserPanel(chooser); // uninstall
                        }
                    }
                }

                mainPanel.removeAllColorChooserPanels();
                for (int i = 0; i < newPanels.length; i++) {
                    if (newPanels[i] != null) {
                        mainPanel.addColorChooserPanel(newPanels[i]);
                    }
                }

                chooser.applyComponentOrientation(chooser.getComponentOrientation());
                for (int i = 0; i < newPanels.length; i++) {
                    if (newPanels[i] != null) {
                        newPanels[i].installChooserPanel(chooser);
                    }
                }
            }
            if (name.equals( JColorChooser.PREVIEW_PANEL_PROPERTY ) ) {
                if (e.getNewValue() != previewPanel) {
                    installPreviewPanel();
                }
            }
            if ("componentOrientation".equals(name)) {
                ComponentOrientation o = (ComponentOrientation)e.getNewValue();
                JColorChooser cc = (JColorChooser)e.getSource();
                if (o != (ComponentOrientation)e.getOldValue()) {
                    cc.applyComponentOrientation(o);
                    cc.updateUI();
                }
            }
        }
    }
    static class ColorTransferHandler extends TransferHandler implements UIResource {
    private static final long serialVersionUID = 1L;

        ColorTransferHandler() {
            super("color");
        }
    }
}
