package com.anthonycosenza.engine.loader.text.tables;

import com.anthonycosenza.engine.util.reader.ByteReader;
import com.anthonycosenza.engine.loader.text.FontData;
import com.anthonycosenza.engine.loader.text.tables.types.cffCharString;
import com.anthonycosenza.engine.loader.text.tables.types.cffDict;
import com.anthonycosenza.engine.loader.text.tables.types.cffIndex;
import com.anthonycosenza.engine.loader.text.tables.types.cffSubroutine;

import java.util.List;

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
        //globalSubroutineIndex = new cffIndex<>(reader, cffGlobalSubroutine.class);
        int glSbrCount = reader.getUnsignedInt16();
        if(glSbrCount > 0) throw new RuntimeException("Learn to handle global subroutines boyo.");
    
        
        //Private
        List<Number> privateVals = fontData.cffTopDict.getValue("Private");
        reader.pointer = getRecordIndex() + (int) privateVals.get(1);
        int privateIndex = reader.pointer;
        System.out.println("Private Index: " + reader.pointer);
        fontData.cffPrivateDict = new cffDict((int) privateVals.get(0), true, fontData, reader);
        System.out.println(fontData.cffPrivateDict.getTable());
        
        //Local Subroutines
        privateVals = fontData.cffPrivateDict.getValue("Subrs");
        if(privateVals != null)
        {
            reader.pointer = privateIndex + (int) privateVals.get(0);
            System.out.println("Private Index: " + reader.pointer);
            fontData.cffLocalSubroutine = new cffIndex<>(fontData, reader, cffSubroutine.class);
            fontData.cffSubroutineBias = cffSubroutine.getBias(fontData.cffLocalSubroutine.getData().size());
            /*System.out.println("Subroutines: ");
            font.cffLocalSubroutine.getData().forEach(cffSubroutine ->
                    {
                        System.out.println(reader.peekHex(cffSubroutine.getStartIndex(), cffSubroutine.getLength()));
                    }
            );*/
        }
        
        //CharStrings
        reader.pointer = getRecordIndex() + (int) fontData.cffTopDict.getValue("CharStrings").get(0);
        //System.out.println("CharString Index: " + reader.pointer);
        fontData.cffCharStringIndex = new cffIndex<>(fontData, reader, cffCharString.class);
    
    
        //reader.pointer = getRecordIndex() + (int) topDict.getValue("charset").get(0);
        //cffCharset charset = new cffCharset(reader);
        
        //Encodings
        /*List<Number> topDictEncoding = topDict.getValue("encoding");
        if(topDictEncoding == null)
        {
            //No encoding found means it's a CID font. I don't know what that means yet.
        }
        cffEncoding encoding = new cffEncoding(reader);*/
        /*int encoding = reader.getUnsignedInt8();
        if(encoding > 0) throw new RuntimeException("Learn to handle encodings boyo.(Also double check encodings and charsets are in the right order.)");
        */
        
    }
}
