package com.anthonycosenza.rendering.text.tables;

import com.anthonycosenza.rendering.text.ByteReader;
import com.anthonycosenza.rendering.text.FontData;

public class GPOS extends OpenTypeTable
{
    public GPOS(int recordIndex, int recordLength, long checksum, FontData fontData, ByteReader reader)
    {
        super("GPOS", recordIndex, recordLength, checksum, fontData, reader);
    }
    
    @Override
    protected void decode(FontData fontData, ByteReader reader)
    {
    
    }
}