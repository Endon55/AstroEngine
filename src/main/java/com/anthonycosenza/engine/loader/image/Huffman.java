package com.anthonycosenza.engine.loader.image;


public class Huffman
{
    public short[] counts;
    public short[] offsets;
    public short[] symbols;
    
    public Huffman(int[] lengths, int from, int length)
    {
        int to = length + from;
    
        /*
         * Now that we have gotten the lengths of every index we need to count how many of each length we have.
         * Length is how many bits it takes to store this code, code = 110 = 3 bits, we add 1 to the 3 bit bucket.
         * We don't want this array to be 0 indexed, counts[1] should yield how many codes are length 1.
         *
         * We only wrote DYNAMIC_ORDER.length number of values so that's all we need to loop through.
         */
        counts = new short[Inflater.MAX_BITS + 1];
        for(int i = from; i < to; i++)
        {
            counts[lengths[i]]++;
        }
        
        /*
         * Check if we have any codes, if we didn't then all the counts would filter into the 0 bucket and been of size length.
         */
        if(counts[0] == length) throw new RuntimeException("No codes assigned.");
    
        /*
         * The offset array is only used to calculate the symbols array.
         * Essentially just adding the previous offset with the number of codes for that index.
         */
        offsets = new short[Inflater.MAX_BITS + 1];
        offsets[1] = 0;
        for(short i = 1; i < Inflater.MAX_BITS; i++)
        {
            offsets[i + 1] = (short) (offsets[i] + counts[i]);
        }
        /*
         * This one is wacky.
         * This is a compact way to fill the symbols array without having symbols overlap, since the different indexes can
         * yield the same value from the length array, which means the same offset which means the same symbol index.
         * You would overwrite the previous value, instead we add 1 to the offset everytime we use it.
         */
        symbols = new short[Inflater.FIX_L_CODES];
        for(short i = 0; i < length; i++)
        {
            int index = i + from;
            if(lengths[index] != 0)
            {
                symbols[offsets[lengths[index]]++] = i;
            }
        }
    }
    
    public int decode(BitReader bits)
    {

        /*
         * Next is used to index the counts array, we start at 1 since we ignore counts that have 0 length.
         * Code gets each new bit added to it and if it doesn't make a code then the whole code is left shifted
         * to make room for the next bit.
         *
         *  We're iterating through the count array and checking if the current code matches a code we already have
         */
        int code = 0;
        int index = 0;
        int first = 0;
        int count = 0;
        for(int i = 1; i < counts.length; i++)
        {
            code |= bits.getInt(1);
            
            count = counts[i];
            
            if(code - first < count)
            {
                return symbols[index + (code - first)];
            }
            
            index += count;
            first += count;
            
            first <<= 1;
            code <<= 1;
        }
        
        throw new RuntimeException("Failed to find code.");
    }
    
}
