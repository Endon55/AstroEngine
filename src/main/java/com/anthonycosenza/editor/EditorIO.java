package com.anthonycosenza.editor;

import com.anthonycosenza.engine.space.ProjectSettings;
import com.anthonycosenza.engine.util.FileUtils;
import com.anthonycosenza.engine.util.Toml;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.toml.TomlFormat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class EditorIO
{
    private static File projectDirectory = null;
    private static File astroDirectory = null;
    private static File projectConfig = null;
    private static File userDirectory = null;
    private static File tempDirectory = null;
    private static File outDirectory = null;
    private static File projectHistory = null;
    private static String engineVersion = "0.1";
    private static File shaderDirectory = null;
    
    
    public static File getProjectConfig()
    {
        return projectConfig;
    }
    public static ProjectSettings getProjectSettings()
    {
        if(projectConfig.exists())
        {
            return Toml.getProjectSettings(getProjectConfig());
        }
        else
        {
            throw new RuntimeException("Project settings should at least exist here...");
        }
    }
    public static File getShaderDirectory()
    {
       if(shaderDirectory == null)
       {
           shaderDirectory = new File(getProjectDirectory().getPath() + "\\shaders");
           if(!shaderDirectory.exists() && !shaderDirectory.mkdirs())
           {
               throw new RuntimeException("Failed to create Shader directory.");
           }
       }
       return shaderDirectory;
    }
    
    private static String getProjectFileName()
    {
        return "project.astro";
    }
    
    public static File getProjectDirectory()
    {
        return projectDirectory;
    }
    
    public static File getOutDirectory()
    {
        if(outDirectory == null)
        {
            outDirectory = new File(getProjectDirectory().getPath() + "\\out");
        }
        return outDirectory;
    }
    
    public static ProjectSettings loadProjectData(String directory)
    {
        File project = new File(directory);
        File config = new File(directory + "\\" + getProjectFileName());
        if(!project.exists() || !config.exists())
        {
            throw new RuntimeException("Invalid project");
        }
        
        return Toml.getProjectSettings(config);
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
        
        //Update the userDirectory with this project.
        Set<String> projects = new HashSet<>();
        projects.add(directory);
        projects.addAll(getRecentProjects());
        try
        {
            Files.write(getProjectHistory().toPath(), projects);
        } catch(IOException e)
        {
            throw new RuntimeException("Failed to write project history: " + e);
        }
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
        astroDirectory = generateAstroDirectory(projectFile);
    }
    private static File getAstroDirectory()
    {
        if(astroDirectory == null)
        {
            astroDirectory = generateAstroDirectory(projectDirectory);
        }
        
        return astroDirectory;
    }
    
    public static File getGuiINI()
    {
        return new File(getAstroDirectory().getPath() + "\\editor.ini");
    }
    
    
    private static File generateAstroDirectory(File projectDirectory)
    {
        File astroDirectory = new File(projectDirectory.getPath() + "\\.astro");
        
        if(!astroDirectory.exists() && !astroDirectory.mkdirs())
        {
            throw new RuntimeException("Failed to create .astro folder within project.");
        }
        
        return astroDirectory;
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
    public static File getAssetDirectory()
    {
        File file = new File(getProjectDirectory().getPath());
        if(!file.exists())
        {
            file.mkdirs();
        }
        return file;
    }
    public static File getUserDirectory()
    {
        if(userDirectory == null)
        {
            userDirectory = new File(System.getProperty("user.home") + "\\.astro");
            if(!userDirectory.exists())
            {
                if(!userDirectory.mkdirs())
                {
                    throw new RuntimeException("Failed to create User Folder.");
                }
            }
        }
        return userDirectory;
    }
    
    public static File getTempDirectory()
    {
        if(tempDirectory == null)
        {
            tempDirectory = new File(getUserDirectory().getPath() + "\\tmp");
            if(!tempDirectory.exists())
            {
                if(!tempDirectory.mkdirs())
                {
                    throw new RuntimeException("Failed to create Temp User Folder.");
                }
            }
        }
        return tempDirectory;
    }
    
    
    private static File getProjectHistory()
    {
        projectHistory = new File(getUserDirectory().getPath() + "\\projects.txt");
        try
        {
            projectHistory.createNewFile();
        } catch(IOException e)
        {
            throw new RuntimeException("Failed to create Project History File: " + e);
        }
        return projectHistory;
    }
    
    public static List<String> getRecentProjects()
    {
        try
        {
            return Files.readAllLines(getProjectHistory().toPath());
        } catch(IOException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static List<File> getAllProjectScripts()
    {
        return getAllProjectFilesRecursive((file) ->
            FileUtils.getExtension(file).equals("java"));
    }
    
    public static List<File> getAllProjectFilesRecursive(FilterFunc filterFunc)
    {
        List<File> files = new ArrayList<>();
        Stack<File> stack = new Stack<>();
        stack.add(getProjectDirectory());
        while(!stack.isEmpty())
        {
            File file = stack.pop();
            
            if(file.isDirectory())
            {
                File[] children = file.listFiles();
                if(children != null && children.length > 0)
                {
                    for(int i = 0; i < children.length; i++)
                    {
                        stack.add(children[i]);
                    }
                }
            }
            else if(filterFunc.filter(file))
            {
                files.add(file);
            }
        }
        return files;
    }
    
    private interface FilterFunc
    {
        boolean filter(File file);
    }
    
}
