/*
 * @(#)SelectAreaTracker.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.standard;

import java.awt.*;

import CH.ifa.draw.framework.*;

/**
 * SelectAreaTracker implements a rubberband selection of an area.
 *
 * @version <$CURRENT_VERSION$>
 */
public class SelectAreaTracker extends AbstractTool {
    /** Selected rectangle in physical coordinates space */
	private Rectangle fSelectGroup;
    private Color fRubberBandColor;

	public SelectAreaTracker(DrawingEditor newDrawingEditor) {
        this(newDrawingEditor, Color.black);
    }

    public SelectAreaTracker(DrawingEditor newDrawingEditor, Color rubberBandColor) {
		super(newDrawingEditor);
        fRubberBandColor = rubberBandColor;
	}

	public void mouseDown(DrawingViewMouseEvent dvme) {
		super.mouseDown(dvme);
		// use event coordinates to supress any kind of
		// transformations like constraining points to a grid
		//I disagree, selection may be unconstrained, but drag should be. I did not change however.
		//also notice the the drag function is using constrained values
		setAnchorX( dvme.getMouseEvent().getX() );
		setAnchorY( dvme.getMouseEvent().getY() );
		rubberBand(getAnchorX(), getAnchorY(), getAnchorX(), getAnchorY());
	}

	public void mouseDrag(DrawingViewMouseEvent dvme) {
		super.mouseDrag(dvme);
		eraseRubberBand();
		rubberBand(getAnchorX(), getAnchorY(), dvme.getMouseEvent().getX(), dvme.getMouseEvent().getY());
	}

	public void mouseUp(DrawingViewMouseEvent dvme) {
		eraseRubberBand();
		selectGroup(dvme.getMouseEvent().isShiftDown());
		super.mouseUp(dvme);
	}

	private void rubberBand(int x1, int y1, int x2, int y2) {
		fSelectGroup = new Rectangle(new Point(x1, y1));
		fSelectGroup.add(new Point(x2, y2));
		drawXORRect(fSelectGroup);
	}

	private void eraseRubberBand() {
		drawXORRect(fSelectGroup);
	}

	private void drawXORRect(Rectangle r) {
		Graphics g = view().getGraphics();
		if ( g != null ) {
			try {
                if (g instanceof Graphics2D) {
                    // Do dotted-line in Java2
                    Stroke dashedStroke = new BasicStroke(1.0f,
                        BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER,
                        10.0f, new float[] {5f, 5f, 5f, 5f}, 5.0f);
                    ((Graphics2D) g).setStroke(dashedStroke);
                }

				g.setXORMode(view().getBackground());
                g.setColor(fRubberBandColor);
				g.drawRect(r.x, r.y, r.width, r.height);
			}
			finally {
				g.dispose(); // SF bugtracker id: #490663
			}
		}
	}

	private void selectGroup(boolean toggle) {
		FigureEnumeration fe = drawing().figuresReverse();
		while (fe.hasNextFigure()) {
			Figure figure = fe.nextFigure();
			Rectangle r2 = figure.displayBox();
			if (fSelectGroup.contains(r2.x, r2.y) && fSelectGroup.contains(r2.x+r2.width, r2.y+r2.height)) {
				if (toggle) {
					view().toggleSelection(figure);
				}
				else {
					view().addToSelection(figure);
				}
			}
		}
	}
}
