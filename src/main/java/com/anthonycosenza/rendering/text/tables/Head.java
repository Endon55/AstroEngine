package com.anthonycosenza.rendering.text.tables;

import com.anthonycosenza.rendering.text.ByteReader;
import com.anthonycosenza.rendering.text.FontData;

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