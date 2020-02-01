/*
 * @(#)AboutAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.grapher.action;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.ApplicationLabels;
import org.jhotdraw8.app.ApplicationModel;
import org.jhotdraw8.app.action.AbstractApplicationAction;
import org.jhotdraw8.app.action.app.AboutAction;

import java.lang.module.ModuleReference;
import java.lang.module.ResolvedModule;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Displays a dialog showing information about the application.
 * <p>
 *
 * @author Werner Randelshofer
 */
public class GrapherAboutAction extends AbstractApplicationAction {

    private static final long serialVersionUID = 1L;

    public static final String ID = AboutAction.ID;

    /**
     * Creates a new instance.
     *
     * @param app the application
     */
    public GrapherAboutAction(Application app) {
        super(app);
        ApplicationLabels.getResources().configureAction(this, ID);
    }

    @Override
    protected void onActionPerformed(ActionEvent event, @Nullable Application app) {
        if (app == null) {
            return;
        }

        addDisabler(this);
        ApplicationModel model = app.getModel();

        String name = model.getName();
        String version = model.getVersion();
        String vendor = model.getVendor();
        String license = model.getLicense();


        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                (vendor == null ? "" : vendor)
                        + (license == null ? "" : "\n" + license)
                        + "\n\nRunning on"
                        + "\n  Java: " + System.getProperty("java.version")
                        + ", " + System.getProperty("java.vendor")
                        + "\n  JVM: " + System.getProperty("java.vm.version")
                        + ", " + System.getProperty("java.vm.vendor")
                        + "\n  OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version")
                        + ", " + System.getProperty("os.arch")
                        + "\n\nModules:"
                        + "\n"
                        + getDependencies()
        );
        alert.getDialogPane().setMaxWidth(640.0);
        alert.setHeaderText((name == null ? "unnamed" : name) + (version == null ? "" : " " + version));
        alert.setGraphic(null);
        alert.initModality(Modality.NONE);
        alert.showingProperty().addListener((observable, oldValue, newValue) -> {
                    if (!newValue) {
                        removeDisabler(this);
                    }
                }
        );
        alert.getDialogPane().getScene().getStylesheets().addAll(
                getApplication().getModel().getSceneStylesheets().asList()
        );
        alert.show();
    }

    private String getDependencies() {
        return
                ModuleLayer.boot().modules().stream()
                        .map(m -> {
                            // Get version string from descriptor if available
                            if (m.getDescriptor().version().isPresent()) {
                                return m.getDescriptor().toNameAndVersion();
                            }
                            // Construct version string from jar file name
                            Pattern pattern = Pattern.compile("-(\\w+(?:[.\\-+]\\w+)*).jar$");
                            String version = m.getLayer().configuration()
                                    .findModule(m.getName())
                                    .map(ResolvedModule::reference)
                                    .map(ModuleReference::location).flatMap(Function.identity())
                                    .map(uri -> {
                                        Matcher matcher = pattern.matcher(uri.getPath());
                                        return matcher.find() ? matcher.group(1) : null;
                                    }).orElse(null);

                            return version == null ? m.getName() : m.getName() + "@" + version;
                        })
                        .filter(str -> !str.startsWith("java.") && !str.startsWith("javafx.") && !str.startsWith("jdk."))
                        .sorted()
                        .collect(Collectors.joining("\n  ", "  ", ""));
    }
}
