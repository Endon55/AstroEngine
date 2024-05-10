package com.anthonycosenza.engine.loader.text.tables.types;

import com.anthonycosenza.engine.util.reader.ByteReader;
import com.anthonycosenza.engine.loader.text.FontData;

import java.util.ArrayList;
import java.util.List;

public class cffCharString
{
    private int index;
    private int type;
    private int length;
    private List<Object> charString;
    private GlyphPath path;
    
    public cffCharString(int length, FontData fontData, ByteReader reader)
    {
        //Type 2 is the default.
        this(2, length, fontData, reader);
    }
    
    public cffCharString(int type, int length, FontData fontData, ByteReader reader)
    {
        charString = new ArrayList<>();
        path = new GlyphPath(fontData);
        
        this.type = type;
        this.index = reader.pointer;
        this.length = length;
        
        
        switch(type)
        {
            case(1) -> type1(fontData, reader);
            case(2) -> type2(length, fontData, reader);
            default -> throw new RuntimeException("Only type 1 and 2 CharStrings are possible, check yourself: " + type);
        }
    }
    
    
    private void type2(int length, FontData fontData, ByteReader reader)
    {
        int start = reader.pointer;
        while(reader.pointer < start + length)
        {
            int b0 = reader.getUnsignedInt8();
    
    
            if((b0 >= 0 && b0 <= 27) || (b0 >= 29 && b0 <= 31))
            {
                //subroutine operator
                if(b0 == 10)
                {
                    cffSubroutine subroutine = fontData.cffLocalSubroutine.getIndex((int) getGlyphPath().popStack() + fontData.cffSubroutineBias);
                    int pointer = reader.pointer;
                    reader.pointer = subroutine.getStartIndex();
                    
                    /*
                     * Double checks that we're not stuck in an infinite loop or some shit.
                     */
                    if(Thread.currentThread().getStackTrace().length > 50)
                    {
                        throw new RuntimeException("Stack 2 big overflow my loins daddy");
                    }
                    type2(subroutine.getLength(), fontData, reader);
                    reader.pointer = pointer;
                }
                else if(b0 == 12)
                {
                    path.pushOperator(b0, reader.getUnsignedInt8(), fontData);
                }
                //Hintmask
                else if(b0 == 19)
                {
                    //Lets GlyphPath clear out all the remaining vStems
                    path.pushOperator(b0, 0, fontData);
                    
                    //Each hint is represented by 1 bit, we need a number of bits equal the number of hints rounded up to the nearest byte.
                    int bytes = (int) Math.ceil(path.getHintCount() / 8f);
                    
                    //Gotta add b0 which is always 19 to the mask.
                    int h0 = b0 + reader.getUnsignedIntX(bytes);
                    path.setHintMask(h0);
                }
                else path.pushOperator(b0, 0, fontData);
          
            }
            //Data is 1 byte long
            else if(b0 >= 32 && b0 <= 246)
            {
                path.pushValue(b0 - 139);
            }
            //2 bytes long
            else if(b0 >= 247 && b0 <= 250)
            {
                int b1 = reader.getUnsignedInt8();
                path.pushValue((b0 - 247) * 256 + b1 + 108);
            }
            //2 bytes long also
            else if(b0 >= 251 && b0 <= 254)
            {
                int b1 = reader.getUnsignedInt8();
                path.pushValue(-((b0 - 251) * 256) - b1 - 108);
            }
            //3 bytes long
            else if(b0 == 28)
            {
                int b1 = reader.getUnsignedInt8();
                int b2 = reader.getUnsignedInt8();
                path.pushValue((b1 << 8) | b2);
            }
            //5 bytes long
            else if(b0 == 255)
            {
                path.pushValue(reader.getInt32());
                throw new RuntimeException("255");
            }
        }
    }
    
    public GlyphPath getGlyphPath()
    {
        return path;
    }
    
    private void type1(FontData fontData, ByteReader reader)
    {
        throw new RuntimeException("I don't know how to handle type 1 CharStrings yet.");
    }
}
