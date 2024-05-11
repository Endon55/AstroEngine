package com.anthonycosenza.engine.loader.text;

import com.anthonycosenza.engine.loader.text.tables.types.Glyph;
import com.anthonycosenza.engine.loader.text.tables.types.points.FontPoint;

import java.util.List;

public class Font
{
    private FontData fontData;
    
    public Font(String filepath)
    {
        fontData = FontData.decodeFont(filepath);
    }

    public Glyph getGlyph(int charCode)
    {
        return fontData.glyphs.get(getGlyphCode(charCode));
    }
    
    public int getGlyphCode(int charCode)
    {
        return fontData.getGlyphCode(charCode);
    }
    
    public FontData getFontData()
    {
        return fontData;
    }
}
