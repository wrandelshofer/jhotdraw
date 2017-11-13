/* @(#)AbstractApplicationAction.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action;

import java.util.concurrent.CompletionException;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import org.jhotdraw8.app.Application;

/**
 * This abstract class can be extended to implement an {@code Action} that acts
 * on an {@link Application}.
 * <p>
 * An AbstractApplicationAction is disabled when it has disablers
 * {@link org.jhotdraw8.app.Disableable} or when its application is disabled.
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public abstract class AbstractApplicationAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    protected Application app;

    /**
     * Creates a new instance.
     *
     * @param app the application
     */
    public AbstractApplicationAction(Application app) {
        if (app == null) {
            throw new IllegalArgumentException("app is null");
        }
        this.app = app;
        disabled.unbind();
        disabled.bind(Bindings.isNotEmpty(disablers).or(app.disabledProperty()));
    }

    protected String createErrorMessage(Throwable t) {
        StringBuilder buf = new StringBuilder();
        if ((t instanceof CompletionException) && t.getCause() != null) {
            t = t.getCause();
        }

        final String msg = t.getLocalizedMessage();
        if (buf.length() != 0) {
            buf.append('\n');
        }
        buf.append(msg == null ? t.toString() : msg);
        return buf.toString();
    }

    public final Application getApplication() {
        return app;
    }

    @Override
    protected final void handleActionPerformed(ActionEvent event) {
        handleActionPerformed(event, app);
    }

    /**
     * This method is invoked when the action is not disabled and the event is
     * not consumed.
     *
     * @param event the action event
     * @param app the applicatoin
     */
    protected abstract void handleActionPerformed(ActionEvent event, Application app);

}
