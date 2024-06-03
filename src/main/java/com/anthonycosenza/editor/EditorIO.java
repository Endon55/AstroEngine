package com.anthonycosenza.editor;

import com.anthonycosenza.editor.scene.SaveType;
import com.anthonycosenza.engine.space.ProjectSettings;
import com.anthonycosenza.engine.util.Toml;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.toml.TomlFormat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EditorIO
{
    private static File projectDirectory = null;
    private static File astroDirectory = null;
    private static File projectConfig = null;
    private static File userDirectory = null;
    private static File projectHistory = null;
    private static String engineVersion = "0.1";
    
    public static void saveNewFile(File file, SaveType saveType)
    {
        //File file = new File(getProjectDirectory().getPath() + "/" + fileName + saveType.getExtension());
        try
        {
            if(file.createNewFile())
            {
            
            }
        } catch(IOException e)
        {
            throw new RuntimeException("Failed to create new file: " + e);
        }
    }
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
    
    private static String getProjectFileName()
    {
        return "project.astro";
    }
    
    public static File getProjectDirectory()
    {
        return projectDirectory;
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
    private static File getUserDirectory()
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
}
