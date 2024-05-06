package com.anthonycosenza.engine.loader.text.tables.types;

import com.anthonycosenza.engine.loader.text.ByteReader;
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
        System.out.println("CharString Length: " + length);
        
        
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
        System.out.println("charstring Index: " + start);
        while(reader.pointer < start + length)
        {
            /*if(reader.pointer - start > length)
            {
                throw new RuntimeException("CharString read too much data.: " + charString);
            }
            else if(reader.pointer - start == length)
            {
                //System.out.println("Charstring: " + charString);
                return;
            }*/
            
            int b0 = reader.getUnsignedInt8();
            System.out.println("b0: " + b0);
    
    
            if((b0 >= 0 && b0 <= 27) || (b0 >= 29 && b0 <= 31))
            {
                //subroutine operator
                if(b0 == 10)
                {
                    System.out.println("Calling Subroutine");
                    cffSubroutine subroutine = fontData.cffLocalSubroutine.getIndex((int) getGlyphPath().popStack() + fontData.cffSubroutineBias);
                    int pointer = reader.pointer;
                    reader.pointer = subroutine.getStartIndex();
                    
                    //System.out.println("Subroutine Length: " + subroutine.getLength());
                    //System.out.println("Subroutine reader.pointer: " + reader.pointer);
                    //System.out.println("Subroutine: " + reader.peekHex(subroutine.getStartIndex(), subroutine.getLength()));
                    
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
                    //System.out.println("PreHints: " + path.getHintCount());
                    //Lets GlyphPath clear out all the remaining vStems
                    path.pushOperator(b0, 0, fontData);
                    
                    //Each hint is represented by 1 bit, we need a number of bits equal the number of hints rounded up to the nearest byte.
                    int bytes = (int) Math.ceil(path.getHintCount() / 8f);
                    //System.out.println("Hints: " + path.getHintCount() + ", Bytes Needed: " + bytes);
                    
                    //Gotta add b0 which is always 19 to the mask.
                    int h0 = b0 + reader.getUnsignedIntX(bytes);
                    //System.out.println("Hint Mask: " + h0 + ", Pointer: " + reader.pointer);
                    path.setHintMask(h0);
                    //throw new RuntimeException("Hint Mask");
                }
                else path.pushOperator(b0, 0, fontData);
                
           /*
                String operatorStr = switch(b0)
                        {
                            case 0, 2, 9, 13, 15, 16, 17 -> throw new RuntimeException("Reserved 1-byte CharString operator: " + b0);
                            case 1 -> "hstem";
                            case 3 -> "vstem";
                            case 4 -> "vmoveto";
                            case 5 -> "rlineto";
                            case 6 -> "hlineto";
                            case 7 -> "vlineto";
                            case 8 -> "rrcurveto";
                            case 10 -> "callsubr";
                            case 11 -> "return";
                            case 12 ->
                            {
                                int operator2 = reader.getUnsignedInt8();
                                yield switch(operator2)
                                        {
                                            case 0 -> throw new RuntimeException("This operators is deprecated apparently: " + operator2);
                                            case 1, 2, 6, 7, 8, 13, 16, 17, 19, 25, 31, 32, 33 -> throw new RuntimeException("Reserved 2-byte CharString operator: " + operator2);
                                            case 3 -> "and";
                                            case 4 -> "or";
                                            case 5 -> "not";
                                            case 9 -> "abs";
                                            case 10 -> "add";
                                            case 11 -> "sub";
                                            case 12 -> "div";
                                            case 14 -> "neg";
                                            case 15 -> "eq";
                                            case 18 -> "drop";
                                            case 20 -> "put";
                                            case 21 -> "get";
                                            case 22 -> "ifelse";
                                            case 23 -> "random";
                                            case 24 -> "mul";
                                            case 26 -> "sqrt";
                                            case 27 -> "dup";
                                            case 28 -> "exch";
                                            case 29 -> "index";
                                            case 30 -> "roll";
                                            case 34 -> "hflex";
                                            case 35 -> "flex";
                                            case 36 -> "hflex1";
                                            case 37 -> "flex1";
                                            default ->
                                            {
                                                throw new RuntimeException("Anything this high is reserved: " + operator2);
                                            }
                                        };
                            }
                            case 14 -> "endchar";
                            case 18 -> "hstemhm";
                            case 19 -> "hintmask";
                            case 20 -> "cntrmask";
                            case 21 -> "rmoveto";
                            case 22 -> "hmoveto";
                            case 23 -> "vstemhm";
                            case 24 -> "rcurveline";
                            case 25 -> "rlinecurve";
                            case 26 -> "vvcurveto";
                            case 27 -> "hhcurveto";
                            case 29 -> "callgsubr";
                            case 30 -> "vhcurveto";
                            case 31 -> "hvcurveto";
                            default -> throw new RuntimeException("I don't think anything should be here...: " + b0);
                        };
                charString.add(operatorStr);*/
        
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
                System.out.println("255");
                path.pushValue(reader.getInt32());
                throw new RuntimeException("255");
                //valueF = reader.getFloat32();
            }
            
            /*if(value != -1)
            {
                charString.add(value);
            }
            else charString.add(valueF);*/
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
