package com.anthonycosenza.rendering.text.tables;

import com.anthonycosenza.rendering.text.ByteReader;
import com.anthonycosenza.rendering.text.FontData;

public class Maxp extends OpenTypeTable
{
    public Maxp(int recordIndex, int recordLength, long checksum, FontData fontData, ByteReader reader)
    {
        super("maxp", recordIndex, recordLength, checksum, fontData, reader);
    }
    
    @Override
    protected void decode(FontData fontData, ByteReader reader)
    {
    
    }
}
