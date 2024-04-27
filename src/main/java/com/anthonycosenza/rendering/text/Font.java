package com.anthonycosenza.rendering.text;

public class Font
{
    private FontData fontData;
    
    public Font(String filepath)
    {
        fontData = FontData.decodeFont(filepath);
    }
    
    
}
