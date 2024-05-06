package com.anthonycosenza.engine.util.reader;

public class BitReader
{
    private byte[] data;
    public int pointer;
    boolean rightToLeft;
    
    public BitReader(byte[] data, ByteType byteType)
    {
        this.data = data;
        pointer = 0;
        rightToLeft = byteType.equals(ByteType.RIGHT_TO_LEFT);
    }
    
    
    public boolean getBit()
    {
        return getBit(pointer++);
    }
    
    public int getInt(int bits)
    {
        if(bits == 0) return 0;
        
        int val = getInt(pointer, bits);
        pointer += bits;
        return val;
    }
    
    public int getInt(int start, int bits)
    {
        if(bits > 32) throw new RuntimeException("Can't be havin no ints bigger than 32");
        int val = 0;
        if(rightToLeft)
        {
            for(int i = 0; i < bits; i++)
            {
                int byteIndex = ((i + start) / 8);
                int bitIndex = (i + start) % 8;
                if(byteIndex > data.length)
                    throw new ArrayIndexOutOfBoundsException("Bit : " + i + " is out of bounds for range(0" + (data.length * 8) + ")");
                
                val |= ((data[byteIndex] >> bitIndex) & 1) << i;
            }
        }
        else //Left to Right
        {
            for(int i = 0; i < bits; i++)
            {
                int byteIndex = ((i + start) / 8);
                int bitIndex = (i + start) % 8;
                if(byteIndex > data.length)
                    throw new ArrayIndexOutOfBoundsException("Bit : " + i + " is out of bounds for range(0" + (data.length * 8) + ")");
                bitIndex = 8 - bitIndex - 1;//reverse the order
        
                val |= ((data[byteIndex] >> bitIndex) & 1) << (bits - i - 1);
            }
        }
        return val;
    }
    
    public boolean getBit(int i)
    {
        int byteIndex = (i / 8);
        int bitIndex = i % 8;
        if(byteIndex > data.length)
            throw new ArrayIndexOutOfBoundsException("Bit : " + i + " is out of bounds for range(0" + (data.length * 8) + ")");
        bitIndex = (rightToLeft ? bitIndex : (8 - bitIndex - 1));//reverse the order
        return (((data[byteIndex] >> bitIndex) & 1) & 0xff) == 1;
    }
    
    public String getBitS(int from, int to)
    {
        StringBuilder builder = new StringBuilder();
        for(int i = from; i <= to; i++)
        {
            builder.append(getBitS(i));
        }
        return builder.toString();
    }
    public String getBitS(int i)
    {
        return getBit(i) ? "1" : "0";
    }
    
    
    
    
    /*
    public boolean getBit()
    {
        return getBit(pointer++);
    }
    
    public int getInt(int bits)
    {
        int val = getInt(pointer, bits);
        pointer += bits;
        return val;
    }
    
    public int getInt(int start, int bits)
    {
        StringBuilder builder = new StringBuilder();
        if(bits > 32) throw new RuntimeException("Can't be havin no ints bigger than 32");
        int val = 0;
        for(int i = 0; i < bits; i++)
        {
            builder.append(getBitS(i + start));
            int byteIndex = ((i + start) / 8);
            int bitIndex = (i + start) % 8;
            if(byteIndex > data.length) throw new ArrayIndexOutOfBoundsException("Bit : " + i + " is out of bounds for range(0" + (data.length * 8) + ")");
            bitIndex = 8 - bitIndex - 1;//reverse the order
            
            val |= ((data[byteIndex] >> bitIndex) & 1) << (bits - i -1);
        }
        System.out.println("Bits: " + builder.toString());
        return val;
    }
    
    public boolean getBit(int i)
    {
        int byteIndex = (i / 8);
        int bitIndex = i % 8;
        if(byteIndex > data.length) throw new ArrayIndexOutOfBoundsException("Bit : " + i + " is out of bounds for range(0" + (data.length * 8) + ")");
        System.out.println("I: " + i + ", Byte: " + byteIndex + ", Bit: " + bitIndex);
        bitIndex = 8 - bitIndex - 1;//reverse the order
        return (((data[byteIndex] >> bitIndex) & 1) & 0xff) == 1;
    }
    
    public String getBitS(int i)
    {
        return getBit(i) ? "1" : "0";
    }
    
    */
    
    public enum ByteType
    {
        RIGHT_TO_LEFT,
        LEFT_TO_RIGHT;
    }
}
