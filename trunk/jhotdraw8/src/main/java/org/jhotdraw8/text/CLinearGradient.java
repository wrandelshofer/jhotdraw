/* @(#)CLinearGradient.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.text;

import java.util.List;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;

/**
 * CLinearGradient.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class CLinearGradient implements Paintable {
    private final String name;
    private LinearGradient linearGradient;
    private final double startX;
    private final double startY;
    private final double endX;
    private final double endY;
    private final boolean proportional;
    private final CycleMethod cycleMethod;
    private final CStop[] cstops;

    public CLinearGradient(double startX, double startY, double endX, double endY, boolean proportional, CycleMethod cycleMethod
    , CStop... stops) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.proportional = proportional;
        this.cycleMethod = cycleMethod;
        this.name=null;
        this.cstops=stops;
    }
    

    public CLinearGradient(LinearGradient linearGradient) {
        this(null,linearGradient);
    }
    public CLinearGradient(String name, LinearGradient linearGradient) {
        this.name = name;
        this.linearGradient = linearGradient;
        this.startX=linearGradient.getStartX();
        this.startY=linearGradient.getStartY();
        this.endX=linearGradient.getEndX();
        this.endY=linearGradient.getEndY();
        this.proportional=linearGradient.isProportional();
        this.cycleMethod=linearGradient.getCycleMethod();
        List<Stop> stopList=linearGradient.getStops();
        cstops=new CStop[stopList.size()];
        for (int i=0;i<cstops.length;i++) {
            Stop stop=stopList.get(i);
            cstops[i]=new CStop(stop.getOffset(),new CColor(stop.getColor()));
        }
    }

    public String getName() {
        return name;
    }

    public LinearGradient getLinearGradient() {
        if (linearGradient==null) {
            Stop[] stops=new Stop[cstops.length];
        for (int i=0;i<cstops.length;i++) {
            CStop cstop=cstops[i];
            double offset;
            if (cstop.getOffset()==null) {
                int left=i, right=i;
                for (;left>0&&cstops[left].getOffset()==null;left--);
                for (;right<cstops.length-1&&cstops[right].getOffset()==null;right++);
                double leftOffset=cstops[left].getOffset()==null?0.0:cstops[left].getOffset();
                double rightOffset=cstops[right].getOffset()==null?1.0:cstops[right].getOffset();
                if (i==left) {
                    offset=leftOffset;
                }else                if (i==right) {
                    offset=rightOffset;
                }else{
                    double mix=(double)(i-left)/(right-left);
                    offset=leftOffset*(1-mix)+rightOffset*mix;
                }
            }else{
                offset=cstop.getOffset();
            }
            
            stops[i]=new Stop(offset,cstop.getColor().getColor());
        }
            linearGradient=new LinearGradient(startX,startY,endX,endY,proportional,cycleMethod,stops);
        }
        return linearGradient;
    }

    @Override
    public Paint getPaint() {
       return linearGradient;
    }


}
