/*
 * @(#)FigureAttributeConstant.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	© by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.framework;

import java.io.Serializable;
import java.util.Vector;
import java.util.Enumeration;

/**
 * A FigureAttribute is a constant for accessing a special figure attribute. It
 * does not contain a value but just defines a unique attribute ID. Therefore,
 * they provide a type-safe way of defining attribute constants.
 * (SourceForge feature request ID: <>)
 *
 * @author Wolfram Kaiser, CL Gilbert(dnoyeb@users.sourceforge.net)
 * @version <$CURRENT_VERSION$>
 */
public class FigureAttributeConstant extends JHDType implements Serializable, Cloneable {

	//should these all be strings?
	//yes should all be strings or simple type
	//different types are really just different ID scopes.  this is only needed
	//when end users want their own attributes for whatever reason and can not
	//know for certain if JHD will ever add new attributes that encroach on their
	//IDs.  so the color and string and integer types are flawed.
	//attributes are generic types only determined at load or store time.
	public static final String FRAME_COLOR_STR = "FrameColor";
	public static final FigureAttributeConstant FRAME_COLOR = new FigureAttributeConstant(FRAME_COLOR_STR, 1);

	public static final String FILL_COLOR_STR = "FillColor";
	public static final FigureAttributeConstant FILL_COLOR = new FigureAttributeConstant(FILL_COLOR_STR, 2);

	public static final String TEXT_COLOR_STR = "TextColor";
	public static final FigureAttributeConstant TEXT_COLOR = new FigureAttributeConstant(TEXT_COLOR_STR, 3);

	public static final String ARROW_MODE_STR = "ArrowMode";
	public static final FigureAttributeConstant ARROW_MODE = new FigureAttributeConstant(ARROW_MODE_STR, 4);

	public static final String FONT_NAME_STR = "FontName";
	public static final FigureAttributeConstant FONT_NAME = new FigureAttributeConstant(FONT_NAME_STR, 5);

	public static final String FONT_SIZE_STR = "FontSize";
	public static final FigureAttributeConstant FONT_SIZE = new FigureAttributeConstant(FONT_SIZE_STR, 6);

	public static final String FONT_STYLE_STR = "FontStyle";
	public static final FigureAttributeConstant FONT_STYLE = new FigureAttributeConstant(FONT_STYLE_STR, 7);

	public static final String URL_STR = "URL";
	public static final FigureAttributeConstant URL = new FigureAttributeConstant(URL_STR, 8);

	public FigureAttributeConstant(java.lang.String newName, int newID) {
		super(newName,newID);
	}

	/**
	 * This is overall flawed because it conflicts with getConstant which assumes
	 * that constants have unique Names.  While this method itself will create
	 * many constants all with the same name.
	 * 
	 * Warning, deserialization issues can occur when using this constructor.
	 * @deprecated Does not work well with deserialization of end users own 
	 *             FigureAttributeConstants.
	 */
	public FigureAttributeConstant(java.lang.String newName) {
		super(newName);
		System.err.println("WARNING: FigureAttributeConstant(String) has been deprecated.");
		Thread.dumpStack();
	}

	/**
	 * @return an existing constant for a given name or create a new one
	 */
	public static FigureAttributeConstant getConstant(java.lang.String constantName) {
		FigureAttributeConstant fac =(FigureAttributeConstant) JHDType.getConstant(FigureAttributeConstant.class, constantName);
		if( fac == null){
			fac = new FigureAttributeConstant(constantName, JHDType.getMaxValue(FigureAttributeConstant.class) +1);
		}
	    return fac;
	}
	public static FigureAttributeConstant getConstant(int constantId) {
		return (FigureAttributeConstant) JHDType.getConstant(FigureAttributeConstant.class, constantId);
	}
}
