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
    
    public static String getFileName(File file)
    {
        String filepath = file.getName();
        int index = 0;
        int extIndex = filepath.length();
        for(int i = filepath.length() - 1; i >= 0; i--)
        {
            char ch = filepath.charAt(i);
            if(ch == '.')
            {
                extIndex = i;
            }
            if(ch == '\\' || ch == '/')
            {
                break;
            }
            index = i;
        }
        return filepath.substring(index, extIndex);
    }
    public static String getFileNameWithExtension(File file)
    {
        String filepath = file.getName();
        int index = filepath.length();
        for(int i = filepath.length() - 1; i >= 0; i--)
        {
            char ch = filepath.charAt(i);
            if(ch == '\\' || ch == '/')
            {
                break;
            }
            index = i;
        }
        return filepath.substring(index);
    }
}
