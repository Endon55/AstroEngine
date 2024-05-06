package com.anthonycosenza.engine.loader.text.tables;

import com.anthonycosenza.engine.util.reader.ByteReader;
import com.anthonycosenza.engine.loader.text.FontData;

public class Post extends OpenTypeTable
{
    public Post(int recordIndex, int recordLength, long checksum, FontData fontData, ByteReader reader)
    {
        super("post", recordIndex, recordLength, checksum, fontData, reader);
    }
    
    @Override
    protected void decode(FontData fontData, ByteReader reader)
    {
    
    }
}
