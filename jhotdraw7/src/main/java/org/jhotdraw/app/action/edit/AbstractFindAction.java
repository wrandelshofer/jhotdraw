/* @(#)AbstractFindAction.java
 * Copyright © 1996-2017 The authors and contributors of JHotDraw.
 * MIT License, CC-by License, or LGPL License.
 */

package org.jhotdraw.app.action.edit;

import edu.umd.cs.findbugs.annotations.Nullable;
import org.jhotdraw.app.*;
import org.jhotdraw.app.action.AbstractViewAction;
import org.jhotdraw.util.*;

/**
 * Presents a find dialog to the user and then highlights the found items
 * in the active view.
 * <p>
 * This action is called when the user selects the Find item in the Edit
 * menu. The menu item is automatically created by the application.
 * <p>
 * If you want this behavior in your application, you have to create an action
 * with this ID and put it in your {@code ApplicationModel} in method
 * {@link ApplicationModel#initApplication}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractFindAction extends AbstractViewAction {
    private static final long serialVersionUID = 1L;
    public static final String ID = "edit.find";
    
    /** Creates a new instance. */
    public AbstractFindAction(Application app, @Nullable View view) {
        super(app, view);
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
        labels.configureAction(this, ID);
    }    
}
