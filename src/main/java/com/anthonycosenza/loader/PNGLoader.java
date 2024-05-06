package com.anthonycosenza.loader;


import com.anthonycosenza.text.ByteReader;
import com.anthonycosenza.util.FileIO;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.List;

public class PNGLoader
{
    public static final byte[] PNG_INITIAL_TAG = {(byte) 137, (byte) 80, (byte) 78, (byte) 71, (byte) 13, (byte) 10, (byte) 26, (byte) 10};
    public static final byte[] PNG_IHDR_TAG = {(byte) 73, (byte) 72, (byte) 68, (byte) 82};
    public static final byte[] PNG_SRGB_TAG = {(byte) 115, (byte) 82, (byte) 71, (byte) 66};
    public static final byte[] PNG_GAMA_TAG = {(byte) 103, (byte) 65, (byte) 77, (byte) 65};
    public static final byte[] PNG_PHYS_TAG = {(byte) 112, (byte) 72, (byte) 89, (byte) 115};
    public static final byte[] PNG_IDAT_TAG = {(byte) 73, (byte) 68, (byte) 65, (byte) 84};
    public static final byte[] PNG_IEND_TAG = {(byte) 73, (byte) 69, (byte) 78, (byte) 68};
    
    
    public int width;
    public int height;
    private int bitDepth;
    private int imageType;
    private int channels;
    private boolean hasAlpha;
    private int compressionMethod;
    private int filterMethod;
    private int interlaceMethod;
    private int renderingIntent;
    private int gamma;
    private int pixelsPerX;
    private int pixelsPerY;
    private int pixelsUnit;
    private int[] rawPixels;
    private float[] pixelData;
    
    public PNGLoader(String filepath)
    {
        ByteBuf reader = Unpooled.wrappedBuffer(FileIO.getFileBytes(filepath));
    
        decode(reader);
        
        /*
         * Convert the unfiltered data into it's final form.
         * It would be more efficient to combine this with this section with the raw pixel array from unFilter.
         */
        pixelData = new float[width * height * (hasAlpha ? channels : channels + 1)];
        int index = 0;
        for(int i = 0; i < pixelData.length; i++)
        {
            if(hasAlpha)
            {
                pixelData[i] = rawPixels[index++] / 255f;
            }
            else
            {
                if(i % 4 == 3)
                {
                    pixelData[i] = 1f;
                }
                else
                {
                    pixelData[i] = rawPixels[index++] / 255f;
                }
            }
        }
    }

    private void decode(ByteBuf reader)
    {
        if(!checkTag(PNG_INITIAL_TAG, reader)) throw new RuntimeException("PNG doesn't have correct header.");
        int tags = 0;
        
        while(reader.readerIndex() < reader.capacity())
        {
            long chunkLength = reader.readUnsignedInt();
            byte[] tag = getTag(4, reader);
            
            //IHDR
            if(checkTag(PNG_IHDR_TAG, tag))
            {
                if(tags != 0)
                {
                    throw new RuntimeException("IHDR must be the first tag.");
                }
                decodeIHDR(reader);
                
                tags++;
            }
            
            //sRGB Optional
            else if(checkTag(PNG_SRGB_TAG, tag))
            {
                renderingIntent = reader.readUnsignedByte();
                long checksum = reader.readUnsignedInt();
                tags++;
            }
    
            //gAMA Optional
            else if(checkTag(PNG_GAMA_TAG, tag))
            {
                gamma = (int) reader.readUnsignedInt();
                long checksum = reader.readUnsignedInt();
                tags++;
            }
    
            //pHYs Optional
            else if(checkTag(PNG_PHYS_TAG, tag))
            {
                pixelsPerX = (int) reader.readUnsignedInt();
                pixelsPerY = (int) reader.readUnsignedInt();
                pixelsUnit = (int) reader.readUnsignedByte();
                long checksum = reader.readUnsignedInt();
                tags++;
            }
    
            //IDAT
            else if(checkTag(PNG_IDAT_TAG, tag))
            {
                //System.out.println("IDAT Tag, Pointer: " + reader.pointer);
                decodeIDAT(chunkLength, reader);
                tags++;
            }
            //IEND
            else if(checkTag(PNG_IEND_TAG, tag))
            {
                break;
            }
            
        }
    }
    
    private void decodeIHDR(ByteBuf reader)
    {
        width = (int) reader.readUnsignedInt();
        height = (int) reader.readUnsignedInt();
        bitDepth = reader.readUnsignedByte();
        imageType = reader.readUnsignedByte();
        compressionMethod = reader.readUnsignedByte();
        filterMethod = reader.readUnsignedByte();
        interlaceMethod = reader.readUnsignedByte();
        
        if(!validateImageType(imageType, bitDepth))
        {
            throw new RuntimeException("IHDR parsing failed, image of Type: " + imageType + ", with bit depth of: " + bitDepth + ", is not allowed.");
        }
        
        long checksum = reader.readUnsignedInt();
    }
    
