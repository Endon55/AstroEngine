package com.anthonycosenza.editor.scene;

import com.anthonycosenza.engine.space.node.Ignore;
import com.anthonycosenza.engine.space.node.Node;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.toml.TomlWriter;
import org.joml.Vector2f;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class NodeSerializer
{
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
        config.set(propertyPath, node.getClass().getSimpleName());
        propertyPath.remove(propertyPath.size() - 1);
        for(Node child : node.children)
        {
            saveNode(new ArrayList<>(path), config, child);
        }
        
        for(Field field : node.getClass().getDeclaredFields())
        {
            if(field.isAnnotationPresent(Ignore.class));
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

                if(field.getType() == Vector2f.class)
                {
                    fieldValue = "Vector2f(" + ((Vector2f)fieldValue).x() + "," + ((Vector2f) fieldValue).y() + ")";
                }
                if(field.getType() == String.class)
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
        for(Node child: node.children)
        {
            saveNode(new ArrayList<>(path), config, child);
        }
    }
}
