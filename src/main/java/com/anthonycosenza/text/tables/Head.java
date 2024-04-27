package com.anthonycosenza.text.tables;

import com.anthonycosenza.text.ByteReader;
import com.anthonycosenza.text.FontData;

public class Head extends OpenTypeTable
{
    
    public Head(int index, int recordLength, long checksum, FontData fontData, ByteReader reader)
    {
        super("head", index, recordLength, checksum, fontData, reader);
    }
    
    @Override
    protected void decode(FontData fontData, ByteReader reader)
    {
    }
}
