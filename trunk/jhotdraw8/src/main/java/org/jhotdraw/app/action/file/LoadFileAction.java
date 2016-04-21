/*
 * @(#)LoadFileAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.app.action.file;

import java.util.concurrent.CompletionStage;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractSaveUnsavedChangesAction;
import org.jhotdraw.util.Resources;

/**
 * Lets the user write unsaved changes of the active view, then presents
 * an {@code URIChooser} and loads the selected URI into the active view.
 * <p>
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class LoadFileAction extends AbstractSaveUnsavedChangesAction {
    private static final long serialVersionUID = 1L;

    public static final String ID = "file.load";

    /** Creates a new instance.
     * @param app the application
     * @param view the view */
    public LoadFileAction(Application app, View view) {
        super(app, view);
        Resources labels = Resources.getResources("org.jhotdraw.app.Labels");
        labels.configureAction(this, ID);
    }
/*
    @Override
    protected URIChooser getChooser(View view) {
        URIChooser chsr = (URIChooser) (view.getComponent()).getClientProperty("loadChooser");
        if (chsr == null) {
            chsr = getApplication().getModel().createOpenChooser(getApplication(), view);
            view.getComponent().putClientProperty("loadChooser", chsr);
        }
        return chsr;
    }

    @Override
    public void doIt(final View view) {
        URIChooser fileChooser = getChooser(view);
        Window wAncestor = SwingUtilities.getWindowAncestor(view.getComponent());
        final Component oldFocusOwner = (wAncestor == null) ? null : wAncestor.getFocusOwner();

        JSheet.showOpenSheet(fileChooser, view.getComponent(), new SheetListener() {

            @Override
            public void optionSelected(final SheetEvent evt) {
                if (evt.getOption() == JFileChooser.APPROVE_OPTION) {
                    final URI uri;
                    if ((evt.getChooser() instanceof JFileURIChooser) && evt.getFileChooser().getFileFilter() instanceof ExtensionFileFilter) {
                        uri = ((ExtensionFileFilter) evt.getFileChooser().getFileFilter()).makeAcceptable(evt.getFileChooser().getSelectedFile()).toURI();
                    } else {
                        uri = evt.getChooser().getSelectedURI();
                    }

                    // Prevent same URI from being opened more than once
                    if (!getApplication().getModel().isAllowMultipleViewsPerURI()) {
                        for (View v : getApplication().getViews()) {
                            if (v != view && v.getURI() != null && v.getURI().equals(uri)) {
                                v.getComponent().requestFocus();
                                return;
                            }
                        }
                    }

                    loadViewFromURI(view, uri, evt.getChooser());
                } else {
                    view.setEnabled(true);
                    if (oldFocusOwner != null) {
                        oldFocusOwner.requestFocus();
                    }
                }
            }
        });
    }

    public void loadViewFromURI(final View view, final URI uri, final URIChooser chooser) {
        view.setEnabled(false);

        // Open the file
        view.execute(new BackgroundTask() {

            @Override
            protected void construct() throws IOException {
                view.read(uri, chooser);
            }

            @Override
            protected void done() {
                view.setURI(uri);
                view.setEnabled(true);
                getApplication().addRecentURI(uri);
            }

            @Override
            protected void failed(Throwable value) {
                value.printStackTrace();
                
                Resources labels = Resources.getResources("org.jhotdraw.app.Labels");
                JSheet.showMessageSheet(view.getComponent(),
                        "<html>" + UIManager.getString("OptionPane.css")
                        + "<b>" + labels.getFormatted("file.read.couldntLoad.message", URIUtil.getName(uri)) + "</b><p>"
                        + ((value == null) ? "" : value),
                        JOptionPane.ERROR_MESSAGE, new SheetListener() {

                    @Override
                    public void optionSelected(SheetEvent evt) {
                        view.clear();
                        view.setEnabled(true);
                    }
                });
            }
        });
    }*/

    @Override public CompletionStage<Void> doIt(final View view) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
