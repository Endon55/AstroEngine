package com.anthonycosenza.engine.loader.text.tables.encoding;

public class Format4Encoding implements FormatEncoding
{
    private int[] startCodes;
    private int[] endCodes;
    private int[] idDeltas;
    private int[] idRangeOffsets;
    
    public Format4Encoding(int[] startCodes, int[] endCodes, int[] idDeltas, int[] idRangeOffsets)
    {
        this.startCodes = startCodes;
        this.endCodes = endCodes;
        this.idDeltas = idDeltas;
        this.idRangeOffsets = idRangeOffsets;
    }
    
    @Override
    public int getGlyphID(int characterCode)
    {
        for(int i = 0; i < endCodes.length; i++)
        {
            if(endCodes[i] >= characterCode && startCodes[i] <= characterCode)
            {
                if(idRangeOffsets[i] == 0)
                {
                    return characterCode + idDeltas[i];
                }
                else //Relies on extracting the glyphIDArray Value
                {
                    throw new RuntimeException("Need to extract glyphIDArray Value");
                    //return (idRangeOffsets[i] / 2 + (characterCode - startCodes[i]) + idRangeOffsets[i]);
                }
            }
        }
        return 0;
    }
    
}
