/*
 * @(#)PreferencesUtil.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.util.prefs;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Dimension2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.jhotdraw8.annotation.NonNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.prefs.Preferences;

/**
 * {@code PreferencesUtil} provides utility methods for {@code
 * java.util.prefs.Preferences}, and can be used as a proxy when the system
 * preferences are not available due to security restrictions.
 *
 * @author Werner Randelshofer
 */
public class PreferencesUtil {
    private final static ConcurrentHashMap<Package, Preferences> systemNodes = new ConcurrentHashMap<>();
    private final static ConcurrentHashMap<Package, Preferences> userNodes = new ConcurrentHashMap<>();

    /**
     * Gets the system node for the package of the class if permitted, gets a
     * stub otherwise.
     *
     * @param c The class
     * @return system node or a proxy.
     */
    public static Preferences systemNodeForPackage(@NonNull Class<?> c) {
        return systemNodes.computeIfAbsent(c.getPackage(), pckg -> {
            try {
                return Preferences.systemNodeForPackage(c);
            } catch (Throwable t) {
                return new PreferencesStub(false);
            }
        });
    }

    /**
     * Gets the user node for the package of the class if permitted, gets a
     * stub otherwise.
     *
     * @param c The class
     * @return user node or a proxy.
     */
    public static Preferences userNodeForPackage(@NonNull Class<?> c) {
        return userNodes.computeIfAbsent(c.getPackage(), pckg -> {
            try {
                return Preferences.userNodeForPackage(c);
            } catch (Throwable t) {
                return new PreferencesStub(true);
            }
        });
    }

    /**
     * Prevents instance creation.
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
    public static void installStagePrefsHandler(@NonNull final Preferences prefs, final String name, @NonNull Stage stage) {
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
     * @param defaultSize This size is used when no preferences are stored yet for
     *                    this window.
     */
    public static void installStagePrefsHandler(@NonNull final Preferences prefs, final String name, @NonNull Stage stage, @NonNull Dimension2D defaultSize) {

        double prefWidth;
        double prefHeight;

        prefWidth = prefs.getDouble(name + ".width", defaultSize.getWidth());
        prefHeight = prefs.getDouble(name + ".height", defaultSize.getHeight());
        Screen primary = Screen.getPrimary();
        Rectangle2D screenBounds = primary == null ? null : primary.getBounds();

        if (prefWidth > 0 && screenBounds != null && prefWidth <= screenBounds.getWidth()) {
            stage.setWidth(prefWidth);
        }
        if (prefHeight > 0 && screenBounds != null && prefHeight <= screenBounds.getHeight()) {
            stage.setHeight(prefHeight);
        }

        stage.widthProperty().addListener((o, oldValue, newValue) -> prefs.putDouble(name + ".width", newValue.doubleValue()));
        stage.heightProperty().addListener((o, oldValue, newValue) -> prefs.putDouble(name + ".height", newValue.doubleValue()));
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
    public static void installVisibilityPrefsHandlers(@NonNull Preferences prefs, @NonNull Node node, @NonNull BooleanProperty visibilityProperty, @NonNull SplitPane splitPane, Side side) {
        ChangeListener<? super Number> positionListener = (o, oldValue, newValue) -> prefs.putDouble(visibilityProperty.getName() + ".dividerPosition", newValue.doubleValue());

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

    public static void installBooleanPropertyHandler(@NonNull final Preferences prefs, final String name, @NonNull BooleanProperty property) {
        boolean prefValue = prefs.getBoolean(name, true);
        property.setValue(prefValue);
        property.addListener((o, oldValue, newValue) -> prefs.putBoolean(name, newValue));
    }
}
