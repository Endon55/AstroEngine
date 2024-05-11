package com.anthonycosenza.engine.loader.text.tables.types;

import com.anthonycosenza.engine.util.reader.ByteReader;
import com.anthonycosenza.engine.loader.text.FontData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class cffDict
{
    private Map<String, List<Number>> table;
    private int startIndex;
    private int entryCount;
    private int offsetByteCount;
    private int offsetBetweenEntries;
    
    
    public cffDict(FontData fontData, ByteReader reader)
    {
        this(0, false, fontData, reader);
    }
    public cffDict(int length, boolean raw, FontData fontData, ByteReader reader)
    {
        /*
         * So we read the first byte and based on the data that bit holds we can determine the size of the data.
         * Keys(1 or 2 bytes) called operators, Values(integer or real numbers) called operands.
         * They're encoded backwards as (Values, Keys) or (Operands, Operators)
         */
        table = new HashMap<>();
        if(raw)
        {
            entryCount = 1;
            offsetBetweenEntries = length;
        }
        else
        {
            entryCount = reader.getUnsignedInt16();
            offsetByteCount = reader.getUnsignedInt8();
    
            int offsetStart = reader.getUnsignedInt8();
            int offsetEnd = reader.getUnsignedInt8();
            offsetBetweenEntries = offsetEnd - offsetStart;
    
            startIndex = reader.pointer;
        }
        List<Number> values = new ArrayList<>();
        
        int start = reader.pointer;
        while(reader.pointer < start + offsetBetweenEntries)
        {
            int b0 = reader.getUnsignedInt8();
            
            //0 to 21 specifies an operator.
            if(b0 >= 0 && b0 <= 21)
            {
    
                String operatorStr = switch(b0)
                        {
                            case 0 -> "version";
                            case 1 -> "Notice";
                            case 2 -> "FullName";
                            case 3 -> "FamilyName";
                            case 4 -> "Weight";
                            case 5 -> "FontBBox";
                            case 6 -> "BlueValues";
                            case 7 -> "OtherBlues";
                            case 8 -> "FamilyBlues";
                            case 9 -> "FamilyOtherBlues";
                            case 10 -> "StdHW";
                            case 11 -> "StdVW";
                            case 12 ->
                            {
                                int operator2 = reader.getUnsignedInt8();
                                yield switch(operator2)
                                        {
                                            case 0 -> "Copyright";
                                            case 1 -> "isFixedPitch";
                                            case 2 -> "ItalicAngle";
                                            case 3 -> "UnderlinePosition";
                                            case 4 -> "UnderlineThickness";
                                            case 5 -> "PaintType";
                                            case 6 -> "CharstringType";
                                            case 7 -> "FontMatrix";
                                            case 8 -> "StrokeWidth";
                                            case 9 -> "BlueScale";
                                            case 10 -> "BlueShift";
                                            case 11 -> "BlueFuzz";
                                            case 12 -> "StemSnapH";
                                            case 13 -> "StemSnapV";
                                            case 14 -> "ForceBold";
                                            case 17 -> "LanguageGroup";
                                            case 18 -> "ExpansionFactor";
                                            case 19 -> "initialRandomSeed";
                                            case 20 -> "SyntheticBase";
                                            case 15, 16, 24, 25, 26, 27, 28, 29 -> throw new RuntimeException("Reserved for what??????? Operator2:" + operator2);
                                            case 21 -> "PostScript";
                                            case 22 -> "BaseFontName";
                                            case 23 -> "BaseFontBlend";
                                            case 30 -> "ROS";
                                            case 31 -> "CIDFontVersion";
                                            case 32 -> "CIDFontRevision";
                                            case 33 -> "CIDFontType";
                                            case 34 -> "CIDCount";
                                            case 35 -> "UIDBase";
                                            case 36 -> "FDArray";
                                            case 37 -> "FDSelect";
                                            case 38 -> "FontName";
                                            default -> throw new RuntimeException("Default reserved for what??????? Operator2:" + operator2);
                                        };
                            }
                            case 13 -> "UniqueID";
                            case 14 -> "XUID";
                            case 15 -> "charset";
                            case 16 -> "Encoding";
                            case 17 -> "CharStrings";
                            case 18 -> "Private";
                            case 19 -> "Subrs";
                            case 20 -> "defaultWidthX";
                            case 21 -> "nominalWidthX";
                            case 28 -> "shortint";
                            case 29 -> "longint";
                            case 30 -> "BCD";
                            case 22, 23, 24, 25, 26, 27, 31, 255 -> throw new RuntimeException("Reserved for what??????? Operator:" + b0);
                            default ->
                            {
                                if(b0 >= 32 && b0 <= 246)
                                {
                                    values.add(reader.getUnsignedInt16());
                                    yield "3byte";
                                }
                                //2 Byte sequence specifying a number, I think we throw out the first byte since it was used to index the table.
                                else if(b0 >= 247 && b0 <= 254)
                                {
                                    values.add(reader.getUnsignedInt8());
                                    yield "2byte";
                        
                                }
                                else
                                    throw new RuntimeException("No idea what Operator does: " + b0 + "\nTable - Index: " + reader.pointer + "\n" + table);
                    
                                //yield "";
                            }
                        };
    
                if(!values.isEmpty())
                {
                    if(table.containsKey(operatorStr))
                    {
                        table.get(operatorStr).addAll(values);
                    }
                    else table.put(operatorStr, values);
                }
                values = new ArrayList<>();
            }
            else
            {
                //Data is 1 byte long
                if(b0 >= 32 && b0 <= 246)
                {
                    values.add(b0 - 139);
                }
                //2 bytes long
                else if(b0 >= 247 && b0 <= 250)
                {
                    int b1 = reader.getUnsignedInt8();
                    values.add((b0 - 247) * 256 + b1 + 108);
                }
                //2 bytes long also
                else if(b0 >= 251 && b0 <= 254)
                {
                    int b1 = reader.getUnsignedInt8();
                    values.add(-(b0 - 251) * 256 - b1 - 108);
                }
                //3 bytes long
                else if(b0 == 28)
                {
                    int b1 = reader.getUnsignedInt8();
                    int b2 = reader.getUnsignedInt8();
                    values.add((b1 << 8) | b2);
                }
                //5 bytes long
                else if(b0 == 29)
                {
                    int b1 = reader.getUnsignedInt8();
                    int b2 = reader.getUnsignedInt8();
                    int b3 = reader.getUnsignedInt8();
                    int b4 = reader.getUnsignedInt8();
                    values.add((b1 << 24) | (b2 << 16) | (b3 << 8) | b4);
                }
                //Real Number
                else if(b0 == 30)
                {
                    throw new RuntimeException("Solve nibbles nerd.");
                    //int[] nibbles = reader.getHalfBytes();
                    //System.out.println(Arrays.toString(nibbles));
                }
                else
                {
                    throw new RuntimeException("There shouldn't be an else bucko, your data is fucked. b0: " + b0 + ", Index: " + reader.pointer);
                }
            }
        }
    }
    
    public Map<String, List<Number>> getTable()
    {
        return table;
    }
    
    public List<Number> getValue(String key)
    {
        return table.getOrDefault(key, null);
    }
    
    private int getOffsetValue(int offsetSize, ByteReader reader)
    {
        if(offsetSize == 1)
        {
            return reader.getUnsignedInt8();
        }
        else if(offsetSize == 2)
        {
            return reader.getUnsignedInt16();
        }
        else if(offsetSize == 3)
        {
            return reader.getUnsignedInt24();
        }
/*        else if(offsetSize == 4)
        {
            return (int)reader.getUnsignedInt32();
        }*/
        throw new RuntimeException("Can't handle offsets of size: " + offsetSize);
    }
    
}
