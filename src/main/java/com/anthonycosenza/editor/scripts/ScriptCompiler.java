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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class ScriptCompiler
{
    //Compile all nodes and materials and whatever.
    //Link everything into a jar file.
    //Place jar file in IDE resources.
    
    private static URLClassLoader loader;
    private static final Map<String, Long> modifiedMap = new HashMap<>();
    private ScriptCompiler() { }
    
    public static String getDefaultScript(String scriptName)
    {
        return "import com.anthonycosenza.engine.space.node.Node;" + "\n\n" +
                "public class " + scriptName + " extends Node" +
                "\n{\n" +
                "\t@Override\n" +
                "\tpublic void update(float delta)\n\t{\n\t\tsuper.update(delta);\n\n\t}\n\n" +
                "\t@Override\n" +
                "\tpublic void updatePhysics(float delta)\n\t{\n\t\tsuper.updatePhysics(delta);\n\n\t}\n\n" +
                "\t@Override\n" +
                "\tpublic void updateUI(float delta)\n\t{\n\t\tsuper.updateUI(delta);\n\n\t}\n\n" +
                "\n}";
    }
    
    private static void checkLoader()
    {
        List<File> sources = findSources(EditorIO.getScriptsDirectory());
        if(loader == null || anyModified(sources))
        {
            compile(sources,EditorIO.getOutDirectory());
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
        checkLoader();
    }
    
    private static void compile(List<File> sources, File outputDirectory)
    {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("javac",
                EditorIO.getScriptsDirectory().getAbsolutePath() + "/*.java",
                "-cp", "C:\\Coding\\Astro\\AstroEngine\\build\\AstroEngine\\AstroAPI.jar",
                "-d", outputDirectory.getAbsolutePath());
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
            if(loader == null)
            {
                loader = new URLClassLoader(new URL[]{
                        EditorIO.getOutDirectory().toURI().toURL()});
            }
            
        } catch(MalformedURLException e)
        {
            throw new RuntimeException(e);
        }
        for(File source : sources)
        {
            modifiedMap.put(source.getAbsolutePath(), source.lastModified());
        }
    }
    private static boolean anyModified(List<File> sources)
    {
        for(File source : sources)
        {
            Long lastModified = modifiedMap.get(source.getAbsolutePath());
            if(lastModified == null || source.lastModified() != lastModified)
            {
                return true;
            }
        }
        return false;
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
                    Collections.addAll(files, children);
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

