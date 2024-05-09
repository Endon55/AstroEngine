package com.anthonycosenza.engine.loader.text;

import com.anthonycosenza.engine.loader.text.tables.types.points.FontPoint;

import java.util.List;

public class Font
{
    private FontData fontData;
    
    public Font(String filepath)
    {
        fontData = FontData.decodeFont(filepath);
    }
    public List<List<FontPoint>> getGlyph(int index)
    {
        return fontData.cffCharStringIndex.getData().get(index).getGlyphPath().getPaths();
    }
    
}
