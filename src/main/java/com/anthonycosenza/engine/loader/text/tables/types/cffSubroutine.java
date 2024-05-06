package com.anthonycosenza.engine.loader.text.tables.types;


import com.anthonycosenza.engine.util.reader.ByteReader;

public class cffSubroutine
{
    private int startIndex;
    private int length;
    
    public cffSubroutine(int length, int startIndex)
    {
        this.startIndex = startIndex;
        this.length = length;
    }
    
    public cffSubroutine(int length, int startIndex, ByteReader reader)
    {
        this.startIndex = startIndex;
        this.length = length;
        String validation = reader.peekHex(startIndex + length - 1, 1);
        /*
            A subroutine must end with a return or endchar command.
            One rare occasions a substring may end with another call to a subroutine.
            
            0xa(10) = Subroutine
            0xb(10) = Return
            0xe(10) = Endchar
         */
        if(!validation.equals("a") && !validation.equals("b") && !validation.equals("e"))
        {
            throw new RuntimeException("Subroutine is invalid. Data must end with a 0xa or 0xb or 0xe: Start Index: " + startIndex + ", Length: " + length + ", Data: " + reader.peekHex(startIndex, length));
        }
    }
    
    public static int getBias(int numberOfSubroutines)
    {
        //Charstring type == 1 then bias is 0
        if(numberOfSubroutines < 1240)
        {
            return 107;
        }
        else if(numberOfSubroutines < 33900)
        {
            return 1131;
        }
        else return 32768;
    }
    
    public int getStartIndex()
    {
        return startIndex;
    }
    
    public int getLength()
    {
        return length;
    }
}
