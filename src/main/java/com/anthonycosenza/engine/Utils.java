package com.anthonycosenza.engine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Utils
{
    public static String loadResource(String filepath)
    {
        String str;
        try
        {
            str = new String(Files.readAllBytes(Paths.get(filepath)));
        }catch(IOException e)
        {
            //System.out.println(Paths.get("/resources" + filepath).toAbsolutePath().toString());
            throw new RuntimeException("Error reading file[" + filepath + "]: " + e);
        }
        return str;
    }
    
    public static float[] listFloatToArray(List<Float> floats)
    {
        int size = floats != null ? floats.size() : 0;
        float[] floatArr = new float[size];
        for(int i = 0; i < size; i++)
        {
            floatArr[i] = floats.get(i);
        }
        return floatArr;
    }
    public static int[] listIntToArray(List<Integer> ints)
    {
        return ints.stream().mapToInt((Integer v) -> v). toArray();
    }
    
}
