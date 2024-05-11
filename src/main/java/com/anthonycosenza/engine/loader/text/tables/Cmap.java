package com.anthonycosenza.engine.loader.text.tables;

import com.anthonycosenza.engine.util.reader.ByteReader;
import com.anthonycosenza.engine.loader.text.FontData;
import com.anthonycosenza.engine.loader.text.tables.encoding.Format4Encoding;
import com.anthonycosenza.engine.loader.text.tables.encoding.FormatEncoding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cmap extends OpenTypeTable
{
    //https://learn.microsoft.com/en-us/typography/opentype/spec/cmap#unicode-platform-platform-id--0
    private int version;
    private int subTablesCount;
    private Map<Integer, FormatEncoding> encodings;
    
    public Cmap(int index, int recordLength, long checksum, FontData fontData, ByteReader reader)
    {
        super("cmap", index, recordLength, checksum, fontData, reader);
    }
    /*
     * https://learn.microsoft.com/en-us/typography/opentype/spec/cmap
     */
    @Override
    protected void decode(FontData fontData, ByteReader reader)
    {
        encodings = new HashMap<>();
        /*
         * CMAP Header
         * The header contains a version, and a number of EncodingRecords that each specify offset, encoding, and platform.
         */
        int version = reader.getUnsignedInt16();
        int tablesCount = reader.getUnsignedInt16();
        
        //Offsets start from here.
        int tableStartIndex = getDataStartIndex();
        for(int i = 0; i < tablesCount; i++)
        {
            /*
             * Parse the EncodingRecord header
             */
            int platformID = reader.getUnsignedInt16();
            int encodingID = reader.getUnsignedInt16();
            int subTableOffset = (int)reader.getUnsignedInt32();
            
            
            
            /*
             * Jump to the EncodingRecord and parse.
             */
            int subIndex = tableStartIndex + subTableOffset;
            int recall = reader.pointer;
            reader.pointer = subIndex;
            
            int format = reader.getUnsignedInt16();
            
            //Duplicate information can be held here.
            if(!encodings.containsKey(format))
            {
                encodings.put(format, formats(format, subIndex, reader));
            }
            reader.pointer = recall;
        }
    }
    
    public int getGlyphId(int charCode)
    {
        return encodings.get(4).getGlyphID(charCode);
    }
    
    private FormatEncoding format0(int index, ByteReader reader)
    {
        return null;
    }
    
    private FormatEncoding format4(int index, ByteReader reader)
    {
        int startIndex = reader.pointer;
        int length = reader.getUnsignedInt16();
        int language = reader.getUnsignedInt16();
        if(language != 0) throw new RuntimeException("Bad language guv");
        
        //reader.pointer = index;
        int endIndex = index + length;
        int segCount2X = reader.getUnsignedInt16();
        int segCount = segCount2X / 2;
        int searchRange = reader.getUnsignedInt16();
        int entrySelector = reader.getUnsignedInt16();
        
        int rangeShift = reader.getUnsignedInt16();
        
        
        List<Integer> endCodes = new ArrayList<>();
        List<Integer> startCodes = new ArrayList<>();
        List<Integer> idDeltas = new ArrayList<>();
        List<Integer> idRangeOffsets = new ArrayList<>();
    
    
        for(int i = 0; i < segCount; i++)
        {
            endCodes.add(reader.getUnsignedInt16());
        }
        int reservedPadding = reader.getUnsignedInt16();
        for(int i = 0; i < segCount; i++)
        {
            startCodes.add(reader.getUnsignedInt16());
        }
        for(int i = 0; i < segCount; i++)
        {
            idDeltas.add(reader.getUnsignedInt16());
        }
        for(int i = 0; i < segCount; i++)
        {
            idRangeOffsets.add(reader.getUnsignedInt16());
        }
        
        List<Integer> glyphIds = new ArrayList<>();
        
        while(reader.pointer < startIndex + length)
        {
            int glyphID = reader.getUnsignedInt16();
            glyphIds.add(glyphID);
        }
        
        return new Format4Encoding(segCount, glyphIds.stream().mapToInt(i -> i).toArray(),
                startCodes.stream().mapToInt(i -> i).toArray(), endCodes.stream()
                .mapToInt(i -> i).toArray(), idDeltas.stream().mapToInt(i -> i).toArray(), idRangeOffsets.stream()
                .mapToInt(i -> i).toArray());
        
        
    }
    private FormatEncoding formats(int format, int indexAfterLanguage, ByteReader reader)
    {
        return switch(format)
        {
            case 0 -> format0(indexAfterLanguage, reader);
            case 4 -> format4(indexAfterLanguage, reader);
            default -> throw new RuntimeException("Format not implemented yet, call your doctor. " + format);
        };
    }
    
}
