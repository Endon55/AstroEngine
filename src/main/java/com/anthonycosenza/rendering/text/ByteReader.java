package com.anthonycosenza.rendering.text;

import java.nio.ByteBuffer;

public class ByteReader
{
    private ByteBuffer bytes;
    private int length;
    public int pointer;
    
    public ByteReader(byte[] bytes)
    {
        this.bytes = ByteBuffer.wrap(bytes);
        pointer = 0;
    }
    
    public ByteReader(ByteBuffer buffer)
    {
        this.bytes = buffer;
        this.length = buffer.limit();
    }
    
    public float getFloat32()
    {
        float value = bytes.getFloat(pointer);
        pointer += 4;
        return value;
    }
    
    public int getInt8()
    {
        return getSignedByteI();
    }
    
    public int getUnsignedInt8()
    {
        return getUnsignedByteI();
    }
    public int getUnsignedInt16()
    {
        return (getUnsignedByteI() << 8) + getUnsignedByteI();
    }
    
    public int getUnsignedInt24()
    {
        return (getUnsignedByteI() << 16) + (getUnsignedByteI() << 8)
                + getUnsignedByteI();
    }
    
    public long getUnsignedInt32()
    {
        return (getUnsignedByteL() << 24) + (getUnsignedByteL() << 16)
                + (getUnsignedByteL() << 8) + getUnsignedByteL();
    }
    
    public int getInt32()
    {
        int value = bytes.getInt(pointer);
        pointer += 4;
        return value;
    }
    
    
    public String getByteString()
    {
        byte b = bytes.get(pointer);
        pointer++;
        return Integer.toBinaryString(b & 0xff);
    }
    
    private  String getByteString(int index)
    {
        byte b = bytes.get(index);
        return Integer.toBinaryString(b & 0xff);
    }
    
    private String getHexString(int index)
    {
        byte b = bytes.get(index);
        return Integer.toHexString(b & 0xff);
    }
    
    
    public String getString(int numBytes)
    {
        byte[] byteArr = new byte[numBytes];
        bytes.get(pointer, byteArr);
        pointer += numBytes;
        return new String(byteArr);
    }
    
    public int[] getHalfBytes()
    {
        int[] halfBytes = new int[2];
        byte bite = bytes.get(pointer);
        halfBytes[0] = (bite & 240) >> 4;
        halfBytes[1] = (bite & 15);
        pointer += 1;
        return  halfBytes;
    }
    
    public int getUnsignedByteI()
    {
        int value = bytes.get(pointer) & 0xff;
        pointer++;
        return value;
    }
    
    public int getSignedByteI()
    {
        int value = bytes.get(pointer);
        pointer++;
        return value;
    }
    
    public long getUnsignedByteL()
    {
        long value = bytes.get(pointer) & 0xff;
        pointer++;
        return value;
    }
    
    public String peekBinary(int startIndex, int byteCount)
    {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < byteCount; i++)
        {
            builder.append(getByteString(startIndex + i));
            builder.append(" ");
        }
        return builder.toString();
    }
    
    public String peekHex(int startIndex, int byteCount)
    {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < byteCount; i++)
        {
            builder.append(getHexString(startIndex + i));
            if(i + 1 != byteCount)
            {
                builder.append(" ");
            }
        }
        return builder.toString();
    }
    
    public int getUnsignedIntX(int byteCount)
    {
        if(byteCount == 1)
        {
            return getUnsignedInt8();
        }
        else if(byteCount == 2)
        {
            return getUnsignedInt16();
        }
        else if(byteCount == 3)
        {
            return getUnsignedInt24();
        }
/*        else if(offsetSize == 4)
        {
            return (int)reader.getUnsignedInt32();
        }*/
        throw new RuntimeException("Can't handle byte lengths of size: " + byteCount);
    }
    
}
