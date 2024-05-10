package com.anthonycosenza.engine.loader.text;

import com.anthonycosenza.engine.loader.text.tables.CFF;
import com.anthonycosenza.engine.loader.text.tables.Name;
import com.anthonycosenza.engine.loader.text.tables.OS2;
import com.anthonycosenza.engine.loader.text.tables.Post;
import com.anthonycosenza.engine.loader.text.tables.encoding.FormatEncoding;
import com.anthonycosenza.engine.loader.text.tables.types.cffDict;
import com.anthonycosenza.engine.loader.text.tables.types.cffIndex;
import com.anthonycosenza.engine.loader.text.tables.GPOS;
import com.anthonycosenza.engine.loader.text.tables.Head;
import com.anthonycosenza.engine.loader.text.tables.Hhea;
import com.anthonycosenza.engine.loader.text.tables.Hmtx;
import com.anthonycosenza.engine.loader.text.tables.Kern;
import com.anthonycosenza.engine.loader.text.tables.Maxp;
import com.anthonycosenza.engine.loader.text.tables.types.cffCharString;
import com.anthonycosenza.engine.loader.text.tables.types.cffSubroutine;
import com.anthonycosenza.engine.util.reader.ByteReader;
import com.anthonycosenza.engine.util.FileIO;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class FontData
{
    public static final int TABLE_RECORD_BYTES = 16;
    public static final int BYTES_IN_TAG = 4;
    
    public CFF cff;
    public cffDict cffTopDict;
    public cffIndex<String> cffNameIndex;
    public cffIndex<String> cffStringIndex;
    public cffDict cffPrivateDict;
    public cffIndex<cffCharString> cffCharStringIndex;
    public cffIndex<cffSubroutine> cffLocalSubroutine;
    public int cffSubroutineBias;
    public GPOS gpos;
    
    /*
     * OS2 values
     * https://learn.microsoft.com/en-us/typography/opentype/spec/os2
     *
     */
    public int os2Version;
    public int xAvgCharWidth;
    public int usWeightClass;
    public int usWidthClass;
    public int fsType;
    public int ySubscriptXSize;
    public int ySubscriptYSize;
    public int ySubscriptXOffset;
    public int ySubscriptYOffset;
    public int ySuperscriptXSize;
    public int ySuperscriptYSize;
    public int ySuperscriptXOffset;
    public int ySuperscriptYOffset;
    public int yStrikeoutSize;
    public int yStrikeoutPosition;
    public int sFamilyClass;
    
    //10 Panose bytes - https://learn.microsoft.com/en-us/typography/opentype/spec/os2#panose
    public short bFamilyType;
    public short bSerifStyle;
    public short bWeight;
    public short bProportion;
    public short bContrast;
    public short bStrokeVariation;
    public short bArmStyle;
    public short bLetterForm;
    public short bMidline;
    public short bXHeight;
    
    public long ulUnicodeRange1;
    public long ulUnicodeRange2;
    public long ulUnicodeRange3;
    public long ulUnicodeRange4;
    public String achVendID;
    public int fsSelection;
    public int usFirstCharIndex;
    public int usLastCharIndex;
    public int sTypoAscender;
    public int sTypoDescender;
    public int sTypoLineGap;
    public int usWinAscent;
    public int usWinDescent;
    public long ulCodePageRange1 = -1;
    public long ulCodePageRange2 = -1;
    public int sxHeight = -1;
    public int sCapHeight = -1;
    public int usDefaultChar = -1;
    public int usBreakChar = -1;
    public int usMaxContext = -1;
    public int usLowerOpticalPointSize = -1;
    public int usUpperOpticalPointSize = -1;
    
    
    public OS2 os2;
    public Head head;
    public Hhea hhea;
    public Hmtx hmtx;
    public Kern kern;
    public Maxp maxp;
    public Name name;
    public Post post;
    
    private int tableCount = 0;
    
    private FormatEncoding encoding;
    
    private FontData() { }
    
    
    public static FontData decodeFont(String filePath)
    {
        FontData fontData = new FontData();
        byte[] byteArr = FileIO.getFileBytes(filePath);
        int bytesLength = byteArr.length;
        ByteBuffer buffer = ByteBuffer.wrap(byteArr);
        buffer.order(ByteOrder.BIG_ENDIAN);
        ByteReader reader = new ByteReader(buffer);
        
        String tag1 = reader.getString(BYTES_IN_TAG);
        if(tag1.equals("OTTO"))
        {
            int tableCount = reader.getUnsignedInt16();
            fontData.tableCount = tableCount;
            int searchRange = reader.getUnsignedInt16();
            //System.out.println("Search Range: " + searchRange);
            int entrySelector = reader.getUnsignedInt16();
            //System.out.println("Entry Selector: " + entrySelector);
            int rangeShift = reader.getUnsignedInt16();
            //System.out.println("Range Shift: " + rangeShift);
    
            for(int i = 0; i < tableCount; i++)
            {
                String tag = reader.getString( BYTES_IN_TAG);
                //The CFF tag's 4th character is a space, gotta strip it off
                tag = tag.strip();
                long tableChecksum = reader.getUnsignedInt32();
                int tableOffset = (int)reader.getUnsignedInt32();
                int tableLength = (int) reader.getUnsignedInt32();
                
                int index = reader.pointer;
                reader.pointer = tableOffset;

                

                
                switch(tag)
                {
                    case "CFF" -> fontData.cff = new CFF(tableOffset, tableLength, tableChecksum, fontData, reader);
                    case "GPOS" -> fontData.gpos = new GPOS(tableOffset, tableLength, tableChecksum, fontData, reader);
                    case "OS/2" -> OS2(fontData, reader);
                    /*case "cmap" ->
                    {
                        Cmap cmap = new Cmap(tableOffset, reader);
                        encoding = cmap.getEncoding();
                        yield cmap;
                    }*/
                    case "head" -> fontData.head = new Head(tableOffset, tableLength, tableChecksum, fontData, reader);
                    case "hhea" -> fontData.hhea = new Hhea(tableOffset, tableLength, tableChecksum, fontData, reader);
                    case "hmtx" -> fontData.hmtx = new Hmtx(tableOffset, tableLength, tableChecksum, fontData, reader);
                    case "kern" -> fontData.kern = new Kern(tableOffset, tableLength, tableChecksum, fontData, reader);
                    case "maxp" -> fontData.maxp = new Maxp(tableOffset, tableLength, tableChecksum, fontData, reader);
                    case "name" -> fontData.name = new Name(tableOffset, tableLength, tableChecksum, fontData, reader);
                    case "post" -> fontData.post = new Post(tableOffset, tableLength, tableChecksum, fontData, reader);
                    
                };
    
/*                if(table != null)
                {
                    tables.put(table.getTag(), table);
                }
                else System.out.println("Couldn't find matching table for: " + tag);*/
                reader.pointer = index;
            }
            
        }
        else throw new RuntimeException("No valid font header data");
    
        return fontData;
    }
    
    private static void OS2(FontData fontData, ByteReader reader)
    {
    
        fontData.os2Version = reader.getUnsignedInt16();
        
        //Version 5 also has usLowerOpticalPointSize, and usUpperOpticalPointSize
        //Version 2, 3, and 4 are identical and also have sxHeight, sCapHeight, usDefaultChar, usBreakChar, and usMaxContext
        //Version 1 also has ulCodePageRange1 and ulCodePageRange2
        //Version 0 is all the default values;
        
        //Version 0 values
        fontData.xAvgCharWidth = reader.getInt16();
        fontData.usWeightClass = reader.getUnsignedInt16();
        fontData.usWidthClass = reader.getUnsignedInt16();
        fontData.fsType = reader.getUnsignedInt16();
        
        fontData.ySubscriptXSize = reader.getInt16();
        fontData.ySubscriptYSize = reader.getInt16();
        fontData.ySubscriptXOffset = reader.getInt16();
        fontData.ySubscriptYOffset = reader.getInt16();
        
        fontData.ySuperscriptXSize = reader.getInt16();
        fontData.ySuperscriptYSize = reader.getInt16();
        fontData.ySuperscriptXOffset = reader.getInt16();
        fontData.ySuperscriptYOffset = reader.getInt16();
        
        fontData.yStrikeoutSize = reader.getInt16();
        fontData.yStrikeoutPosition = reader.getInt16();
        fontData.sFamilyClass = reader.getInt16();
        
        //10 Panose Bytes
        fontData.bFamilyType = (short) reader.getUnsignedInt8();
        fontData.bSerifStyle = (short) reader.getUnsignedInt8();
        fontData.bWeight = (short) reader.getUnsignedInt8();
        fontData.bProportion = (short) reader.getUnsignedInt8();
        fontData.bContrast = (short) reader.getUnsignedInt8();
        fontData.bStrokeVariation = (short) reader.getUnsignedInt8();
        fontData.bArmStyle = (short) reader.getUnsignedInt8();
        fontData.bLetterForm = (short) reader.getUnsignedInt8();
        fontData.bMidline = (short) reader.getUnsignedInt8();
        fontData.bXHeight = (short) reader.getUnsignedInt8();
    
        fontData.ulUnicodeRange1 = reader.getUnsignedInt32();
        fontData.ulUnicodeRange2 = reader.getUnsignedInt32();
        fontData.ulUnicodeRange3 = reader.getUnsignedInt32();
        fontData.ulUnicodeRange4 = reader.getUnsignedInt32();
        
        fontData.achVendID = reader.getString(4);
        fontData.fsSelection = reader.getUnsignedInt16();
        fontData.usFirstCharIndex = reader.getUnsignedInt16();
        fontData.usLastCharIndex = reader.getUnsignedInt16();
        
        fontData.sTypoAscender = reader.getInt16();
        fontData.sTypoDescender = reader.getInt16();
        fontData.sTypoLineGap = reader.getInt16();
        
        fontData.usWinAscent = reader.getUnsignedInt16();
        fontData.usWinDescent = reader.getUnsignedInt16();
        //Version 1 additional values
        if(fontData.os2Version > 0)
        {
            fontData.ulCodePageRange1 = reader.getUnsignedInt32();
            fontData.ulCodePageRange2 = reader.getUnsignedInt32();
        }
        //Version 2/3/4 additional values
        if(fontData.os2Version > 1)
        {
            fontData.sxHeight = reader.getInt16();
            fontData.sCapHeight = reader.getInt16();
            fontData.usDefaultChar = reader.getUnsignedInt16();
            fontData.usBreakChar = reader.getUnsignedInt16();
            fontData.usMaxContext = reader.getUnsignedInt16();
    
            System.out.println("sxHeight: " + fontData.sxHeight);
            System.out.println("sCapHeight: " + fontData.sCapHeight);
            System.out.println("usDefaultChar: " + fontData.usDefaultChar);
            System.out.println("usBreakChar: " + fontData.usBreakChar);
            System.out.println("usMaxContext: " + fontData.usMaxContext);
        }
        //Version 5 additional values
        if(fontData.os2Version == 5)
        {
            fontData.usLowerOpticalPointSize = reader.getUnsignedInt16();
            fontData.usUpperOpticalPointSize = reader.getUnsignedInt16();
        }
    }
    
    
    public CFF getCff()
    {
        return cff;
    }
    
    public GPOS getGpos()
    {
        return gpos;
    }
    
    public OS2 getOs2()
    {
        return os2;
    }
    
    public Head getHead()
    {
        return head;
    }
    
    public Hhea getHhea()
    {
        return hhea;
    }
    
    public Hmtx getHmtx()
    {
        return hmtx;
    }
    
    public Kern getKern()
    {
        return kern;
    }
    
    public Maxp getMaxp()
    {
        return maxp;
    }
    
    public Name getName()
    {
        return name;
    }
    
    public Post getPost()
    {
        return post;
    }
}
