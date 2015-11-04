/*
 * @(#)ExportFileAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.app.action.file;

import java.net.URISyntaxException;
import java.net.URI;
import java.util.prefs.Preferences;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import org.jhotdraw.app.*;
import org.jhotdraw.app.action.AbstractViewAction;
import org.jhotdraw.gui.URIChooser;
import org.jhotdraw.net.URIUtil;
import org.jhotdraw.util.Resources;

/**
 * Presents a file chooser to the user and then exports the contents of the
 * active view to the chosen file.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id: ExportFileAction.java 788 2014-03-22 07:56:28Z rawcoder $
 */
public class ExportFileAction extends AbstractViewAction {
    private static final long serialVersionUID = 1L;

    public static final String ID = "file.export";
    private Node oldFocusOwner;
    private boolean proposeFileName;

    /** Creates a new instance.
     * @param app the application
     * @param view the view */
    public ExportFileAction(Application app, View view) {
        this(app, view, false);
    }

    public ExportFileAction(Application app, View view, boolean proposeFileName) {
        super(app, view);
        Resources labels = Resources.getResources("org.jhotdraw.app.Labels");
        labels.configureAction(this, ID);
        this.proposeFileName = proposeFileName;
    }
/*
    /** Whether the export file action shall propose a file name or shall
     * leave the filename empty.
     * @return True if filename is proposed.
     * /
    public boolean isProposeFileName() {
        return proposeFileName;
    }

    /** Whether the export file action shall propose a file name or shall
     * leave the filename empty.
     * 
     * @param newValue True if filename shall be proposed.
     * /
    public void setProposeFileName(boolean newValue) {
        this.proposeFileName = newValue;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        final View view = getActiveView();
        if (view.isEnabled()) {
            Resources labels = Resources.getResources("org.jhotdraw.app.Labels");

            oldFocusOwner = SwingUtilities.getWindowAncestor(view.getComponent()).getFocusOwner();
            view.setEnabled(false);
            try {
                URIChooser fileChooser = getApplication().getExportChooser(view);
                if (proposeFileName) {
                    // => try to propose file name without extension
                    URI proposedURI = view.getURI();
                    if (proposedURI != null) {
                        try {
                            URI selectedURI = fileChooser.getSelectedURI();

                            File selectedFolder;
                            if (selectedURI == null) {
                                Preferences prefs = Preferences.userNodeForPackage(getApplication().getModel().getClass());
                                try {
                                    selectedURI = new URI(//
                                            prefs.get("recentExportFile", new File(proposedURI).getParentFile().toURI().toString())//
                                            );
                                    selectedFolder = new File(selectedURI).getParentFile();
                                } catch (URISyntaxException ex) {
                                    // selectedURI is null
                                    selectedFolder = new File(proposedURI).getParentFile();
                                }
                            } else {
                                selectedFolder = new File(selectedURI).getParentFile();
                            }

                            File file = new File(selectedFolder,new File(proposedURI).getName());
                            
                            String name = file.getName();
                            int p = name.lastIndexOf('.');
                            if (p != -1) {
                                name = name.substring(0, p);
                                file = new File(selectedFolder, name);
                                proposedURI = file.toURI();
                            }
                        } catch (IllegalArgumentException e) {
                        }
                    }
                    fileChooser.setSelectedURI(proposedURI);
                }
                JSheet.showSheet(fileChooser, view.getComponent(), labels.getString("filechooser.export"), new SheetListener() {

                    @Override
                    public void optionSelected(final SheetEvent evt) {
                        if (evt.getOption() == JFileChooser.APPROVE_OPTION) {
                            URI uri = evt.getChooser().getSelectedURI();
                            if ((evt.getChooser() instanceof JFileURIChooser) && evt.getFileChooser().getFileFilter() instanceof ExtensionFileFilter) {
                                uri = ((ExtensionFileFilter) evt.getFileChooser().getFileFilter()).makeAcceptable(evt.getFileChooser().getSelectedFile()).toURI();
                            } else {
                                uri = evt.getChooser().getSelectedURI();
                            }
                            Preferences prefs = Preferences.userNodeForPackage(getApplication().getModel().getClass());
                            prefs.put("recentExportFile", uri.toString());


                            if (evt.getChooser() instanceof JFileURIChooser) {
                                exportView(view, uri, evt.getChooser());
                            } else {
                                exportView(view, uri, null);
                            }
                        } else {
                            view.setEnabled(true);
                            if (oldFocusOwner != null) {
                                oldFocusOwner.requestFocus();
                            }
                        }
                    }
                });
            } catch (Error err) {
                view.setEnabled(true);
                throw err;
            } catch (Throwable err) {
                view.setEnabled(true);
                err.printStackTrace();
            }
        }
    }

    protected void exportView(final View view, final URI uri,
            @Nullable final URIChooser chooser) {
        view.execute(new BackgroundTask() {

            @Override
            protected void construct() throws IOException {
                view.write(uri, chooser);
            }

            @Override
            protected void failed(Throwable value) {
                System.out.flush();
                value.printStackTrace();
                // FIXME localize this error messsage
                JSheet.showMessageSheet(view.getComponent(),
                        "<html>" + UIManager.getString("OptionPane.css")
                        + "<b>Couldn't export to the file \"" + URIUtil.getName(uri) + "\".<p>"
                        + "Reason: " + value,
                        JOptionPane.ERROR_MESSAGE);
            }

            @Override
            protected void finished() {
                view.setEnabled(true);
                SwingUtilities.getWindowAncestor(view.getComponent()).toFront();
                if (oldFocusOwner != null) {
                    oldFocusOwner.requestFocus();
                }
            }
        });
    }*/

    @Override
    protected void onActionPerformed(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
