package com.anthonycosenza.engine.loader.text.tables;

import com.anthonycosenza.engine.util.reader.ByteReader;
import com.anthonycosenza.engine.loader.text.FontData;

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
