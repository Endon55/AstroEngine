package com.anthonycosenza.rendering.text.tables.types;

import com.anthonycosenza.rendering.text.ByteReader;

public class cffEncoding
{
    private int format;
    private int encodedGlyphs;
    
    public cffEncoding(ByteReader reader)
    {
        System.out.println("Encoding Index: " + reader.pointer);
        format = reader.getUnsignedInt8();
        encodedGlyphs = reader.getUnsignedInt8();
        System.out.println("format: " + format);
        System.out.println("glyphs: " + encodedGlyphs);
    
        for(int i = 0; i < encodedGlyphs; i++)
        {
            
        }
        
        
    }
}
