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
                    case "OS/2" -> fontData.os2 = new OS2(tableOffset, tableLength, tableChecksum, fontData, reader);
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
