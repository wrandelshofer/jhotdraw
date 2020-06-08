/*
 * @(#)FontAction.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.teddy.action;

import javafx.event.ActionEvent;
import javafx.scene.text.Font;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.Activity;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.action.AbstractActivityAction;
import org.jhotdraw8.gui.fontchooser.FontDialog;
import org.jhotdraw8.gui.fontchooser.FontFamilySize;
import org.jhotdraw8.samples.teddy.TeddyLabels;

import java.util.Optional;

public class FontAction extends AbstractActivityAction {

    private FontDialog fontDialog;
    public final static String ID = "format.font";

    /**
     * Creates a new instance which acts on the specified activity of the
     * application.
     *
     * @param app      The application.
     * @param activity The activity. If activity is null then the action acts on
     *                 the active activity of the application. Otherwise it will act on the
     *                 specified activity.
     */
    public FontAction(@NonNull Application app, @Nullable Activity activity) {
        super(app, activity, FontableActivity.class);
        TeddyLabels.getResources().configureAction(this, ID);
        disabledProperty().addListener((o, oldv, newv) -> {
            System.out.println("FontAction.disabled=" + newv);
        });
    }

    @Override
    protected void onActionPerformed(ActionEvent event, Activity activity) {
        if (fontDialog == null) {
            fontDialog = new FontDialog();
            fontDialog.initOwner(activity.getNode().getScene().getWindow());
        }
        FontableActivity foa = (FontableActivity) activity;
        Optional<FontFamilySize> fontFamilySize = fontDialog.showAndWait(
                new FontFamilySize(foa.getFont().getFamily(), foa.getFont().getSize()));
        fontFamilySize.ifPresent(f -> foa.setFont(Font.font(f.getFamily(), f.getSize())));
    }
}
