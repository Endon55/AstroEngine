package com.anthonycosenza.engine.loader.text.tables;

import com.anthonycosenza.engine.loader.text.ByteReader;
import com.anthonycosenza.engine.loader.text.FontData;

public class Hmtx extends OpenTypeTable
{
    public Hmtx(int recordIndex, int recordLength, long checksum, FontData fontData, ByteReader reader)
    {
        super("hmtx", recordIndex, recordLength, checksum, fontData, reader);
    }
    
    @Override
    protected void decode(FontData fontData, ByteReader reader)
    {
    
    }
}
