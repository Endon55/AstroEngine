package com.anthonycosenza.rendering.text.tables;

import com.anthonycosenza.rendering.text.ByteReader;
import com.anthonycosenza.rendering.text.FontData;

public class Hhea extends OpenTypeTable
{
    public Hhea(int recordIndex, int recordLength, long checksum, FontData fontData, ByteReader reader)
    {
        super("hhea", recordIndex, recordLength, checksum, fontData, reader);
    }
    
    @Override
    protected void decode(FontData fontData, ByteReader reader)
    {
    
    }
}