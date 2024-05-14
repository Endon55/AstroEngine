package com.anthonycosenza.engine.loader.text.tables.types;

import com.anthonycosenza.engine.loader.text.tables.types.hints.Hint;
import com.anthonycosenza.engine.loader.text.tables.types.points.FontPoint;

import java.util.List;

public interface Glyph
{
    List<List<FontPoint>> getPaths();
    List<Hint> getHints();
    
    float getMinX();
    
    float getMaxX();
    
    float getMinY();
    
    float getMaxY();
    float getWidth();
    float getHeight();
    
    void shiftOffset();
    boolean hasCurves();
    void removeCurves(float smoothness);
    Glyph getAtSize(int unitsPerEm, float fontSize);
    
    float getSize();
    
}
