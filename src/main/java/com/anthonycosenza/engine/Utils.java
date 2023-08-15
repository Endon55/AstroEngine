package com.anthonycosenza.engine;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Utils
{
    
    public static String loadResource(String filepath)
    {
        String str;
        try
        {
            str = new String(Files.readAllBytes(Path.of(Utils.class.getResource(filepath).toURI())));
        }catch(IOException | URISyntaxException e)
        {
            //System.out.println(Paths.get("/resources" + filepath).toAbsolutePath().toString());
            throw new RuntimeException("Error reading file[" + filepath + "]: " + e);
        }
        return str;
    }
}
