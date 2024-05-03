package com.anthonycosenza.util;

public class StringUtils
{
    
    public static String toHex(byte... data)
    {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < data.length; i++)
        {
            builder.append(getHexString(data[i]));
            if(i + 1 != data.length)
            {
                builder.append(" ");
            }
        }
        return builder.toString();
    }
    
    public static String getByteString(byte... data)
    {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < data.length; i++)
        {
            builder.append(Integer.toBinaryString(data[i] & 0xff));
            if(i + 1 != data.length)
            {
                builder.append(" ");
            }
        }
        return builder.toString();
    }
    
    public static String getHexString(int value)
    {
        return Integer.toHexString(value & 0xff);
    }
}
