package com.anthonycosenza.editor.scene;

import com.anthonycosenza.engine.space.node.Node;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.core.io.ParsingMode;
import com.electronwill.nightconfig.toml.TomlParser;
import org.joml.Vector2f;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

public class NodeDeSerializer
{
    public static Node deSerialize(File file)
    {
        CommentedConfig config = CommentedConfig.inMemory();
        
        TomlParser reader = new TomlParser();
    
        reader.parse(file, config, ParsingMode.REPLACE, FileNotFoundAction.THROW_ERROR);
        System.out.println(config.valueMap());
        
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
                
                node = parseNode((Config)attribute.getValue(), attribute.getKey(), null);
            }
        }
        return node;
    }
    
    
    private static Node parseNode(Config config, String name, Node parent)
    {
        Map<String, Object> map = config.valueMap();
        
        Node node = null;
    
        String type = (String)map.get("type");
        if(type.equals(Node.class.getSimpleName()))
        {
            node = new Node();
        }
        if(node == null)
        {
            throw new RuntimeException("Cannot identify node type: " + type);
        }
        node.parent = parent;
        if(parent == null)
        {
            parent = node;
        }
        else {
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
}
