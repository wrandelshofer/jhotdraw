/*
 * @(#)ZoomTool.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.contrib.zoom;

import CH.ifa.draw.framework.DrawingEditor;
import CH.ifa.draw.framework.Tool;
import CH.ifa.draw.standard.AbstractTool;

import java.awt.event.InputEvent;
import CH.ifa.draw.framework.DrawingViewMouseEvent;


/**
 * @author Andre Spiegel <spiegel@gnu.org>
 * @version <$CURRENT_VERSION$>
 */
public class ZoomTool extends AbstractTool {

	private Tool child;

	public ZoomTool(DrawingEditor editor) {
		super(editor);
	}

	public void mouseDown(DrawingViewMouseEvent dvme) {
		super.mouseDown(dvme);
		int x = getAnchorX();
		int y = getAnchorY();
		//  Added handling for SHIFTed and CTRLed BUTTON3_MASK so that normal
		//  BUTTON3_MASK does zoomOut, SHIFTed BUTTON3_MASK does zoomIn
		//  and CTRLed BUTTON3_MASK does deZoom
		if ((dvme.getMouseEvent().getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
			if (child != null) {
				return;
			}
			view().freezeView();
			child = new ZoomAreaTracker(editor());
			child.mouseDown(dvme);
		}
		else if ((dvme.getMouseEvent().getModifiers() & InputEvent.BUTTON2_MASK) != 0) {
			((ZoomDrawingView) view()).deZoom(x, y);
		}
		else if ((dvme.getMouseEvent().getModifiers() & InputEvent.BUTTON3_MASK) != 0) {
			if ((dvme.getMouseEvent().getModifiers() & InputEvent.SHIFT_MASK) != 0) {
				((ZoomDrawingView)view()).zoomIn(x, y);
			}
			else if ((dvme.getMouseEvent().getModifiers() & InputEvent.CTRL_MASK) != 0) {

				((ZoomDrawingView) view()).deZoom(x, y);
			}
			else {
				((ZoomDrawingView)view()).zoomOut(x, y);
			}
		}
	}

	public void mouseDrag(DrawingViewMouseEvent dvme) {
		if (child != null) {
			child.mouseDrag(dvme);
		}
	}

	public void mouseUp(DrawingViewMouseEvent dvme) {
		if (child != null) {
			view().unfreezeView();
			child.mouseUp(dvme);
		}
		child = null;
	}
}
