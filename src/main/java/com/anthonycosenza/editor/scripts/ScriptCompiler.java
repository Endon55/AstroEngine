package com.anthonycosenza.editor.scripts;

import com.anthonycosenza.editor.EditorIO;
import com.anthonycosenza.engine.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class ScriptCompiler
{
    //Compile all nodes and materials and whatever.
    //Link everything into a jar file.
    //Place jar file in IDE resources.
    private static URLClassLoader loader;
    private ScriptCompiler() { }
    
    
    private static void checkLoader()
    {
        if(loader == null)
        {
            compile();
        }
    }

    public static Class<?> load(String className)
    {
        checkLoader();
        try
        {
            return loader.loadClass(className);
        } catch(ClassNotFoundException e)
        {
            throw new RuntimeException("Failed to load class(" + className + ") -" + e);
        }
    }
    public static void compile()
    {
        compile(findSources(EditorIO.getProjectDirectory()), EditorIO.getOutDirectory());
    }
    
    public static void compile(List<File> sourceFiles,  File outputDirectory)
    {
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
        try
        {
            if(loader != null)
            {
                loader.close();
            }
            loader = new URLClassLoader(new URL[]{
                    EditorIO.getOutDirectory().toURI().toURL()});
        } catch(MalformedURLException e)
        {
            throw new RuntimeException(e);
        } catch(IOException e)
        {
            throw new RuntimeException("Failed to close class loader -" + e);
        }
    }
    private static List<File> findSources(File directory)
    {
        List<File> sourceFiles = new ArrayList<>();
        
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
        return sourceFiles;
    }
}

