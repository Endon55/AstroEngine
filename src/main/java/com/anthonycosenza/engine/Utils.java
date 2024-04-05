package com.anthonycosenza.engine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
}
