package com.anthonycosenza.engine.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils
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
    public static String getExtension(File file)
    {
        String[] split = file.getAbsolutePath().split("\\.");
        return split[split.length - 1];
    }
    
}