    private void decodeIDAT(long length, ByteBuf reader)
    {
        /*
         * All IDAT chunks should be concatenated before they're decompressed.
         * Don't include chunk length, tag, or checksum.
         */
        List<ByteBuf> buffers = new ArrayList<>();
        do{
            buffers.add(reader.readRetainedSlice((int) length));
            
            //Skip checksum
            reader.skipBytes(4);
            length = reader.readUnsignedInt();
        }
        while(checkTag(PNG_IDAT_TAG, getTag(4, reader)));
        
        ByteBuf compressed = Unpooled.copiedBuffer(buffers.toArray(new ByteBuf[]{}));
        
        Inflater inflater = new Inflater(compressed);
        
        /*
         * Sent the concatenated data through the decompressor, and leaves us with an uncompressed buffer.
         */
        ByteBuf uncompressedData = inflater.inflate(width * height * channels);
        
        /*
        * Un-filter the data back to normal.
        */
        unFilter(uncompressedData);
        
        //Skip the checksum
        reader.skipBytes(4);
    }
    private void unFilter(ByteBuf data)
    {
        rawPixels = new int[width * height * channels];
        int filterFunction = 0;
        int rowWidth = width * channels;
    
        for(int y = 0; y < height; y++)
        {
            //Cut out the filter function
            filterFunction = data.readUnsignedByte();

            //start at 1 to
            for(int x = 0; x < rowWidth; x++)
            {
                int index = y * rowWidth + x;
                int upIndex = index - rowWidth;
    
                //Default values above or to the left of the "scanline" are to be treated as 0
                int z = data.readUnsignedByte();
                int a = ((x - channels) < 0) ? 0 : rawPixels[index - channels];
                int b = (y == 0) ? 0 : rawPixels[upIndex];
                int c = (y == 0) ? 0 : ((x - channels < 0) ? 0 : rawPixels[upIndex - channels]);
                
                //Use the filter function to unscramble the data.
                rawPixels[index] =
                    switch(filterFunction)
                        {
                            //Subtraction(we do the inverse of the subtraction and add the value)
                            case 0 -> z;
                            case 1 -> (a + z) % 256;
                            //Up
                            case 2 -> (b + z) % 256;
                            //Average
                            case 3 -> (z + (a + b) / 2) % 256;
                            //Paeth whatever the fuck that means.
                            case 4 ->
                            {
                                int p = a + b - c;
                                int pa = Math.abs(p - a);
                                int pb = Math.abs(p - b);
                                int pc = Math.abs(p - c);
                                int val = 0;
                                if(pa <= pb && pa <= pc) val = a;
                                else if(pb <= pc) val = b;
                                else val = c;
    
                                yield (z + val) % 256;
                            }
                            default -> throw new RuntimeException("Invalid filter type: " + filterFunction);
                        };
            }
        }
    }

    
    private boolean validateImageType(int imageType, int bitDepth)
    {
        return switch(imageType)
        {
            //Greyscale
            case 0 ->{
                channels = 1;
                yield bitDepth == 1 || bitDepth == 2 || bitDepth == 4 || bitDepth == 8 || bitDepth == 16;
            }
            //Truecolor
            case 2 ->
            {
                channels = 3;
                yield bitDepth == 8 || bitDepth == 16;
            }
            //Indexed-color
            case 3 ->
            {
                channels = 3;
                yield bitDepth == 1 || bitDepth == 2 || bitDepth == 4 || bitDepth == 8;
            }
            //Greyscale w/alpha
            case 4 ->
            {
                channels = 2;
                hasAlpha = true;
                yield bitDepth == 8 || bitDepth == 16;
            }
            //Truecolor w/alpha
            case 6 ->
            {
                channels = 4;
                hasAlpha = true;
                yield bitDepth == 8 || bitDepth == 16;
            }
            default -> false;
        };
    }
    
    public float[] getPixelData()
    {
        return pixelData;
    }
    
    private boolean checkTag(byte[] tag, ByteBuf reader)
    {
        for(byte b : tag)
        {
            if(reader.readByte() != b)
            {
                return false;
            }
        }
        return true;
    }
    
    private boolean checkTag(byte[] tag, byte[] bytes)
    {
        for(int i = 0; i < tag.length; i++)
        {
            if(tag[i] != bytes[i])
            {
                return false;
            }
        }
        return true;
    }
    
    private byte[] getTag(int bytes, ByteBuf reader)
    {
        byte[] tag = new byte[bytes];
        for(int i = 0; i < bytes; i++)
        {
            tag[i] = reader.readByte();
        }
        return tag;
    }
}
