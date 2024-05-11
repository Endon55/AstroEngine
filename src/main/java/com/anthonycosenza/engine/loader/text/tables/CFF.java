package com.anthonycosenza.engine.loader.text.tables;

import com.anthonycosenza.engine.loader.text.tables.types.Glyph;
import com.anthonycosenza.engine.util.reader.ByteReader;
import com.anthonycosenza.engine.loader.text.FontData;
import com.anthonycosenza.engine.loader.text.tables.types.cffCharString;
import com.anthonycosenza.engine.loader.text.tables.types.cffDict;
import com.anthonycosenza.engine.loader.text.tables.types.cffIndex;
import com.anthonycosenza.engine.loader.text.tables.types.cffSubroutine;

import java.util.List;
import java.util.stream.Collectors;

public class CFF extends OpenTypeTable
{
    //https://adobe-type-tools.github.io/font-tech-notes/pdfs/5176.CFF.pdf
    
    private int majorVersion;
    private int minorVersion;
    private int headerSize;
    private int offsetByteCount;
    
    public CFF(int recordIndex, int recordLength, long checksum, FontData fontData, ByteReader reader)
    {
        super("CFF", recordIndex, recordLength, checksum, fontData, reader);
    }
    
    @Override
    protected void decode(FontData fontData, ByteReader reader)
    {
        //Header
        majorVersion = reader.getUnsignedInt8();
        minorVersion = reader.getUnsignedInt8();
        headerSize = reader.getUnsignedInt8();
        /*
        This value tells you the maximum number of bytes that offsets will use in the table.
        In this section it's like a warning, expect to work with up to this many bytes while parsing.
        In other sections it tells you specifically how many bytes you can expect to use before you need to read the data.
        Which kind of makes this value a bit pointless so long as you're reading the other one correctly.
        */
        offsetByteCount = reader.getUnsignedInt8();
    
        //Name INDEX
        fontData.cffNameIndex = new cffIndex<>(fontData, reader, String.class);
    
        // Top DICT INDEX
        fontData.cffTopDict = new cffDict(fontData, reader);
        
        //String INDEX
        fontData.cffNameIndex = new cffIndex<>(fontData, reader, String.class);
    
        //Global Subroutine Index
        int glSbrCount = reader.getUnsignedInt16();
        if(glSbrCount > 0) throw new RuntimeException("Learn to handle global subroutines boyo.");
    
        //Private
        List<Number> privateVals = fontData.cffTopDict.getValue("Private");
        reader.pointer = getRecordIndex() + (int) privateVals.get(1);
        int privateIndex = reader.pointer;
        fontData.cffPrivateDict = new cffDict((int) privateVals.get(0), true, fontData, reader);
        
        //Local Subroutines
        privateVals = fontData.cffPrivateDict.getValue("Subrs");
        if(privateVals != null)
        {
            reader.pointer = privateIndex + (int) privateVals.get(0);
            fontData.cffLocalSubroutine = new cffIndex<>(fontData, reader, cffSubroutine.class);
            fontData.cffSubroutineBias = cffSubroutine.getBias(fontData.cffLocalSubroutine.getData().size());
        }
        
        //CharStrings
        reader.pointer = getRecordIndex() + (int) fontData.cffTopDict.getValue("CharStrings").get(0);
        fontData.cffCharStringIndex = new cffIndex<>(fontData, reader, cffCharString.class);
        //Extract the glyphs from the charstrings to have a more easily accessible list.
        fontData.glyphs = fontData.cffCharStringIndex.getData().stream().map(cffCharString::getGlyph).collect(Collectors.toList());

        
    }
}
