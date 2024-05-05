package com.anthonycosenza.loader;


import com.anthonycosenza.text.ByteReader;
import com.anthonycosenza.util.FileIO;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class PNGLoader
{
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
    private ByteBuf filteredOutput;
    int[] pixelData;
    Inflater inflater;
    
    public PNGLoader(String filepath, boolean shouldAlpha)
    {
        ByteReader reader = new ByteReader(ByteBuffer.wrap(FileIO.getFileBytes(filepath)));

        decode(reader);
        if(shouldAlpha && channels < 4)
        {
            pixelData = addAlpha(pixelData);
        }
        
    }
    private int[] addAlpha(int[] pixels)
    {
        int[] newPixels = new int[(pixels.length / 3) * 4];
        int index = 0;
        for(int i = 0; i < newPixels.length; i++)
        {
            if(i % 4 == 3)
            {
                newPixels[i] = 255;
            }
            else newPixels[i] = pixels[index++];
        }
        return newPixels;
    }
    
    public boolean validateTag(int[] bytesToCheckAgainst, int... bytesToCheck)
    {
        if(bytesToCheckAgainst.length != bytesToCheck.length) throw new RuntimeException("Tag bytes length mismatch");
        for(int i = 0; i < bytesToCheckAgainst.length; i++)
        {
            if(bytesToCheckAgainst[i] !=bytesToCheck[i] )
            {
                return false;
            }
        }
        return true;
    }
    
    
    private void decode(ByteReader reader)
    {
        if(!reader.validateTag1Byte(137, 80, 78, 71, 13, 10, 26, 10))
        {
            throw new RuntimeException("PNG Corrupted.");
        }
        int tags = 0;
        
        while(reader.pointer + 8 < reader.getLength())
        {
            long chunkLength = reader.getUnsignedInt32();
            int[] tag = reader.getBytesUnsignedInt8(4);
            
            //IHDR
            if(validateTag(tag, 73, 72, 68, 82))
            {
                if(tags != 0)
                {
                    throw new RuntimeException("IHDR must be the first tag.");
                }
                decodeIHDR(reader);
                
                //System.out.println("Image Dimensions: " + width + ", " + height);
                //System.out.println("Image Type: " + imageType + "-" + bitDepth + "bit");
                //System.out.println("Compression: " + compressionMethod + ", Filter: " + filterMethod + ", Interlace: " + interlaceMethod);
    
                tags++;
            }
            
            //sRGB Optional
            else if(validateTag(tag, 115, 82, 71, 66))
            {
                renderingIntent = reader.getUnsignedInt8();
                long checksum = reader.getUnsignedInt32();
                tags++;
            }
    
            //gAMA Optional
            else if(validateTag(tag, 103, 65, 77, 65))
            {
                gamma = (int) reader.getUnsignedInt32();
                long checksum = reader.getUnsignedInt32();
                tags++;
            }
    
            //pHYs Optional
            else if(validateTag(tag, 112, 72, 89, 115))
            {
                pixelsPerX = (int) reader.getUnsignedInt32();
                pixelsPerY = (int) reader.getUnsignedInt32();
                pixelsUnit = (int) reader.getUnsignedInt8();
                long checksum = reader.getUnsignedInt32();
                
                tags++;
            }
    
            //IDAT
            else if(validateTag(tag, 73, 68, 65, 84))
            {
                //System.out.println("IDAT Tag, Pointer: " + reader.pointer);
                decodeIDAT(chunkLength, reader);
            }
            //IEND
            else if(validateTag(tag, 73, 69, 78, 68))
            {
                break;
            }
            
        }
    }
    
    private void decodeIDAT(long length, ByteReader reader)
    {
        //Concatenate all data.
        ByteBuf compressed = Unpooled.buffer();
        do{
            compressed.writeBytes(reader.getBytes((int) length));
            //Skip checksum
            reader.pointer += 4;
            length = reader.getUnsignedInt32();
        }
        while(validateTag(reader.getBytesUnsignedInt8(4), 73, 68, 65, 84));
    
        inflater = new Inflater(compressed);
        filteredOutput = inflater.getBytes();
        
        /*
        * Checking if that was the last IDAT chunk.
        * Un-filter the data back to normal.
        */
        if(inflater.isFinal())
        {
            unFilter(filteredOutput);
        }
        //Add 4 to skip to the checksum
        reader.pointer += 4;
        //System.out.println(Arrays.toString(pixelData));
        
    }
    private void unFilter(ByteBuf data)
    {
        pixelData = new int[width * height * channels];
        
        int filterFunction = 0;
        int rowWidth = width * channels;
    
        //data.resetReaderIndex();
        for(int y = 0; y < height; y++)
        {
            //Cut out the filter function
            //filterFunction = (int)data[rowWidth * y] & 0xff;
            filterFunction = (int) data.readByte() & 0xff;

            //start at 1 to
            for(int x = 0; x < rowWidth; x++)
            {
                int index = y * rowWidth + x;
                int upIndex = index - rowWidth;
    
                //Default values above or to the left of the "scanline" are to be treated as 0
                int z = (data.readByte() & 0xff);
                //builder.append(z).append(", ");
                int a = ((x - channels) < 0) ? 0 : pixelData[index - channels];
                int b = (y == 0) ? 0 : pixelData[upIndex];
                int c = (y == 0) ? 0 : ((x - channels < 0) ? 0 : pixelData[upIndex - channels]);
                
                //Use the filter function to unscramble the data.
                pixelData[index] = switch(filterFunction)
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
                //pixelData[index] %= 256;
            }
        }
    }
    
    private void decodeIHDR(ByteReader reader)
    {
        width = (int)reader.getUnsignedInt32();
        height = (int)reader.getUnsignedInt32();
        bitDepth = reader.getUnsignedInt8();
        imageType = reader.getUnsignedInt8();
        compressionMethod = reader.getUnsignedInt8();
        filterMethod = reader.getUnsignedInt8();
        interlaceMethod = reader.getUnsignedInt8();
        
        if(!validateImageType(imageType, bitDepth))
        {
            throw new RuntimeException("IHDR parsing failed, image of Type: " + imageType + ", with bit depth of: " + bitDepth + ", is not allowed.");
        }
        
        long checksum = reader.getUnsignedInt32();
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
    
    private byte[] getAllIDATData(ByteReader reader)
    {
    
        //int zLibFlags = reader.getUnsignedInt8();
        //int otherFlags = reader.getUnsignedInt8();
    
        reader.pointer -= 10;
        
        
        
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        int idatCounter = 0;
        while(true)
        {
            //Read length
            int length = (int) reader.getUnsignedInt32();
            
            //Read IDAT header
            if(!reader.validateTag1Byte(73, 68, 65, 84))
            {
                break;
            }
            /*int offset = (idatCounter == 0 ? 4 : 2);
            if(idatCounter != 0)
            {
                reader.pointer += 2;
            }*/
            //Could also advance the pointer by 4
            //int zLibFlags = reader.getUnsignedInt8();
            //int otherFlags = reader.getUnsignedInt8();
            try
            {
                stream.write(reader.getBytes(length + 4));
            } catch(IOException e)
            {
                throw new RuntimeException(e);
            }
            //reader.pointer += length + 4;
            idatCounter++;
        }
        
        //stream.write(0);
        try
        {
            stream.close();
        } catch(IOException e)
        {
            throw new RuntimeException(e);
        }
        return stream.toByteArray();
    }
    
    
    //Get pretty close by running nowrap=false and adding the checksum to the decompress stream.
    private byte[] getAllIDATData2(ByteReader reader)
    {
        reader.pointer -= 8;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        while(true)
        {
            //Read length
            int length = (int) reader.getUnsignedInt32();
            
            //Read IDAT header
            if(!reader.validateTag1Byte(73, 68, 65, 84))
            {
                break;
            }
            //Could also advance the pointer by 4
            int zLibFlags = reader.getUnsignedInt8();
            int otherFlags = reader.getUnsignedInt8();
            try
            {
                stream.write(reader.getBytes(length - 2));
            } catch(IOException e)
            {
                throw new RuntimeException(e);
            }
            reader.pointer += length + 2;
        }
    
        //stream.write(0);
        try
        {
            stream.close();
        } catch(IOException e)
        {
            throw new RuntimeException(e);
        }
        return stream.toByteArray();
    }
    
}
