/* @(#)AbstractApplicationModel.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.app;

import org.jhotdraw.beans.AbstractBean;
import org.jhotdraw.gui.JFileURIChooser;
import org.jhotdraw.gui.URIChooser;

import org.jhotdraw.annotation.Nullable;
import javax.swing.JFileChooser;
import javax.swing.JToolBar;
import java.util.List;
import java.util.function.Supplier;

/**
 * This abstract class can be extended to implement an {@link ApplicationModel}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractApplicationModel extends AbstractBean
        implements ApplicationModel {

    private static final long serialVersionUID = 1L;
    
    protected String name;
    protected String version;
    protected String copyright;
    protected Class<?> viewClass;
    protected Supplier<View> viewFactory;
    protected boolean allowMultipleViewsForURI = true;
    protected boolean openLastURIOnLaunch = false;
    public static final String NAME_PROPERTY = "name";
    public static final String VERSION_PROPERTY = "version";
    public static final String COPYRIGHT_PROPERTY = "copyright";
    public static final String VIEW_CLASS_NAME_PROPERTY = "viewFactory";
    public static final String VIEW_CLASS_PROPERTY = "viewClass";

    /** Creates a new instance. */
    public AbstractApplicationModel() {

    }

    public void setName(String newValue) {
        String oldValue = name;
        name = newValue;
        firePropertyChange(NAME_PROPERTY, oldValue, newValue);
    }

    @Override
    public String getName() {
        return name;
    }

    public void setVersion(String newValue) {
        String oldValue = version;
        version = newValue;
        firePropertyChange(VERSION_PROPERTY, oldValue, newValue);
    }

    public Supplier<View> getViewFactory() {
        return viewFactory;
    }

    public void setViewFactory(Supplier<View> viewFactory) {
        this.viewFactory = viewFactory;
    }

    @Override
    public String getVersion() {
        return version;
    }

    public void setCopyright(String newValue) {
        String oldValue = copyright;
        copyright = newValue;
        firePropertyChange(COPYRIGHT_PROPERTY, oldValue, newValue);
    }

    @Override
    public String getCopyright() {
        return copyright;
    }


    @Override
    public View createView() {
        try {
            return viewFactory.get();
        } catch (Exception e) {
            e.printStackTrace();
            InternalError error = new InternalError("unable to create view");
            error.initCause(e);
            throw error;
        }
    }

    /**
     * Creates toolbars for the application.
     */
    @Override
    public abstract List<JToolBar> createToolBars(Application a, @Nullable View p);

    /** This method is empty. */
    @Override
    public void initView(Application a, View p) {
    }

    /** This method is empty. */
    @Override
    public void destroyView(Application a, View p) {
    }

    /** This method is empty. */
    @Override
    public void initApplication(Application a) {
    }

    /** This method is empty. */
    @Override
    public void destroyApplication(Application a) {
    }

    @Override
    public URIChooser createOpenChooser(Application a, @Nullable View v) {
        URIChooser c = new JFileURIChooser();

        return c;
    }

    @Override
    public URIChooser createOpenDirectoryChooser(Application a, @Nullable View v) {
        JFileURIChooser c = new JFileURIChooser();
        c.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        return c;
    }

    @Override
    public URIChooser createSaveChooser(Application a, @Nullable View v) {
        JFileURIChooser c = new JFileURIChooser();
        return c;
    }

    /** Returns createOpenChooser. */
    @Override
    public URIChooser createImportChooser(Application a, @Nullable View v) {
        return createOpenChooser(a, v);
    }

    /** Returns createSaveChooser. */
    @Override
    public URIChooser createExportChooser(Application a, @Nullable View v) {
        return createSaveChooser(a, v);
    }

    /** 
     * {@inheritDoc}
     * The default value is true.
     */
    @Override
    public boolean isOpenLastURIOnLaunch() {
        return openLastURIOnLaunch;
    }

    /**
     * {@inheritDoc}
     * The default value is true.
     */
    @Override
    public boolean isAllowMultipleViewsPerURI() {
        return allowMultipleViewsForURI;
    }

    /** Whether the application may open multiple views for the same URI.
     * <p>
     * The default value is true.
     *
     * @param allowMultipleViewsForURI the value
     */
    public void setAllowMultipleViewsForURI(boolean allowMultipleViewsForURI) {
        this.allowMultipleViewsForURI = allowMultipleViewsForURI;
    }

    /** Whether the application should open the last opened URI on launch.
     * <p>
     * The default value is false.
     *
     * @param openLastURIOnLaunch
     */
    public void setOpenLastURIOnLaunch(boolean openLastURIOnLaunch) {
        this.openLastURIOnLaunch = openLastURIOnLaunch;
    }
}
