package com.anthonycosenza.editor.external;

import com.anthonycosenza.editor.EditorIO;
import com.anthonycosenza.engine.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class APIGenerator
{
    //Compile all nodes and materials and whatever.
    //Link everything into a jar file.
    //Place jar file in IDE resources.
    List<File> sourceFiles;
    public APIGenerator()
    {
    }
    
    public void generate(File sourceDirectory, File outputDirectory)
    {
        findSources(sourceDirectory);
        System.out.println(sourceFiles);
        File tmp = EditorIO.getTempDirectory();
        ProcessBuilder builder = new ProcessBuilder();
        //, "C:\\Coding\\Astro\\AstroEngine\\build\\AstroEngine\\AstroAPI.jar"
        builder.command("javac",
                "-cp", "C:\\Coding\\Astro\\AstroEngine\\build\\AstroEngine\\AstroAPI.jar",
                "-d", outputDirectory.getAbsolutePath(),
                sourceFiles.get(0).getAbsolutePath());
        
        builder.inheritIO();
        try
        {
            builder.start();
        } catch(IOException e)
        {
            throw new RuntimeException(e);
        }
    }
    private void findSources(File directory)
    {
        sourceFiles = new ArrayList<>();
        
        
        Stack<File> files = new Stack<>();
        files.add(directory);
        while(!files.isEmpty())
        {
            File file = files.pop();
            
            if(file.isDirectory())
            {
                File[] children = file.listFiles();
                if(children != null)
                {
                    Collections.addAll(files, file.listFiles());
                }
            }
            else if(FileUtils.getExtension(file).equals("java"))
            {
                sourceFiles.add(file);
            }
        }
    }
}

