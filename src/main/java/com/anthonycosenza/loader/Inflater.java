package com.anthonycosenza.loader;

import com.anthonycosenza.text.ByteReader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Arrays;

public class Inflater
{
    public static final int MAX_BITS = 15;
    public static final int MAX_L_CODES = 286;
    public static final int MAX_D_CODES = 30;
    public static final int MAX_CODES = MAX_L_CODES + MAX_D_CODES;
    public static final int FIX_L_CODES = 288;
    public static final short[] DYNAMIC_ORDER = new short[]
            {16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15};
    public static final short[] DECODE_LENGTHS = new short[]
            {3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 15, 17, 19, 23, 27, 31, 35, 43, 51, 59, 67, 83, 99, 115, 131, 163, 195, 227, 258};
    public static final short[] DECODE_EXTRA_BITS = new short[]
            {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 0};
    public static final short[] DISTANCE_OFFSETS = new short[]
            {1, 2, 3, 4, 5, 7, 9, 13, 17, 25, 33, 49, 65, 97, 129, 193, 257, 385, 513, 769, 1025, 1537, 2049, 3073, 4097, 6145, 8193, 12289, 16385, 24577};
    public static final short[] DISTANCE_EXTRA_BITS = new short[]
            {0, 0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 12, 13, 13};
    
    
    private int writtenBytes = 0;
    private int zLibFlags;
    private int otherFlags;
    private int compressionMethod;
    private int compressionInfo;
    private int fCheck;
    private int fDict;
    private int fLevel;
    private boolean isFinal;
    private int nlen;
    private int ndist;
    private int ncode;
    private BitReader bits;
    private Huffman codeCodes;
    private Huffman codes;
    private Huffman distance;
    
    /*
     * The documentation for the inflate algorithm can be found here.
     * https://www.ietf.org/rfc/rfc1951.txt
     * An implementation of the algorithms can be found here.
     * https://github.com/madler/zlib/blob/master/contrib/puff/puff.c#L436
     * There's also a png parser that fully narrates the output here.
     * https://github.com/madler/infgen
     */
    public Inflater(ByteBuf input)
    {
        int byteCount = input.readableBytes();
        byte[] bytes = input.array();
        bits = new BitReader(Arrays.copyOfRange(bytes, 0, byteCount), BitReader.ByteType.RIGHT_TO_LEFT);
    
        
        //Checksum don't work yo. I think it needs to be done on the original data not the uncompressed data,
        //as well as including the 4 byte tag header(not the length though).
        /*Checksum check = new CRC32();
        check.update(getBytes(), 0, maxBytes);
        
        long crc = check.getValue();
        System.out.println("Checksum: " + checksum + ", CRC Checksum: " + crc);
        if(crc != checksum)
        {
            throw new RuntimeException("Checksum doesn't match.");
        }*/
    }

    
    public ByteBuf inflate(int bufferSize)
    {
        ByteBuf output = Unpooled.buffer(bufferSize);
        zLibFlags = bits.getInt(8);
        otherFlags = bits.getInt(8);
        compressionMethod = zLibFlags & 15;
        //Get highest 4 bits
        compressionInfo = zLibFlags >> 4;
        fCheck = otherFlags & 15;
        if(compressionMethod == 8)
        {
            fDict = -1;
            fLevel = otherFlags >> 5;
        }
        else
        {
            fDict = otherFlags & 16;
            fLevel = otherFlags & 48;
        }

        //Validate that the data flags we got are valid.
        if((zLibFlags * 256 + otherFlags) % 31 != 0)
        {
            throw new RuntimeException("Bad zLib Header");
        }
        huffmanDecode(output, bits);
        
        return output;
    }
    
