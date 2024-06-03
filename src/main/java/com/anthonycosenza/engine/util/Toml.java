package com.anthonycosenza.engine.util;

import com.anthonycosenza.editor.EditorIO;
import com.anthonycosenza.engine.annotations.Property;
import com.anthonycosenza.engine.assets.Asset;
import com.anthonycosenza.engine.assets.AssetInfo;
import com.anthonycosenza.engine.assets.AssetManager;
import com.anthonycosenza.engine.assets.AssetType;
import com.anthonycosenza.engine.space.ProjectSettings;
import com.anthonycosenza.engine.space.node.Node;
import com.anthonycosenza.engine.space.node.Scene;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.core.io.ParsingMode;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.toml.TomlParser;
import com.electronwill.nightconfig.toml.TomlWriter;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Toml
{
    private static final String ASSET = "ASSET_HEADER";
    private static final String PROJECT_SETTINGS = "PROJECT_SETTINGS";
    
    public static void updateScene(Scene scene, String filename)
    {
        new builder().scene(scene, filename).build(filename);
    }
    
    public static void updateScene(Scene scene)
    {
        updateScene(scene, AssetManager.getInstance().getAssetInfo(scene.getResourceID()));
    }
    
    public static void updateScene(Scene scene, AssetInfo info)
    {
        new builder().scene(scene, info).build(info.filePath());
    }
    
    public static void updateAssetHeader(long assetHandle, AssetType assetType, String filePath, File file)
    {
        throw new RuntimeException("Update Asset Header");
        /*
        config.add(ASSET + ".handle", assetHandle);
        config.add(ASSET + ".type", assetType.name());
        config.add(ASSET + ".path", filePath);
        return this;
        */
    }
    public static void updateProjectSettings(ProjectSettings settings)
    {
        new builder().settings(settings).build(EditorIO.getProjectConfig());
    }
    
    public static AssetInfo getAssetHeader(File file)
    {
        CommentedConfig config = CommentedConfig.inMemory();
    
        TomlParser reader = new TomlParser();
    
        reader.parse(file, config, ParsingMode.REPLACE, FileNotFoundAction.THROW_ERROR);
        config = config.get(ASSET);
        return new AssetInfo(config.getLong("handle"), AssetType.valueOf(config.get("type")), config.get("path"));
    }
    
    public static ProjectSettings getProjectSettings(File file)
    {
        CommentedConfig config = CommentedConfig.inMemory();
    
        TomlParser reader = new TomlParser();
    
        reader.parse(file, config, ParsingMode.REPLACE, FileNotFoundAction.THROW_ERROR);
        config = config.get(PROJECT_SETTINGS);
        ProjectSettings settings;
        if(config == null)
        {
            settings = new ProjectSettings();
        }
        else
        {
            settings = parseSettings(config);
        }
        
        return settings;
    }
    
    private static ProjectSettings parseSettings(CommentedConfig config)
    {
        Map<String, Object> map = config.valueMap();
        ProjectSettings settings = new ProjectSettings();
        for(Map.Entry<String, Object> entry : map.entrySet())
        {
            try
            {
                Field field = ProjectSettings.class.getField(entry.getKey());
                field.set(settings, entry.getValue());
            } catch(NoSuchFieldException | IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
        }
        return settings;
    }
    
    public static Scene getScene(AssetInfo info)
    {
        CommentedConfig config = CommentedConfig.inMemory();
        TomlParser reader = new TomlParser();
        File file = new File(info.filePath());
        reader.parse(file, config, ParsingMode.REPLACE, FileNotFoundAction.THROW_ERROR);
        
        Node node = null;
        Map<String, Object> map = config.valueMap();
        for(Map.Entry<String, Object> attribute : map.entrySet())
        {
            if(attribute.getKey().equals(ASSET))
            {
                CommentedConfig asset = (CommentedConfig) attribute.getValue();
                info = new AssetInfo(asset.getLong("handle"), AssetType.valueOf(asset.get("type")), asset.get("path"));
            }
            else if(attribute.getValue() instanceof Config)
            {
                if(node != null) throw new RuntimeException("Multiple nodes in scene file.");
            
                node = parseNode((Config)attribute.getValue(), attribute.getKey(), null);
            }
        }
        return (Scene) node;
    }
    
    public static Node getNode(File file)
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
                Class<? extends Node> nodeClass = node.getClass();
                while(field == null && nodeClass != null && !Object.class.equals(nodeClass))
                {
                    
                    try{
                        field = nodeClass.getField(key);
                    } catch(NoSuchFieldException ignored) { }
                    
                    nodeClass = (Class<? extends Node>) nodeClass.getSuperclass();
                }
                if(field == null)
                {
                    throw new RuntimeException("Couldn't find the parent class the property(" + key + ") belongs to.");
                }
                
                //TODO
                if(value instanceof String)
                {
                    String[] split = ((String) value).split("\\s*[()]");
                    if(split[0].equals("Vector2f"))
                    {
                        String[] values = split[1].split(",");
                        value = new Vector2f(Float.parseFloat(values[0]), Float.parseFloat(values[1]));
                    }
                    else if(split[0].equals("Vector3f"))
                    {
                        String[] values = split[1].split(",");
                        value = new Vector3f(Float.parseFloat(values[0]), Float.parseFloat(values[1]), Float.parseFloat(values[2]));
                    }
                    else if(split[0].equals("Quaternionf"))
                    {
                        String[] values = split[1].split(",");
                        value = new Quaternionf(Float.parseFloat(values[0]), Float.parseFloat(values[1]), Float.parseFloat(values[2]), Float.parseFloat(values[3]));
                    }
                    else if(split[0].equals("String"))
                    {
                        value = split[1];
                    }
                    else if(split[0].equals("Asset"))
                    {
                        value = AssetManager.getInstance().getAsset(Long.parseLong(split[1]));
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
    public static class builder
    {
        private final CommentedConfig config = CommentedConfig.inMemory();
        private AssetInfo info;
    
        public Toml.builder asset(AssetInfo info)
        {
            return asset(info.assetID(), info.assetType(), info.filePath());
        }
        public Toml.builder asset(long assetHandle, AssetType assetType, String filePath)
        {
            config.add(ASSET + ".handle", assetHandle);
            config.add(ASSET + ".type", assetType.name());
            config.add(ASSET + ".path", filePath);
            info = new AssetInfo(assetHandle, assetType, filePath);
            return this;
        }
    
        public Toml.builder scene(Scene scene, AssetInfo info)
        {
            asset(info);
            return node(scene);
        }
    
        public Toml.builder scene(Scene scene, String filePath)
        {
            asset(scene.getResourceID(), AssetType.SCENE, filePath);
            return node(scene);
        }
        public Toml.builder settings(ProjectSettings settings)
        {
            List<String> path = new ArrayList<>(2);
            path.add(PROJECT_SETTINGS);
            for(Field field : ProjectSettings.class.getDeclaredFields())
            {
                path.add(field.getName());
                try
                {
                    config.add(path, field.get(settings));
                } catch(IllegalAccessException e)
                {
                    throw new RuntimeException(e);
                }
                path.remove(1);
            }
            
            return this;
        }
        
        public Toml.builder node(Node node)
        {
            saveNode(new ArrayList<>(), config, node);
            return this;
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
            
            Class<? extends Node> nodeClass = node.getClass();
            while(nodeClass != null && !Object.class.equals(nodeClass))
            {
                List<Field> fields = Arrays.stream(nodeClass.getDeclaredFields())
                        .filter(field -> field.isAnnotationPresent(Property.class) &&
                                !field.getName().equals("parent") &&
                                !field.getName().equals("children") &&
                                !field.getName().equals("name")
                        ).toList();
                for(Field field : fields)
                {
                    field.setAccessible(true);
                    String fieldName = field.getName();
                    propertyPath.add(fieldName);
                    Object fieldValue = null;
                    try
                    {
                        fieldValue = field.get(node);
                        if(fieldValue != null)
                        {
                            if(Asset.class.isAssignableFrom(field.getType()))
                            {
                                System.out.println("asset");
                                fieldValue = "Asset(" + ((Asset) fieldValue).getResourceID() + ")";
                            }
                            else if(field.getType() == Vector2f.class)
                            {
                                fieldValue = "Vector2f(" + ((Vector2f) fieldValue).x() + "," + ((Vector2f) fieldValue).y() + ")";
                            }
                            else if(field.getType() == Vector3f.class)
                            {
                                fieldValue = "Vector3f(" + ((Vector3f) fieldValue).x() + "," + ((Vector3f) fieldValue).y() + "," + ((Vector3f) fieldValue).z() + ")";
                            }
                            else if(field.getType() == Quaternionf.class)
                            {
                                fieldValue = "Quaternionf(" + ((Quaternionf) fieldValue).x() + "," + ((Quaternionf) fieldValue).y() + "," + ((Quaternionf) fieldValue).z() + "," + ((Quaternionf) fieldValue).w() + ")";
                            }
                            else if(field.getType() == String.class)
                            {
                                fieldValue = "String(" + fieldValue + ")";
                            }
                            
                            config.set(propertyPath, fieldValue);
                        }
                        propertyPath.remove(propertyPath.size() - 1);
            
                    } catch(IllegalAccessException e)
                    {
                        throw new RuntimeException(e);
                    }
        
                }
    
                nodeClass = (Class<? extends Node>) nodeClass.getSuperclass();
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
    
        public void build(String destination)
        {
            build(new File(destination));
        }
        public void build(File destination)
        {
            TomlWriter writer = new TomlWriter();
    
            writer.write(config, destination, WritingMode.REPLACE);
            if(info != null)
            {
            
            }
        }
    }
}
