package com.anthonycosenza.text.tables;

import com.anthonycosenza.text.ByteReader;
import com.anthonycosenza.text.FontData;

public class Kern extends OpenTypeTable
{
    public Kern(int recordIndex, int recordLength, long checksum, FontData fontData, ByteReader reader)
    {
        super("kern", recordIndex, recordLength, checksum, fontData, reader);
    }
    
    @Override
    protected void decode(FontData fontData, ByteReader reader)
    {
    
    }
}
