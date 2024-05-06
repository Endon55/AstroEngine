package com.anthonycosenza.engine.loader.text.tables;

import com.anthonycosenza.engine.loader.text.ByteReader;
import com.anthonycosenza.engine.loader.text.FontData;

public class OS2 extends OpenTypeTable
{
    public OS2(int recordIndex, int recordLength, long checksum, FontData fontData, ByteReader reader)
    {
        super("OS/2", recordIndex, recordLength, checksum, fontData, reader);
    }
    
    @Override
    protected void decode(FontData fontData, ByteReader reader)
    {
    
    }
}