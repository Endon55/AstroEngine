package com.anthonycosenza.engine.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileIO
{
    public static String getFileContents(String filepath)
    {
        String str;
        try
        {
            str = new String(Files.readAllBytes(Paths.get(filepath)));
        } catch(IOException e)
        {
            throw new RuntimeException("Error reading file[" + filepath + "] - " + e);
        }
        return str;
    }
    
    public static byte[] getFileBytes(String filepath)
    {
        try
        {
            return Files.readAllBytes(Paths.get(filepath));
        } catch(IOException e)
        {
            throw new RuntimeException("Error reading file[" + filepath + "] - " + e);
        }
    }
    
    
}
