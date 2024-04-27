package com.anthonycosenza.rendering.text.tables;

import com.anthonycosenza.rendering.text.ByteReader;
import com.anthonycosenza.rendering.text.FontData;
import com.anthonycosenza.rendering.text.tables.encoding.Format4Encoding;
import com.anthonycosenza.rendering.text.tables.encoding.FormatEncoding;

import java.util.ArrayList;
import java.util.List;

public class Cmap extends OpenTypeTable
{
    //https://learn.microsoft.com/en-us/typography/opentype/spec/cmap#unicode-platform-platform-id--0
    private int version;
    private int subTablesCount;
    private FormatEncoding encoding;
    
    public Cmap(int index, int recordLength, long checksum, FontData fontData, ByteReader reader)
    {
        super("cmap", index, recordLength, checksum, fontData, reader);
    }
    
    @Override
    protected void decode(FontData fontData, ByteReader reader)
    {
        //Header
        int version = reader.getUnsignedInt16();
        int tablesCount = reader.getUnsignedInt16();
    
        System.out.println("version: " + version);
        System.out.println("tablesCount: " + tablesCount);
        
        int tableStartIndex = getDataStartIndex();
        System.out.println("Table Start Index: " + tableStartIndex);
        for(int i = 0; i < tablesCount; i++)
        {
            int platformID = reader.getUnsignedInt16();
            int encodingID = reader.getUnsignedInt16();
            //Where this sub-table is in relation to the end of the table
            int subTableOffset = (int)reader.getUnsignedInt32();
            System.out.println("Platform: " + platformID + ", Encoding: " + encodingID + ", Offset: " + subTableOffset);
            
            int index = reader.pointer;
            int subIndex = tableStartIndex + subTableOffset;
            reader.pointer = subIndex;
            int endOffset = subIndex;
            int format = reader.getUnsignedInt16();
            int length = reader.getUnsignedInt16();
            endOffset += length;
            int language = reader.getUnsignedInt16();
            reader.pointer = index;
            System.out.println("format: " + format);
            System.out.println("length: " + length);
            System.out.println("language: " + language);
            
            encoding = formats(format, length, subIndex, reader);
            
        }
    }
    private FormatEncoding format0(int format, int length, int index, ByteReader reader)
    {
        return null;
    }
    
    private FormatEncoding format4(int format, int length, int index, ByteReader reader)
    {
        reader.pointer = index;
        int endIndex = index + length;
        int segCount2X = reader.getUnsignedInt16();
        int searchRange = reader.getUnsignedInt16();
        int entrySelector = reader.getUnsignedInt16();
        
        int rangeShift = reader.getUnsignedInt16();
    
        System.out.println("segCount2X: " + segCount2X);
        System.out.println("searchRange: " + searchRange);
        System.out.println("entrySelector: " + entrySelector);
        System.out.println("rangeShift: " + rangeShift);
        
        
        List<Integer> endCodes = new ArrayList<>();
        List<Integer> startCodes = new ArrayList<>();
        List<Integer> idDeltas = new ArrayList<>();
        List<Integer> idRangeOffsets = new ArrayList<>();
    
    
        for(int i = 0; i < segCount2X / 2; i++)
        {
            int startCode = reader.getUnsignedInt16();
            if(startCode == 0xffff) break;
            int endCode = reader.getUnsignedInt16();
            int idDelta = reader.getUnsignedInt16();
            int idRangeOffset = reader.getUnsignedInt16();
    
            endCodes.add(endCode);
            startCodes.add(startCode);
            idDeltas.add(idDelta);
            idRangeOffsets.add(idRangeOffset);
        }
        
        while(true)
        {
            int startCode = reader.getUnsignedInt16();
            if(startCode == 0xffff) break;
            int endCode = reader.getUnsignedInt16();
            int idDelta = reader.getUnsignedInt16();
            int idRangeOffset = reader.getUnsignedInt16();
    
            endCodes.add(endCode);
            startCodes.add(startCode);
            idDeltas.add(idDelta);
            idRangeOffsets.add(idRangeOffset);
        }
    
        //System.out.println("endCodes" + endCodes);
        //System.out.println("startCodes" + startCodes);
        //System.out.println("idDeltas" + idDeltas);
        //System.out.println("idRangeOffsets" + idRangeOffsets);
        
        return new Format4Encoding(startCodes.stream().mapToInt(i -> i).toArray(), endCodes.stream()
                .mapToInt(i -> i).toArray(), idDeltas.stream().mapToInt(i -> i).toArray(), idRangeOffsets.stream()
                .mapToInt(i -> i).toArray());
        
        
    }
    private FormatEncoding formats(int format, int length, int indexAfterLanguage, ByteReader reader)
    {
        return switch(format)
        {
            case 0 -> format0(format, length, indexAfterLanguage, reader);
            case 4 -> format4(format, length, indexAfterLanguage, reader);
            default -> throw new RuntimeException("Format not implemented yet, call your doctor. " + format);
        };
    }
    
    public FormatEncoding getEncoding()
    {
        return encoding;
    }
    
    /*    private void getEncodingSchema(int platformID, int encodingID)
    {
        switch(platformID)
        {
            //Unicode
            case 0 ->
            {
                switch(encodingID)
                {
                    //unicode 1.0
                }
            }
        }
    }*/
    
}
