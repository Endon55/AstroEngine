package com.anthonycosenza.editor;

import com.anthonycosenza.editor.scene.SaveType;
import com.anthonycosenza.engine.annotations.Ignore;
import com.anthonycosenza.engine.assets.Asset;
import com.anthonycosenza.engine.assets.AssetManager;
import com.anthonycosenza.engine.space.node.Node;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.core.io.ParsingMode;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.electronwill.nightconfig.toml.TomlParser;
import com.electronwill.nightconfig.toml.TomlWriter;
import org.joml.Vector2f;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    
    private static String getProjectFileName()
    {
        return "project.astro";
    }
    
    public static File getProjectDirectory()
    {
        return projectDirectory;
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
    
    public static Node deserialize(File file)
    {
        CommentedConfig config = CommentedConfig.inMemory();
        
        TomlParser reader = new TomlParser();
        
        reader.parse(file, config, ParsingMode.REPLACE, FileNotFoundAction.THROW_ERROR);
        
        return parse(config);
    }
    
    private static Node parse(CommentedConfig config)
    {
        Node node = null;
        Map<String, Object> map = config.valueMap();
        for(Map.Entry<String, Object> attribute : map.entrySet())
        {
            if(attribute.getValue() instanceof Config)
            {
                if(node != null) throw new RuntimeException("Multiple nodes in scene file.");
                
                node = parseNode((Config) attribute.getValue(), attribute.getKey(), null);
            }
        }
        return node;
    }
    
    
    private static Node parseNode(Config config, String name, Node parent)
    {
        Map<String, Object> map = config.valueMap();
        
        Node node = null;
        String type = (String) map.get("type");
        Class<? extends Node> clazz;
        try
        {
            clazz = (Class<? extends Node>) Class.forName(type);
            node = clazz.getConstructor().newInstance();
        } catch(ClassNotFoundException e)
        {
            throw new RuntimeException("Cannot get class: " + type + " - " + e);
        } catch(InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e)
        {
            throw new RuntimeException("Cannot instantiate class: " + type + " - " + e);
        }
        
        node.parent = parent;
        if(parent == null)
        {
            parent = node;
        }
        else
        {
            parent.children.add(node);
        }
        
        node.name = name;
        
        for(Map.Entry<String, Object> attribute : map.entrySet())
        {
            String key = attribute.getKey();
            Object value = attribute.getValue();
            
            if(key.equals("type"))
            {
                continue;
            }
            
            if(value instanceof Config)
            {
                parseNode((Config) value, key, node);
            }
            else
            {
                Field field = null;
                try
                {
                    field = node.getClass().getField(key);
                } catch(NoSuchFieldException e)
                {
                    throw new RuntimeException(e);
                }
                
                
                if(value instanceof String)
                {
                    String[] split = ((String) value).split("\\s*[()]");
                    if(split[0].equals("Vector2f"))
                    {
                        String[] values = split[1].split(",");
                        value = new Vector2f(Float.parseFloat(values[0]), Float.parseFloat(values[1]));
                    }
                    else if(split[0].equals("String"))
                    {
                        value = split[1];
                    }
                    else if(split[0].equals("Asset"))
                    {
                        value = AssetManager.getAsset(Long.parseLong(split[1]));
                    }
                }
                
                
                try
                {
                    field.set(node, value);
                } catch(IllegalAccessException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
        return parent;
    }
    
    public static void serialize(File file, Node node)
    {
        CommentedConfig config = CommentedConfig.inMemory();
        
        saveNode(new ArrayList<>(), config, node);
        TomlWriter writer = new TomlWriter();
        
        writer.write(config, file, WritingMode.REPLACE);
        
    }
    
    private static void saveNode(List<String> path, CommentedConfig config, Node node)
    {
        path.add(node.name);
        List<String> propertyPath = new ArrayList<>(path);
        propertyPath.add("type");
        config.set(propertyPath, node.getClass().getName());
        propertyPath.remove(propertyPath.size() - 1);
        for(Node child : node.children)
        {
            saveNode(new ArrayList<>(path), config, child);
        }
        
        for(Field field : node.getClass().getDeclaredFields())
        {
            if(field.isAnnotationPresent(Ignore.class))
            {
                continue;
            }
            
            String fieldName = field.getName();
            if(fieldName.equals("children") || fieldName.equals("name"))
            {
                continue;
            }
            propertyPath.add(fieldName);
            Object fieldValue = null;
            try
            {
                fieldValue = field.get(node);
                if(Asset.class.isAssignableFrom(fieldValue.getClass()))
                {
                    fieldValue = "Asset(" + ((Asset) fieldValue).getResourceID() + ")";
                }
                else if(field.getType() == Vector2f.class)
                {
                    fieldValue = "Vector2f(" + ((Vector2f) fieldValue).x() + "," + ((Vector2f) fieldValue).y() + ")";
                }
                else if(field.getType() == String.class)
                {
                    fieldValue = "String(" + fieldValue + ")";
                }
                config.set(propertyPath, fieldValue);
                propertyPath.remove(propertyPath.size() - 1);
                
            } catch(IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
            
        }
        
        
        
        /*for(Map.Entry<String, Object> property : node.properties.entrySet())
        {
            List<String> propertyPath = new ArrayList<>(path);
            propertyPath.add(property.getKey());
            config.set(propertyPath, property.getValue());
        }*/
        for(Node child : node.children)
        {
            saveNode(new ArrayList<>(path), config, child);
        }
    }
}
