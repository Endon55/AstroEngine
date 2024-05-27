package com.anthonycosenza.editor;

import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.toml.TomlFormat;

import java.io.File;
import java.io.IOException;

public class ProjectFileUtil
{
    private static File projectDirectory = null;
    private static File projectConfig = null;
    private static String engineVersion = "0.1";
    
    
    private static String getProjectFileName()
    {
        return "project.astro";
    }
    public static void loadProject(String directory)
    {
        File project = new File(directory);
        File config = new File(directory + "\\" + getProjectFileName());
        if(!project.exists())
        {
            throw new RuntimeException("Cannot load project: " + directory);
        }
        if(!config.exists())
        {
            config = generateConfigFile(project);
        }
        projectDirectory = project;
        projectConfig = config;
    }
    public static void createNewProject(File projectFile)
    {
        //Creating the project directory
        if(projectFile.exists())
        {
            throw new RuntimeException("Project file already exists.");
        }
        if(!projectFile.mkdirs())
        {
            throw new RuntimeException("Failed to create project directory.");
        }
        generateConfigFile(projectFile);
    }
    
    private static File generateConfigFile(File projectDirectory)
    {
        //Create the .astro file
        File astro = new File(projectDirectory.getPath() + "\\" + getProjectFileName());
        try
        {
            astro.createNewFile();
        } catch(IOException e)
        {
            throw new RuntimeException("Failed to create .astro file" + e);
        }
        System.out.println(astro.getAbsolutePath());
    
        FileConfig config = FileConfig.of(astro, TomlFormat.instance());
        config.set("Config Version", engineVersion);
        config.save();
        config.close();
        //Fill .astro file
        //.astro version
        //Project Name
        //Main Scene
        //Project Icon
        return astro;
    }
    
    
}
