package com.anthonycosenza.text;

import com.anthonycosenza.text.tables.types.points.FontPoint;

import java.util.List;

public class Font
{
    private FontData fontData;
    
    public Font(String filepath)
    {
        fontData = FontData.decodeFont(filepath);
    }
    public List<FontPoint> getGlyph(int index)
    {
        return fontData.cffCharStringIndex.getData().get(index).getGlyphPath().getPoints();
    }
    
}
