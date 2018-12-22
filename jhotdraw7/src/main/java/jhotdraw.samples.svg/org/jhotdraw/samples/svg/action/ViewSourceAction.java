/* @(#)ViewSourceAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.samples.svg.action;

import org.jhotdraw.app.Application;
import org.jhotdraw.app.Disposable;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractViewAction;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.samples.svg.SVGLabels;
import org.jhotdraw.samples.svg.SVGView;
import org.jhotdraw.samples.svg.io.SVGOutputFormat;
import org.jhotdraw.util.ResourceBundleUtil;
import org.jhotdraw.util.prefs.PreferencesUtil;

import org.jhotdraw.annotation.Nullable;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.prefs.Preferences;

/**
 * ViewSourceAction.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ViewSourceAction extends AbstractViewAction {
    private static final long serialVersionUID = 1L;

    public static final String ID = "view.viewSource";
    /**
     * We store the dialog as a client property in the view.
     */
    private static final String DIALOG_CLIENT_PROPERTY = "view.viewSource.dialog";

    /** Creates a new instance. */
    public ViewSourceAction(Application app, @Nullable View view) {
        super(app, view);
        ResourceBundleUtil labels = SVGLabels.getLabels();
        labels.configureAction(this, ID);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ResourceBundleUtil labels = SVGLabels.getLabels();
        final SVGView v = (SVGView) getActiveView();
        Drawing drawing = v.getDrawing();
        final JDialog dialog;
        if (v.getClientProperty(DIALOG_CLIENT_PROPERTY) == null) {
            dialog = new JDialog(SwingUtilities.getWindowAncestor(v.getComponent()));
            v.putClientProperty(DIALOG_CLIENT_PROPERTY, dialog);
            dialog.setTitle(labels.getFormatted("view.viewSource.titleText", v.getTitle()));
            dialog.setResizable(true);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            final JTextArea ta = new JTextArea();
            ta.setWrapStyleWord(true);
            ta.setLineWrap(true);
            JScrollPane sp = new JScrollPane(ta);
            //sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            dialog.getContentPane().add(sp);
            dialog.setSize(400, 400);
            dialog.setLocationByPlatform(true);
            updateSource(drawing, ta);

            final UndoableEditListener undoableEditHandler = new UndoableEditListener() {

                @Override
                public void undoableEditHappened(UndoableEditEvent e) {
                    updateSource(v.getDrawing(), ta);
                }
            };
            v.getDrawing().addUndoableEditListener(undoableEditHandler);

            final PropertyChangeListener propertyChangeHandler = new PropertyChangeListener() {

                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName() == SVGView.DRAWING_PROPERTY) {
                        Drawing oldDrawing = (Drawing) evt.getOldValue();
                        if (oldDrawing != null) {
                            oldDrawing.removeUndoableEditListener(undoableEditHandler);
                        }
                        Drawing newDrawing = (Drawing) evt.getNewValue();
                        if (newDrawing != null) {
                            newDrawing.addUndoableEditListener(undoableEditHandler);
                        }
                        if (newDrawing!=null) {
                            updateSource(newDrawing, ta);
                        }
                    } else if (evt.getPropertyName() == View.TITLE_PROPERTY) {
                        ResourceBundleUtil labels = SVGLabels.getLabels();
                        dialog.setTitle(labels.getFormatted("view.viewSource.titleText", v.getTitle()));
                    }
                }
            };
            v.addPropertyChangeListener(propertyChangeHandler);

            final Disposable disposable = new Disposable() {

                @Override
                public void dispose() {
                    if (v.getDrawing()!=null) {
                    v.getDrawing().removeUndoableEditListener(undoableEditHandler);
                    }
                    v.removePropertyChangeListener(propertyChangeHandler);
                    getApplication().removeWindow(dialog);
                    v.putClientProperty(DIALOG_CLIENT_PROPERTY, null);
                    v.removeDisposable(this);
                }
            };

            dialog.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosed(WindowEvent evt) {
                    disposable.dispose();
                }
            });

            v.addDisposable(disposable);
        } else {
            dialog = (JDialog) v.getClientProperty(DIALOG_CLIENT_PROPERTY);
            JTextArea ta = (JTextArea) ((JScrollPane) dialog.getContentPane().getComponent(0)).getViewport().getView();
            updateSource(drawing, ta);
        }

        Preferences prefs = PreferencesUtil.userNodeForPackage(getClass());
        PreferencesUtil.installFramePrefsHandler(prefs, "viewSource", dialog);

        getApplication().addWindow(dialog, v);
        dialog.setVisible(true);
    }

    private void updateSource(Drawing drawing, JTextArea textArea) {
        SVGOutputFormat format = new SVGOutputFormat();
        format.setPrettyPrint(true);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        try {
            format.write(buf, drawing);
            String source = buf.toString("UTF-8");
            textArea.setText(source);

        } catch (IOException ex) {
            textArea.setText(ex.toString());
        }
    }
}
