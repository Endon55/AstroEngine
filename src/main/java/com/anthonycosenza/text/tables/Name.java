package com.anthonycosenza.text.tables;

import com.anthonycosenza.text.ByteReader;
import com.anthonycosenza.text.FontData;

public class Name extends OpenTypeTable
{
    public Name(int recordIndex, int recordLength, long checksum, FontData fontData, ByteReader reader)
    {
        super("name", recordIndex, recordLength, checksum, fontData, reader);
    }
    
    @Override
    protected void decode(FontData fontData, ByteReader reader)
    {
    
    }
}
