package com.anthonycosenza.engine.loader.text.tables.types;

import com.anthonycosenza.engine.util.reader.ByteReader;

import java.util.ArrayList;
import java.util.List;

public class cffCharset
{
    private int format;
    private List<Integer> charset;
    
    public cffCharset(int glyphCount, ByteReader reader)
    {
        charset = new ArrayList<>();
        format = reader.getUnsignedInt8();
        
    
        for(int i = 0; i < glyphCount; i++)
        {
            //These are SIDs(String Identifiers)
            charset.add(reader.getUnsignedInt16());
        }
        
    }
    
    
}
