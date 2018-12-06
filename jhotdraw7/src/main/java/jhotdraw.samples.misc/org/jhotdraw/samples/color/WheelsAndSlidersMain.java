/* @(#)WheelsAndSlidersMain.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.samples.color;

import org.jhotdraw.color.CIELABColorSpace;
import org.jhotdraw.color.CIELCHabColorSpace;
import org.jhotdraw.color.CMYKNominalColorSpace;
import org.jhotdraw.color.ColorSliderModel;
import org.jhotdraw.color.ColorSliderUI;
import org.jhotdraw.color.ColorUtil;
import org.jhotdraw.color.DefaultColorSliderModel;
import org.jhotdraw.color.HSBColorSpace;
import org.jhotdraw.color.HSLColorSpace;
import org.jhotdraw.color.HSLPhysiologicColorSpace;
import org.jhotdraw.color.HSVColorSpace;
import org.jhotdraw.color.HSVPhysiologicColorSpace;
import org.jhotdraw.color.ICCProfileReader;
import org.jhotdraw.color.JColorWheel;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * A demo of color wheels and color sliders using all kinds of color systems.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class WheelsAndSlidersMain extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;
    private FileDialog fileDialog;
    private Color color;
    private JLabel colorLabel;
    private ArrayList<ColorSliderModel> models;
    private ArrayList<JColorWheel> views;
    private ArrayList<ColorSliderUI> sliderViews;

    private void readXYZ(ICC_Profile profile, int tag, StringBuilder buf) {
        byte[] data = profile.getData(tag);

        try {
            double[] xyz = new ICCProfileReader(data).readXYZType();
            buf.append("X:");
            buf.append(Float.toString((float) xyz[0]));
            buf.append(" Y:");
            buf.append(Float.toString((float) xyz[1]));
            buf.append(" Z:");
            buf.append(Float.toString((float) xyz[2]));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void loadScreenColorProfile(File file) {
        new SwingWorker<ICC_Profile, Object>() {
            @Override
            public ICC_Profile doInBackground() {
                try {
                    return ICC_Profile.getInstance(file.getPath());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    ICC_Profile profile = get();
                    if (profile != null) {
                        setScreenColorSpace(new ICC_ColorSpace(profile));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.execute();
    }

    private void setScreenColorSpace(ColorSpace cs) {
        if (cs == null){
        screenColorProfileLabel.setText("sRGB");
    }else{
        screenColorProfileLabel.setText(ColorUtil.getName(cs))                ;
        }
        for (JColorWheel wheel:views) {
            wheel.setScreenColorSpace(cs);
        }
              for (ColorSliderUI ui:sliderViews) {
            ui.setScreenColorSpace(cs);
        }
    }

    private class Handler implements ChangeListener {

        private int adjusting;

        @Override
        public void stateChanged(ChangeEvent e) {
            if (adjusting++ == 0) {
                ColorSliderModel m = (ColorSliderModel) e.getSource();
                color = m.getColor();
                previewLabel.setBackground(color);
                for (ColorSliderModel c : models) {
                    if (c != m) {
                        if (c.getColorSpace().equals(m.getColorSpace())) {
                            // If the color system is the same, directly set the components (=lossless)
                            for (int i = 0; i < m.getComponentCount(); i++) {
                                c.setComponent(i, m.getComponent(i));
                            }
                        } else {
                            // If the color system is different, set the RGB color (=lossy)
                            c.setColor(color);
                        }
                    }
                }
            }
            adjusting--;
        }
    }
    private Handler handler;

    /**
     * Creates new form.
     */
    public WheelsAndSlidersMain() {
        initComponents();

        models = new ArrayList<ColorSliderModel>();
        views = new ArrayList<>();
        sliderViews = new ArrayList<>();
        handler = new Handler();

        previewLabel.setOpaque(true);

        // RGB panels
        chooserPanel.add(createSliderChooser(ColorSpace.getInstance(ColorSpace.CS_sRGB)));
        chooserPanel.add(createColorWheelChooser(ICC_ColorSpace.getInstance(ICC_ColorSpace.CS_sRGB), 0, 1, 2, JColorWheel.Type.SQUARE));
        //chooserPanel.add(createColorWheelChooser(ICC_ColorSpace.getInstance(ICC_ColorSpace.CS_LINEAR_RGB), 0, 1, 2, JColorWheel.Type.SQUARE));

        // CMYK
        //chooserPanel.add(createSliderChooser(CMYKGenericColorSpace.getInstance()));
        chooserPanel.add(createSliderChooser(CMYKNominalColorSpace.getInstance()));

        // Empty panel
        chooserPanel.add(new JPanel());

        // HSB, HSV, HSL, ... variants
        chooserPanel.add(createColorWheelChooser(HSBColorSpace.getInstance()));
        chooserPanel.add(createColorWheelChooser(HSVColorSpace.getInstance()));
        chooserPanel.add(createColorWheelChooser(HSLColorSpace.getInstance()));
        chooserPanel.add(createColorWheelChooser(HSLColorSpace.getInstance(), 0, 2, 1));        
        chooserPanel.add(createColorWheelChooser(HSVPhysiologicColorSpace.getInstance()));
        chooserPanel.add(createColorWheelChooser(HSLPhysiologicColorSpace.getInstance()));
        chooserPanel.add(createColorWheelChooser(HSLPhysiologicColorSpace.getInstance(), 0, 2, 1));
        chooserPanel.add(new JPanel());

        // CIELAB
        ColorSpace cs;
        cs = new CIELABColorSpace();
        // ((CIELABColorSpace) cs).setOutsideGamutHandling(CIELABColorSpace.OutsideGamutHandling.LEAVE_OUTSIDE);
        chooserPanel.add(createColorWheelChooser(cs, 1, 2, 0, JColorWheel.Type.SQUARE));
        cs = new CIELCHabColorSpace();
        // ((CIELCHabColorSpace) cs).setOutsideGamutHandling(CIELABColorSpace.OutsideGamutHandling.LEAVE_OUTSIDE);
        chooserPanel.add(createColorWheelChooser(cs, 2, 1, 0, JColorWheel.Type.POLAR));

        // CIEXYZ
        chooserPanel.add(createColorWheelChooser(ICC_ColorSpace.getInstance(ICC_ColorSpace.CS_CIEXYZ), 2,0, 1, JColorWheel.Type.SQUARE));
        chooserPanel.add(createColorWheelChooser(ICC_ColorSpace.getInstance(ICC_ColorSpace.CS_PYCC), 1, 2, 0, JColorWheel.Type.SQUARE));

    }

    private JPanel createColorWheelChooser(ColorSpace sys) {
        return createColorWheelChooser(sys, 0, 1, 2);
    }

    private JPanel createColorWheelChooser(ColorSpace sys, int angularIndex, int radialIndex, int verticalIndex) {
        return createColorWheelChooser(sys, angularIndex, radialIndex, verticalIndex, JColorWheel.Type.POLAR);
    }

    private JPanel createColorWheelChooser(ColorSpace sys, int angularIndex, int radialIndex, int verticalIndex, JColorWheel.Type type) {
        return createColorWheelChooser(sys, angularIndex, radialIndex, verticalIndex, type, false, false);
    }

    private JPanel createColorWheelChooser(ColorSpace sys, int angularIndex, int radialIndex, int verticalIndex, JColorWheel.Type type, boolean flipX, boolean flipY) {
        JPanel p = new JPanel(new BorderLayout());
        final DefaultColorSliderModel m = new DefaultColorSliderModel(sys);
        models.add(m);
        m.addChangeListener(handler);
        JColorWheel w = new JColorWheel();
        views.add(w);
        w.setType(type);
        w.setAngularComponentIndex(angularIndex);
        w.setRadialComponentIndex(radialIndex);
        w.setVerticalComponentIndex(verticalIndex);
        w.setFlipX(flipX);
        w.setFlipY(flipY);
        w.setModel(m);
        JSlider s = new JSlider(JSlider.VERTICAL);
        m.configureSlider(verticalIndex, s);
        sliderViews.add((ColorSliderUI) s.getUI());
        p.add(new JLabel("<html>" + ColorUtil.getName(sys) + "<br>α:" + angularIndex + " r:" + radialIndex + " v:" + verticalIndex), BorderLayout.NORTH);
        p.add(w, BorderLayout.CENTER);
        p.add(s, BorderLayout.EAST);

        JPanel pp = new JPanel();
        p.add(pp, BorderLayout.SOUTH);
        for (int i = 0; i < m.getComponentCount(); i++) {
            final int comp = i;
            final JTextField tf = new JTextField();
            tf.setEditable(false);
            tf.setColumns(4);
            ChangeListener cl = new ChangeListener() {
                NumberFormat df = NumberFormat.getNumberInstance();

                @Override
                public void stateChanged(ChangeEvent e) {
                    df.setMaximumFractionDigits(3);
                    tf.setText(df.format(m.getComponent(comp)));
                }
            };
            cl.stateChanged(null);
            m.addChangeListener(cl);
            pp.add(tf);
        }
        return p;
    }

    private JPanel createSliderChooser(ColorSpace sys) {
        return createSliderChooser(sys, false);
    }

    private JPanel createSliderChooser(ColorSpace sys, boolean vertical) {
        JPanel p = new JPanel(new GridBagLayout());
        final DefaultColorSliderModel m = new DefaultColorSliderModel(sys);

        models.add(m);
        GridBagConstraints gbc = new GridBagConstraints();
        if (!vertical) {
            gbc.gridx = 0;
            gbc.gridy = 0;
            p.add(new JLabel(
                    "<html>" + ColorUtil.getName(sys)), gbc);
        }
        m.addChangeListener(handler);

        for (int i = 0; i < m.getComponentCount(); i++) {
            final int comp = i;
            JSlider s = new JSlider(JSlider.HORIZONTAL);
            s.setMajorTickSpacing(50);
            s.setPaintTicks(true);
            s.setOrientation(vertical ? JSlider.VERTICAL : JSlider.HORIZONTAL);
            m.configureSlider(comp, s);
            sliderViews.add((ColorSliderUI) s.getUI());
            if (vertical) {
                gbc.gridx = i;
                gbc.gridy = 0;
            } else {
                gbc.gridy = i + 1;
                gbc.gridx = 0;
            }
            p.add(s, gbc);
            final JTextField tf = new JTextField();
            tf.setEditable(false);
            tf.setColumns(4);
            ChangeListener cl = new ChangeListener() {
                NumberFormat df = NumberFormat.getNumberInstance();

                @Override
                public void stateChanged(ChangeEvent e) {
                    df.setMaximumFractionDigits(3);
                    tf.setText(df.format(m.getComponent(comp)));
                }
            };
            cl.stateChanged(null);
            m.addChangeListener(cl);
            if (vertical) {
                gbc.gridx = i;
                gbc.gridy = 1;
            } else {
                gbc.gridy = i + 1;
                gbc.gridx = 1;
            }
            p.add(tf, gbc);
        }
        return p;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame f = new JFrame("Color Wheels, Squares and Sliders");
                final WheelsAndSlidersMain colorWheelsMain = new WheelsAndSlidersMain();
                f.add(colorWheelsMain);
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.pack();
                f.setVisible(true);

                colorWheelsMain.onResetScreenColorProfile(null);
            }
        });

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        chooserPanel = new javax.swing.JPanel();
        previewLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        screenColorProfileLabel = new javax.swing.JLabel();
        chooseColorProfileButton = new javax.swing.JButton();
        resetColorProfileButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        chooserPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        chooserPanel.setLayout(new java.awt.GridLayout(0, 4, 10, 10));
        add(chooserPanel, java.awt.BorderLayout.CENTER);

        previewLabel.setText("Selected Color");
        add(previewLabel, java.awt.BorderLayout.SOUTH);

        screenColorProfileLabel.setText("Screen Color Profile:");
        jPanel1.add(screenColorProfileLabel);

        chooseColorProfileButton.setText("Choose...");
        chooseColorProfileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onChooseScreenColorProfile(evt);
            }
        });
        jPanel1.add(chooseColorProfileButton);

        resetColorProfileButton.setText("Reset");
        resetColorProfileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onResetScreenColorProfile(evt);
            }
        });
        jPanel1.add(resetColorProfileButton);

        add(jPanel1, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents

    private void onChooseScreenColorProfile(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onChooseScreenColorProfile
        if (fileDialog == null) {
            fileDialog = new FileDialog((Frame) SwingUtilities.getWindowAncestor(this), "Choose Color Profile", FileDialog.LOAD);
            fileDialog.setFilenameFilter(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name != null && name.endsWith(".icc");
                }
            });
        }
        fileDialog.setVisible(true);
        File file = fileDialog.getFiles()[0];
        if (file != null) {
            loadScreenColorProfile(file);
        }
    }//GEN-LAST:event_onChooseScreenColorProfile

    private void onResetScreenColorProfile(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onResetScreenColorProfile
            setScreenColorSpace(null);

    }//GEN-LAST:event_onResetScreenColorProfile

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton chooseColorProfileButton;
    private javax.swing.JPanel chooserPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel previewLabel;
    private javax.swing.JButton resetColorProfileButton;
    private javax.swing.JLabel screenColorProfileLabel;
    // End of variables declaration//GEN-END:variables
}
