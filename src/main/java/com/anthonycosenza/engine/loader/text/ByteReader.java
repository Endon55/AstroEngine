package com.anthonycosenza.engine.loader.text;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteReader
{
    private ByteBuffer buffer;
    private int length;
    public int pointer;
    
    
    public ByteReader(ByteBuffer buffer)
    {
        this.buffer = buffer;
        this.length = this.buffer.limit();
        pointer = 0;
    }
    
    public void setEndian(boolean bigEndian)
    {
        this.buffer.order(bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
    }
    
    public float getFloat32()
    {
        float value = buffer.getFloat(pointer);
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
    
    public long getUnsignedLong64()
    {
        return(getUnsignedByteL() << 56) + (getUnsignedByteL() << 48)
                + (getUnsignedByteL() << 40) + (getUnsignedByteL() << 32)
                + (getUnsignedByteL() << 24) + (getUnsignedByteL() << 16)
                + (getUnsignedByteL() << 8) + getUnsignedByteL();
    }
    
    public int getInt32()
    {
        int value = buffer.getInt(pointer);
        pointer += 4;
        return value;
    }
    
    
    public String getByteString()
    {
        byte b = buffer.get(pointer);
        pointer++;
        return Integer.toBinaryString(b & 0xff);
    }
    
    private  String getByteString(int index)
    {
        byte b = buffer.get(index);
        return Integer.toBinaryString(b & 0xff);
    }
    
    private String getHexString(int index)
    {
        byte b = buffer.get(index);
        return Integer.toHexString(b & 0xff);
    }
    
    
    public String getString(int numBytes)
    {
        byte[] byteArr = new byte[numBytes];
        buffer.get(pointer, byteArr);
        pointer += numBytes;
        return new String(byteArr);
    }
    
    public int[] getHalfBytes()
    {
        int[] halfBytes = new int[2];
        byte bite = buffer.get(pointer);
        halfBytes[0] = (bite & 240) >> 4;
        halfBytes[1] = (bite & 15);
        pointer += 1;
        return  halfBytes;
    }
    
    public int getUnsignedByteI()
    {
        int value = buffer.get(pointer) & 0xff;
        pointer++;
        return value;
    }
    
    public int getSignedByteI()
    {
        int value = buffer.get(pointer);
        pointer++;
        return value;
    }
    
    public long getUnsignedByteL()
    {
        long value = buffer.get(pointer) & 0xff;
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
    
    public boolean validateTag1Byte(int... bytesToCheck)
    {
        for(int check : bytesToCheck)
        {
            if(check != getUnsignedByteI())
            {
                return false;
            }
        }
        return true;
    }
    
    public boolean validateTag2Byte(int... bytesToCheck)
    {
        for(int i = 0; i < bytesToCheck.length; i++)
        {
            if(bytesToCheck[i] != buffer.getShort(pointer + i))
            {
                return false;
            }
        }
        return true;
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
    public boolean hasBytes()
    {
        return pointer < length;
    }
    
    public byte[] getBytes(int length)
    {
        byte[] bites = new byte[length];
        System.arraycopy(buffer.array(), pointer, bites, 0, length);
        pointer += length;
        return bites;
    }
    
    public byte[] getBytes(int length, boolean noPointer)
    {
        
        byte[] bites = new byte[length];
        System.arraycopy(buffer.array(), pointer, bites, 0, length);
        if(!noPointer)
        {
            pointer += length;
        }
        return bites;
    }
    
    public int[] getBytesUnsignedInt8(int length)
    {
        int[] bites = new int[length];
        for(int i = 0; i < length; i++)
        {
            bites[i] = getUnsignedByteI();
        }
        return bites;
    }
    
    public int getLength()
    {
        return length;
    }
}
