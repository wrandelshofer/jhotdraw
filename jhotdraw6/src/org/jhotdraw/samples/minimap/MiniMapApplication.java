package CH.ifa.draw.samples.minimap;

import CH.ifa.draw.contrib.*;
import CH.ifa.draw.framework.DrawingView;
import CH.ifa.draw.figures.ImageFigure;
import CH.ifa.draw.util.Iconkit;

import javax.swing.*;
import java.awt.*;

public class MiniMapApplication extends SplitPaneDrawApplication {
	protected Desktop createDesktop() {
		return new MiniMapDesktop();
	}

	public static void main(String[] args) {
		final MiniMapApplication window = new MiniMapApplication();
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
