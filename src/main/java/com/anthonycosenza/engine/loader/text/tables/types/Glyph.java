package com.anthonycosenza.engine.loader.text.tables.types;

import com.anthonycosenza.engine.loader.text.tables.types.hints.Hint;
import com.anthonycosenza.engine.loader.text.tables.types.points.FontPoint;

import java.util.List;

public interface Glyph
{
    List<List<FontPoint>> getPaths();
    List<Hint> getHints();
    int getMinX();
    int getMaxX();
    int getMinY();
    int getMaxY();
    int getWidth();
    int getHeight();
    
    void shiftOffset();
    boolean hasCurves();
    void removeCurves(float smoothness);
    Glyph getAtSize(int unitsPerEm, float fontSize);
    
    float getSize();
    
}
