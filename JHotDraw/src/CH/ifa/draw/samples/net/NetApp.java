/*
 * @(#)NetApp.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.samples.net;

import javax.swing.JToolBar;
import CH.ifa.draw.framework.*;
import CH.ifa.draw.standard.*;
import CH.ifa.draw.figures.*;
import CH.ifa.draw.application.DrawApplication;

/**
 * @version <$CURRENT_VERSION$>
 */
public  class NetApp extends DrawApplication {

	NetApp() {
		super("Net");
	}

	protected void createTools(JToolBar palette) {
		super.createTools(palette);

		Tool tool = new TextTool(this, new NodeFigure());
		palette.add(createToolButton(IMAGES + "TEXT", "Text Tool", tool));

		tool = new CreationTool(this, new NodeFigure());
		palette.add(createToolButton(IMAGES + "RECT", "Create Org Unit", tool));

		tool = new ConnectionTool(this, new LineConnection());
		palette.add(createToolButton(IMAGES + "CONN", "Connection Tool", tool));
	}

	//-- main -----------------------------------------------------------

	public static void main(String[] args) {
		final DrawApplication window = new NetApp();
		window.open();
		Runnable r = new Runnable() {
			public void run() {
				window.newWindow();
			}
		};
		try {
			java.awt.EventQueue.invokeAndWait( r );
		}
		catch(java.lang.InterruptedException ie){
			System.err.println(ie.getMessage());
			window.exit();
		}
		catch(java.lang.reflect.InvocationTargetException ite){
			System.err.println(ite.getMessage());
			window.exit();
		}
	}
}
