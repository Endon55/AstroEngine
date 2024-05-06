package com.anthonycosenza.engine.loader.text.tables;

import com.anthonycosenza.engine.loader.text.ByteReader;
import com.anthonycosenza.engine.loader.text.FontData;

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