package com.anthonycosenza.engine.loader.text.tables.types;


import com.anthonycosenza.engine.util.reader.ByteReader;
import com.anthonycosenza.engine.loader.text.FontData;

import java.util.ArrayList;
import java.util.List;

public class cffIndex<T>
{
    private int entryCount;
    private int offsetByteCount;
    private List<T> data;
    private int length;
    
    
    public cffIndex(FontData fontData, ByteReader reader, Class<T> clazz)
    {
        data = new ArrayList<>();
        int startIndex = reader.pointer;
        entryCount = reader.getUnsignedInt16();
        
        if(entryCount == 0)
        {
            length = 2;
            return;
        }
        offsetByteCount = reader.getUnsignedInt8();
    
        //System.out.println("Entries: " + entryCount);
        
        int lastOffset = getOffsetValue(offsetByteCount, reader);
        int[] offsets = new int[entryCount];
        //read all offsets.
        for(int i = 0; i < entryCount; i++)
        {
            offsets[i] = getOffsetValue(offsetByteCount, reader);
        }
        //System.out.println("Offsets: " + Arrays.toString(offsets));
        int totalOffset = 0;
        int tableStart = reader.pointer;
        //System.out.println("TableStart: " + tableStart);
        for(int i = 0; i < entryCount; i++)
        {
            //System.out.println(i + "/" + (entryCount - 1));
            int entryLength = offsets[i] - lastOffset;
            if(clazz.equals(cffDict.class))
            {
                data.add((T) new cffDict(fontData, reader));
            }
            else if(clazz.equals(String.class))
            {
                data.add((T) reader.getString(entryLength));
            }
            else if(clazz.equals(cffGlobalSubroutine.class))
            {
                throw new RuntimeException("Implement GlobalSubroutines");
            }
            else if(clazz.equals(cffCharString.class))
            {
                if(i == 24)
                {
                    System.out.println("O");
                }
                data.add((T) new cffCharString(entryLength, fontData, reader));
            }
            else if(clazz.equals(cffSubroutine.class))
            {
                data.add((T) new cffSubroutine(entryLength, tableStart + lastOffset - 1, reader));
            }
    
            lastOffset = offsets[i];
            totalOffset += entryLength;
        }
        length = reader.pointer - startIndex;
    }
    
    private int getOffsetValue(int offsetSize, ByteReader reader)
    {
        if(offsetSize == 1)
        {
            return reader.getUnsignedInt8();
        }
        else if(offsetSize == 2)
        {
            return reader.getUnsignedInt16();
        }
        else if(offsetSize == 3)
        {
            return reader.getUnsignedInt24();
        }
        throw new RuntimeException("Can't handle offsets of size: " + offsetSize);
    }
    
    public T getIndex(int index)
    {
        if(index > data.size())
        {
            throw new ArrayIndexOutOfBoundsException("Attempting to access cffData that doesn't exist: " + index + ", max: " + data.size());
        }
        return data.get(index);
    }
    
    
    public int getLength()
    {
        return length;
    }
    
    public List<T> getData()
    {
        return data;
    }
}