    private void huffmanDecode(ByteBuf output, BitReader bits)
    {
        isFinal = false;
        
        /*
         * The chunk of data that gets sent into here will be split up into 65k byte chunks, those chunk lines don't necessarily line up on IDAT chunk lines.
         * So we loop through each chunk and read the header for what type of compression it has and the bit flag for if it's the final chunk in the series.
         * Decompressing each as we go.
         */
        while(!isFinal)
        {
            isFinal = bits.getBit();
            int compressionType = bits.getInt(2);
    
            /*
             * 00 no Compression
             * 01 fixed Huffman codes
             * 10 dynamic Huffman codes
             * 11 reserved/error
             */
            switch(compressionType)
            {
                //No compression
                case 0 -> throw new RuntimeException("Handle Uncompressed Huffman.");
                //Static Huffman Codes
                case 1 -> throw new RuntimeException("Handle Static Huffman.");
                //Dynamic Huffman Codes
                case 2 -> dynamicHuffman(output, bits);
                //Using either a reserved code or a value outside the bounds.
                default -> throw new RuntimeException("Error: Huffman compression used wrong format: " + compressionType);
            }
        }
    }
    
    
    public void dynamicHuffman(ByteBuf output, BitReader bits)
    {
        /*
         * First we need to retrieve the codes that we can use to decode the entire huffman encoded file.
         * Unfortunately they're encoded to save space, fortunately they're encoded using huffman codes,
         * So once we can decode the codes we should have no trouble decoding the file.
         *
         * What we're aiming to get by the end of this section is a list of literal codes(nlen) and
         * a list of distance codes(ndist)
         *
         * What they've given us is ncode number of codes to decipher those 2 lists with.
         *
         * First we read the number of codes
         */
        nlen = bits.getInt(5) + 257;// # of literal/length codes
        ndist = bits.getInt(5) + 1;// # of distance codes
        ncode = bits.getInt(4) + 4;// # of code length codes
    
        /*
         * For every code we need to grab a 3 bit value, and assign it to an array that tracks length for each value.
         * The DYNAMIC_ORDER array is used to convert the weird order into a i = 0 = D_O[i] = 16
         * which indexes the length array at 16 and sets the lengths to the 3 bits we got earlier.
         *
         * We create an array that can hold all the length and distance codes in one place.
         * Since they only gave us ncode number of values to work with they're stored in a peculiar order DYNAMIC_ORDER
         *
         * Every set of 3 bits is a length that needs to be assigned to the length array at the index specified by DYNAMIC_ORDER
         */
        int[] lengths = new int[MAX_CODES];
        for(int i = 0; i < ncode; i++)
        {
            int len = bits.getInt(3);
            lengths[DYNAMIC_ORDER[i]] = len;
        }
    
        /*
         * We have now created the first set of huffman codes used to decipher the codes for reading the rest of the data.
         */
        codeCodes = new Huffman(lengths, 0, DYNAMIC_ORDER.length);
        
        /*
         * We can now feed bits into decode and output the correct symbol to be injected into the output stream.
         * The exact value of the symbol gets modulated depending on it's initial value.
         *
         * The formula can be found in the docs  pg12 heading 3.2.7
         *
         * Values of 0-15 don't change.
         * Value 16 repeats the previous value 3 + (the next 2 bits as an int from the input stream)
         * Value 17 adds some number of 0s = 3 + (the next 3 bits as an int from the input stream)
         * Value 18 adds some number of 0s = 11 + (the next 7 bits as an int from the input stream)
         */
        int index = 0;
        int val = 0;
        int repeat = 0;
        while(index < nlen + ndist)
        {
            int symbol = codeCodes.decode(bits);
            
            if(symbol < 0)
            {
                throw new RuntimeException("Invalid Symbol: " + symbol);
            }
            if(symbol < 16)
            {
                lengths[index++] = symbol;
            }
            else
            {
                val = 0;
                if(symbol == 16)
                {
                    if(index == 0) throw new RuntimeException("No previous length to repeat");
    
                    val = lengths[index - 1];
                    repeat = 3 + bits.getInt(2);
                    
                }
                else if(symbol == 17)
                {
                    repeat = 3 + bits.getInt(3);
                }
                else if(symbol == 18) //Symbol is 18
                {
                    repeat = 11 + bits.getInt(7);
                }
                else throw new RuntimeException("Symbols above 18 have no meaning - Symbol: " + symbol);
                for(int i = 0; i < repeat; i++)
                {
                    lengths[index++] = val;
                }
            }
        }
        
        /*
         * Code 256 is always designated as the termination value
         * signifying the end of the input stream to be decoded.
         */
        if(lengths[256] == 0) throw new RuntimeException("No termination code set on index 256: " + lengths[256]);
        
        /*
         * The length array we have now is 2 sets of data the literal codes and the distance codes combined into 1.
         *
         * We no longer need the old huffman table and can reuse the pointer.
         */
        codes = new Huffman(lengths, 0, nlen);
        distance = new Huffman(lengths, nlen, ndist);
        
        /*
         * Ok listen up. If the symbol is less than 256 then we can write the symbol as is without doing anything strange to it,
         * just like we did with symbols less than 16 in the previous section.
         *
         * For everything above 256 we aren't just doing math to figure out the value,
         * we're actually calculating how long the string of bits we want to copy into the output stream is going to be.
         * Symbol is over 256, so we grab the base length for that symbol from DECODE_LENGTHS, and most of the values are
         * going to need a bit of extra information so check the DECODE_EXTRA_BITS array to see how many more bits
         * we need to read and then add them together.
         *
         * Remember we're converting a bunch of bits into whole bytes, don't overthink it.
         * A symbol gets converted into a byte array and sent to the output.
         */
        int symbol = 0;
        int len = 0;
        int dist = 0;
        int start = 0;
        do
        {
            symbol = codes.decode(bits);
            if(symbol < 0) throw new RuntimeException("Bad length symbol: " + symbol);
            
            if(symbol < 256)
            {
                output.writeByte(symbol);
                writtenBytes++;
            }
            else if(symbol > 256)
            {
                //Lets us index the arrays properly.
                symbol -= 257;
                len = DECODE_LENGTHS[symbol] + bits.getInt(DECODE_EXTRA_BITS[symbol]);
                
                //Fill symbol with the distance from the distance huffman code
                symbol = distance.decode(bits);
                if(symbol < 0) throw new RuntimeException("Bad distance symbol: " + symbol);
                dist = DISTANCE_OFFSETS[symbol] + bits.getInt(DISTANCE_EXTRA_BITS[symbol]);
    
                if(dist > writtenBytes) throw new RuntimeException("Trying to read more bytes than are available - Dist: " + dist + ", Available: " + writtenBytes);
                
                //Starting at distance backwards, fill the array with values.
                start = writtenBytes - dist;
                for(int i = 0; i < len; i++)
                {
                    output.writeByte(output.getByte(start + (i % dist)));
                    writtenBytes++;
                }
            }
            
        }while(symbol != 256);
    }
    
    public boolean isFinal()
    {
        return isFinal;
    }
}
