/*
 * @(#)PreferencesUtil.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.util.prefs;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Dimension2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

/**
 * {@code PreferencesUtil} provides utility methods for {@code
 * java.util.prefs.Preferences}, and can be used as a proxy when the system
 * preferences are not available due to security restrictions.
 *
 * @author Werner Randelshofer
 */
public class PreferencesUtil
        extends Preferences {

    @Nonnull
    private HashMap<String, Object> map = new HashMap<>();
    private boolean isUserNode;
    private static HashMap<Package, Preferences> systemNodes;
    private static HashMap<Package, Preferences> userNodes;

    public PreferencesUtil(boolean isUserNode) {
        this.isUserNode = isUserNode;
    }

    @Override
    public void put(String key, String value) {
        map.put(key, value);
    }

    @Nonnull
    @Override
    public String get(String key, String def) {
        return (String) (map.containsKey(key) ? map.get(key) : def);
    }

    @Override
    public void remove(String key) {
        map.remove(key);
    }

    @Override
    public void clear() throws BackingStoreException {
        map.clear();
    }

    @Override
    public void putInt(String key, int value) {
        map.put(key, value);
    }

    @Override
    public int getInt(String key, int def) {
        return (Integer) (map.containsKey(key) ? map.get(key) : def);
    }

    @Override
    public void putLong(String key, long value) {
        map.put(key, value);
    }

    @Override
    public long getLong(String key, long def) {
        return (Long) (map.containsKey(key) ? map.get(key) : def);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        map.put(key, value);
    }

    @Override
    public boolean getBoolean(String key, boolean def) {
        return (Boolean) (map.containsKey(key) ? map.get(key) : def);
    }

    @Override
    public void putFloat(String key, float value) {
        map.put(key, value);
    }

    @Override
    public float getFloat(String key, float def) {
        return (Float) (map.containsKey(key) ? map.get(key) : def);
    }

    @Override
    public void putDouble(String key, double value) {
        map.put(key, value);
    }

    @Override
    public double getDouble(String key, double def) {
        return (Double) (map.containsKey(key) ? map.get(key) : def);
    }

    @Override
    public void putByteArray(String key, byte[] value) {
        map.put(key, value);
    }

    @Nonnull
    @Override
    public byte[] getByteArray(String key, byte[] def) {
        return (byte[]) (map.containsKey(key) ? map.get(key) : def);
    }

    @Nonnull
    @Override
    public String[] keys() throws BackingStoreException {
        return map.keySet().toArray(new String[map.keySet().size()]);
    }

    @Nonnull
    @Override
    public String[] childrenNames() throws BackingStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Nullable
    @Override
    public Preferences parent() {
        return null;
    }

    @Nullable
    @Override
    public Preferences node(String pathName) {
        return null;
    }

    @Override
    public boolean nodeExists(String pathName) throws BackingStoreException {
        return false;
    }

    @Override
    public void removeNode() throws BackingStoreException {
        // empty
    }

    @Nonnull
    @Override
    public String name() {
        return "Dummy";
    }

    @Nonnull
    @Override
    public String absolutePath() {
        return "Dummy";
    }

    @Override
    public boolean isUserNode() {
        return isUserNode;
    }

    @Nonnull
    @Override
    public String toString() {
        return "Dummy";
    }

    @Override
    public void flush() throws BackingStoreException {
        clear();
    }

    @Override
    public void sync() throws BackingStoreException {
        //
    }

    @Override
    public void addPreferenceChangeListener(PreferenceChangeListener pcl) {
        //
    }

    @Override
    public void removePreferenceChangeListener(PreferenceChangeListener pcl) {
        //
    }

    @Override
    public void addNodeChangeListener(NodeChangeListener ncl) {
        //
    }

    @Override
    public void removeNodeChangeListener(NodeChangeListener ncl) {
        //
    }

    @Override
    public void exportNode(OutputStream os) throws IOException, BackingStoreException {
        //
    }

    @Override
    public void exportSubtree(OutputStream os) throws IOException, BackingStoreException {
        //
    }

    /**
     * Gets the system node for the package of the class if permitted, gets a
     * proxy otherwise.
     *
     * @param c The class
     * @return system node or a proxy.
     */
    public static Preferences systemNodeForPackage(Class<?> c) {
        if (systemNodes != null) {
            if (!systemNodes.containsKey(c.getPackage())) {
                systemNodes.put(c.getPackage(), new PreferencesUtil(false));
            }
            return systemNodes.get(c.getPackage());
        }

        try {
            return Preferences.systemNodeForPackage(c);
        } catch (Throwable t) {
            if (systemNodes == null) {
                systemNodes = new HashMap<>();
            }
            return systemNodeForPackage(c);
        }
    }

    /**
     * Gets the user node for the package of the class if permitted, gets a
     * proxy otherwise.
     *
     * @param c The class
     * @return user node or a proxy.
     */
    public static Preferences userNodeForPackage(Class<?> c) {
        if (userNodes != null) {
            if (!userNodes.containsKey(c.getPackage())) {
                userNodes.put(c.getPackage(), new PreferencesUtil(false));
            }
            return userNodes.get(c.getPackage());
        }

        try {
            return Preferences.userNodeForPackage(c);
        } catch (Throwable t) {
            if (userNodes == null) {
                userNodes = new HashMap<>();
            }
            return userNodeForPackage(c);
        }
    }

    /**
     * Creates a new instance.
     */
    private PreferencesUtil() {
    }

    /**
     * Installs a frame preferences handler. On first run, sets the window to
     * its preferred size at the top left corner of the screen. On subsequent
     * runs, sets the window the last size and location where the user had
     * placed it before.
     * <p>
     * If no preferences are stored yet for this window, a default size of 400 x
     * 300 pixels is used.
     *
     * @param prefs Preferences for storing/retrieving preferences values.
     * @param name  Base name of the preference.
     * @param stage The window for which to track preferences.
     */
    public static void installStagePrefsHandler(@Nonnull final Preferences prefs, final String name, @Nonnull Stage stage) {
        installStagePrefsHandler(prefs, name, stage, new Dimension2D(400, 300));
    }

    /**
     * Installs a frame preferences handler. On first run, sets the window to
     * its preferred size at the top left corner of the screen. On subsequent
     * runs, sets the window the last size and location where the user had
     * placed it before.
     *
     * @param prefs       Preferences for storing/retrieving preferences values.
     * @param name        Base name of the preference.
     * @param stage       The window for which to track preferences.
     * @param defaultSize This size is used when no prefences are stored yet for
     *                    this window.
     */
    public static void installStagePrefsHandler(final Preferences prefs, final String name, Stage stage, Dimension2D defaultSize) {

        double prefWidth;
        double prefHeight;

        prefWidth = prefs.getDouble(name + ".width", defaultSize.getWidth());
        prefHeight = prefs.getDouble(name + ".height", defaultSize.getHeight());

        stage.setWidth(prefWidth);
        stage.setHeight(prefHeight);

        stage.widthProperty().addListener((o, oldValue, newValue) -> {
            prefs.putDouble(name + ".width", newValue.doubleValue());
        });
        stage.heightProperty().addListener((o, oldValue, newValue) -> {
            prefs.putDouble(name + ".height", newValue.doubleValue());
        });
    }

    /**
     * Inits handlers which toggle the visibility of the given node in the split
     * pane and remembers user preferences.
     * <p>
     * The name attribute of the {@code visiblityProperty} is used to identify
     * preferences values.
     *
     * @param prefs              The preferences object to use.
     * @param node               The node which is added or removed to the split pane. The
     *                           node is also made visible/invisible.
     * @param visibilityProperty the boolean property which holds the visibility
     *                           state
     * @param splitPane          splitPane to which the node is added or removed
     * @param side               on which side of the split pane the element should be added
     */
    public static void installVisibilityPrefsHandlers(Preferences prefs, @Nonnull Node node, BooleanProperty visibilityProperty, SplitPane splitPane, Side side) {
        ChangeListener<? super Number> positionListener = (o, oldValue, newValue) -> {
            prefs.putDouble(visibilityProperty.getName() + ".dividerPosition", newValue.doubleValue());
        };

        ChangeListener<Boolean> visibilityListener = (o, oldValue, newValue) -> {
            node.setVisible(newValue);
            ObservableList<SplitPane.Divider> dividers = splitPane.getDividers();
            double[] oldPositions = new double[dividers.size()];
            for (int i = 0; i < oldPositions.length; i++) {
                oldPositions[i] = dividers.get(i).getPosition();
            }

            boolean first = side == Side.LEFT || side == Side.TOP;
            if (newValue) {
                if (first) {
                    splitPane.getItems().add(0, node);
                } else {
                    splitPane.getItems().add(node);
                }
                SplitPane.setResizableWithParent(node, false);
                DoubleProperty pp = dividers.get(first ? 0 : dividers.size() - 1).positionProperty();
                pp.set(prefs.getDouble(visibilityProperty.getName() + ".dividerPosition", first ? 0.2 : 0.8));
                pp.addListener(positionListener);
            } else {
                DoubleProperty pp = dividers.get(first ? 0 : dividers.size() - 1).positionProperty();
                pp.removeListener(positionListener);
                splitPane.getItems().remove(node);
            }
            prefs.putBoolean(visibilityProperty.getName(), newValue);

            // fix the positions because SplitPane does weird things with them
            if (first) {
                if (newValue) {
                    for (int i = 0; i < oldPositions.length; i++) {
                        dividers.get(i + 1).setPosition(oldPositions[i]);
                    }
                } else {
                    for (int i = 1; i < oldPositions.length; i++) {
                        dividers.get(i - 1).setPosition(oldPositions[i]);
                    }
                }
            }
        };
        splitPane.getItems().remove(node);
        visibilityProperty.set(false);
        visibilityProperty.addListener(visibilityListener);
        visibilityProperty.set(prefs.getBoolean(visibilityProperty.getName(), true));
    }

    public static void installBooleanPropertyHandler(final Preferences prefs, final String name, BooleanProperty property) {
        boolean prefValue = prefs.getBoolean(name, true);
        property.setValue(prefValue);
        property.addListener((o, oldValue, newValue) -> {
            prefs.putBoolean(name, newValue);
        });
    }
}
