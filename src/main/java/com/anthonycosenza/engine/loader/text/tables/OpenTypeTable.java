package com.anthonycosenza.engine.loader.text.tables;

import com.anthonycosenza.engine.util.reader.ByteReader;
import com.anthonycosenza.engine.loader.text.FontData;

public abstract class OpenTypeTable
{
    protected int recordIndex;
    protected String tag;
    protected int recordLength;
    protected long checksum;
    
    
    public OpenTypeTable(String tag, int recordIndex, int recordLength, long checksum, FontData fontData, ByteReader reader)
    {
        //Index should include the table tag which we don't need anymore
        this.recordIndex = recordIndex;
        this.tag = tag;
        reader.pointer = recordIndex;
        this.recordLength = recordLength;
        this.checksum = checksum;
        decode(fontData, reader);
    }
    
    private void validateChecksum(ByteReader reader)
    {
        int pointer = reader.pointer;
        //reader.pointer = recordIndex;
        System.out.println("Pointer: " + pointer + " Start Index: " + recordIndex);
        
        //Rounding up to the nearest 4
        int paddedLength = (int)((recordLength + 3) & 65532L) >> 2;
        
        //int paddedLength = (int) (4 * Math.ceil(recordLength / 4.0));
        System.out.println("Length: " + recordLength + " w/Padding: " + paddedLength);
    
        long sum = 0;
        for(int i = 0; i < paddedLength; i++)
        {
            sum += reader.getUnsignedInt32();
        }
        System.out.println("Sum: " + sum);
        System.out.println("Checksum: " + checksum);
        if(sum != checksum)
        {
            throw new RuntimeException("Checksum failed for table: " + tag);
        }
        
        reader.pointer = pointer;
    }
    
    
    public int getRecordIndex()
    {
        return recordIndex;
    }
    
    public String getTag()
    {
        return tag;
    }
    
    
    public int getDataStartIndex()
    {
        return (int) recordIndex;
    }
    


    
    protected abstract void decode(FontData fontData, ByteReader reader);
    
    
    @Override
    public String toString()
    {
        return "OpenTypeTable{" +
                "tag='" + tag + '\'' +
                ", recordIndex=" + recordIndex +
                '}';
    }
}
