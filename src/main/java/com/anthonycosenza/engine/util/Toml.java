package com.anthonycosenza.engine.util;

import com.anthonycosenza.editor.EditorIO;
import com.anthonycosenza.editor.logger.EditorLogger;
import com.anthonycosenza.editor.scripts.ScriptCompiler;
import com.anthonycosenza.engine.assets.Asset;
import com.anthonycosenza.engine.assets.AssetInfo;
import com.anthonycosenza.engine.assets.AssetManager;
import com.anthonycosenza.engine.assets.AssetType;
import com.anthonycosenza.engine.compute.Execution;
import com.anthonycosenza.engine.space.node.Camera;
import com.anthonycosenza.engine.space.ProjectSettings;
import com.anthonycosenza.engine.space.node.Node;
import com.anthonycosenza.engine.space.node.Scene;
import com.anthonycosenza.engine.space.rendering.materials.StandardMaterial;
import com.anthonycosenza.engine.util.math.Color;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.core.io.ParsingMode;
import com.electronwill.nightconfig.core.io.WritingException;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.toml.TomlParser;
import com.electronwill.nightconfig.toml.TomlWriter;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Toml
{
    private static final String ASSET = "ASSET_HEADER";
    private static final String PROJECT_SETTINGS = "PROJECT_SETTINGS";
    private static final String CAMERA = "CAMERA";
    
    public static void updateAsset(AssetInfo info, Asset asset, String filename)
    {
        new builder().asset(info, asset).build(filename);
    }
    public static void updateCamera(Camera camera, File file)
    {
        new builder().load(file).camera(camera).build(file);
    }
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
        new builder().load(EditorIO.getProjectConfig()).settings(settings).build(EditorIO.getProjectConfig());
    }
    
    public static AssetInfo getAssetHeader(File file)
    {
        CommentedConfig config = CommentedConfig.inMemory();
    
        TomlParser reader = new TomlParser();
    
        reader.parse(file, config, ParsingMode.REPLACE, FileNotFoundAction.CREATE_EMPTY);
        if(config.isEmpty())
        {
            throw new RuntimeException("Failed to read config: " + file);
        }
        config = config.get(ASSET);
        if(config == null)
        {
            return null;
        }
        else return new AssetInfo(config.getLong("handle"), AssetType.valueOf(config.get("type")), config.get("path"));
    }
    public static Camera getCamera(File file)
    {
        CommentedConfig config = CommentedConfig.inMemory();
    
        TomlParser reader = new TomlParser();
    
        reader.parse(file, config, ParsingMode.REPLACE, FileNotFoundAction.THROW_ERROR);
        config = config.get(CAMERA);
        
        Camera camera;
        if(config == null)
        {
            camera = null;
        }
        else
        {
            camera = parseCamera(config);
        }
    
        return camera;
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
            if(entry.getKey().equals("type")) continue;
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
    
    private static Camera parseCamera(CommentedConfig config)
    {
        Map<String, Object> map = config.valueMap();
        Camera camera;
        try
        {
            camera = (Camera) getClass(config.get("type")).getConstructor().newInstance();
            
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }
        for(Map.Entry<String, Object> entry : map.entrySet())
        {
            if(entry.getKey().equals("type")) continue;
            try
            {
                Field field = ClassUtils.getFieldInclSuper(camera.getClass(), entry.getKey());
                if(field == null) throw new NoSuchFieldException();
                field.setAccessible(true);
                field.set(camera, deserializeValue(field.getType(), entry.getValue(), null));
            } catch(NoSuchFieldException | IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
        }
        return camera;
    }
    
    public static Scene getScene(AssetInfo info)
    {
        CommentedConfig config = CommentedConfig.inMemory();
        TomlParser reader = new TomlParser();
        File file = new File(info.filePath());
        if(!file.exists())
        {
            return null;
        }
        reader.parse(file, config, ParsingMode.REPLACE, FileNotFoundAction.THROW_ERROR);
        
        Map<String, Object> map = config.valueMap();
        Map<Long, Asset> assetRefs = new HashMap<>();
        Map.Entry<String, Object> nodeEntry = null;
        for(Map.Entry<String, Object> attribute : map.entrySet())
        {
            if(attribute.getKey().equals(ASSET))
            {
                CommentedConfig asset = (CommentedConfig) attribute.getValue();
                info = new AssetInfo(asset.getLong("handle"), AssetType.valueOf(asset.get("type")), asset.get("path"));
            }
            else if(attribute.getKey().startsWith("ref"))
            {
                String refID = attribute.getKey().split("_")[1];
                assetRefs.put(Long.parseLong(refID), (Asset)parseObject((CommentedConfig) attribute.getValue(), assetRefs));
            }
            else if(attribute.getValue() instanceof Config)
            {
                if(nodeEntry != null) throw new RuntimeException("Multiple nodes in scene file.");
                nodeEntry = attribute;
                
            }
        }
    
        Node node;
        if(nodeEntry != null)
        {
            node = parseNode((Config) nodeEntry.getValue(), nodeEntry.getKey(), null, assetRefs);
        }
        else
        {
            throw new RuntimeException("Couldn't find scene entry node.");
        }
        
        return (Scene) node;
    }
    private static Class<?> getClass(String type)
    {
        if(type.startsWith("Script"))
        {
            String[] split = ((String) type).split("\\s*[()]");
            return ScriptCompiler.load(split[1]);
            
        }
        else
        {
            try
            {
                return Class.forName(type);
            } catch(ClassNotFoundException e)
            {
                throw new RuntimeException(e);
            }
        }
    }
    public static Object parseObject(CommentedConfig config, Map<Long, Asset> assets)
    {
        try
        {
            Class<?> clazz = getClass(config.get("type"));
            
            Object object = clazz.getDeclaredConstructor().newInstance();
            for(Map.Entry<String, Object> entry : config.valueMap().entrySet())
            {
                if(entry.getKey().equals("type")) continue;
                
                Field field = ClassUtils.getFieldInclSuper(object.getClass(), entry.getKey());
                field.setAccessible(true);
                field.set(object, deserializeValue(field.getType(), entry.getValue(), assets));
            }
    
            return object;
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                NoSuchFieldException e)
        {
            throw new RuntimeException(e);
        }
    
    }
    
    private static Node parseNode(Config config, String name, Node parent, Map<Long, Asset> assets)
    {
        Map<String, Object> map = config.valueMap();
        
        Node node = null;
        String type = (String) map.get("type");
        Class<? extends Node> clazz;
        try
        {
            clazz = (Class<? extends Node>) getClass(type);
            node = clazz.getConstructor().newInstance();
        } catch(InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e)
        {
            throw new RuntimeException("Cannot instantiate class: " + type + " - " + e);
        }
        node.setParent(parent);
        if(parent == null)
        {
            parent = node;
        }
        else
        {
            parent.addChild(node);
        }
        
        node.setName(name);
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
                parseNode((Config) value, key, node, assets);
            }
            else
            {
                Field field = null;
                Class<? extends Node> nodeClass = node.getClass();
                
                
                try
                {
                    field = ClassUtils.getFieldInclSuper(nodeClass, key);
                    
                    field.setAccessible(true);
                    field.set(node, deserializeValue(field.getType(), value, assets));
                } catch(IllegalAccessException | NoSuchFieldException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
        return parent;
    }


    
    public static Object deserializeValue(Class<?> type, Object value, Map<Long, Asset> assets)
    {
        if(value instanceof String valueStr)
        {
            String[] split = valueStr.split("\\s*[()]");
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
            else if(split[0].equals("Color"))
            {
                String[] values = split[1].split(",");
                value = new Color(Float.parseFloat(values[0]), Float.parseFloat(values[1]), Float.parseFloat(values[2]), Float.parseFloat(values[3]));
            }
            else if(split[0].equals("StandardMaterial"))
            {
                String[] values = split[2].split(",");
                StandardMaterial material = new StandardMaterial();
                material.setDiffuseColor(Float.parseFloat(values[0]), Float.parseFloat(values[1]), Float.parseFloat(values[2]), Float.parseFloat(values[3]));
                value = material;
            }
            else if(split[0].equals("String"))
            {
                value = split[1];
            }
            else if(split[0].equals("Asset"))
            {
                value = AssetManager.getInstance().getAsset(Long.parseLong(split[1]));
            }
            else if(split[0].equals("AssetRef"))
            {
                //ref=resourceID
                value = assets.get(Long.parseLong(split[1]));
            }
            else if(split[0].equals("Script"))
            {
                value = ClassUtils.instantiate(ScriptCompiler.load(split[1]));
            }
            else if(split[0].equals("List"))
            {
                String[] elements = valueStr.substring("List(".length(), valueStr.length() - 1).split(" ");
                List list = new ArrayList<>();
                for(String element : elements)
                {
                    list.add(deserializeValue(null, element, null));
                }
                value = list;
            }
            else if(split[0].equals("Array"))
            {
                String[] elements = valueStr.substring("List(".length(), valueStr.length() - 1).split(" ");
                Object[] array = new Object[elements.length];
                for(int i = 0; i < array.length; i++)
                {
                    array[i] = deserializeValue(null, array[i], null);
                }
                value = array;
            }
        }
        else if(short.class.equals(type) || Short.class.equals(type))
        {
            value = ((Number) value).shortValue();
        }
        else if(int.class.equals(type) || Integer.class.equals(type))
        {
            value = ((Number) value).intValue();
        }
        else if(long.class.equals(type) || Long.class.equals(type))
        {
            value = ((Number) value).longValue();
        }
        else if(float.class.equals(type) || Float.class.equals(type))
        {
            value = ((Number) value).floatValue();
        }
        else if(double.class.equals(type) || Double.class.equals(type))
        {
            value = ((Number) value).doubleValue();
        }
        return value;
    }
    
    
    public static class builder
    {
        private final CommentedConfig config = CommentedConfig.inMemory();
        private int refID = 10000;
    
        /*
         * Path should include the destination for this objects fields.
         */
        public Toml.builder serializeObject(List<String> path, Object object)
        {
            return serializeObject(ClassUtils.getAllFieldsInclSuper(object.getClass()).stream()
                    .filter(field -> !Modifier.isTransient(field.getModifiers()) &&
                            !Modifier.isStatic(field.getModifiers())).toList(),
                    path, object);
        }
        
        public Toml.builder serializeObject(List<Field> fields, List<String> path, Object object)
        {
            int lastItem = path.size();
            type(path, object);
            for(Field field : fields)
            {
                field.setAccessible(true);
                
                Object value = null;
                try
                {
                    Object fieldValue = field.get(object);
                    if(fieldValue == null) continue;
                    
                    path.set(lastItem, field.getName());
                    value = serializeValue(fieldValue.getClass(), fieldValue);
                    
                } catch(IllegalAccessException e)
                {
                    throw new RuntimeException(e);
                }
                if(value != null)
                {
                    config.set(path, value);
                }
            }
            path.remove(path.size() - 1);
            return this;
        }
    
        private Object serializeValue(Class<?> clazz, Object fieldValue)
        {
            Object serializedValue = fieldValue;
            if(fieldValue != null)
            {
                if(Asset.class.isAssignableFrom(clazz))
                {
                    long resourceID = ((Asset) fieldValue).getResourceID();
                    AssetInfo info = AssetManager.getInstance().getAssetInfo(resourceID);
                    if(info == null)
                    {
                        refID++;
                        serializedValue = "AssetRef(" + refID + ")";
                        assetRef(refID, ((Asset) fieldValue));
                    }
                    else serializedValue = "Asset(" + resourceID + ")";
                }
                else if(Execution.class.isAssignableFrom(clazz))
                {
                    serializedValue = "Script(" + clazz.getSimpleName() + ")";
                }
                else if(clazz == Vector2f.class)
                {
                    serializedValue = "Vector2f(" + ((Vector2f) fieldValue).x() + "," + ((Vector2f) fieldValue).y() + ")";
                }
                else if(clazz == Vector3f.class)
                {
                    serializedValue = "Vector3f(" + ((Vector3f) fieldValue).x() + "," + ((Vector3f) fieldValue).y() + "," + ((Vector3f) fieldValue).z() + ")";
                }
                else if(clazz == Quaternionf.class)
                {
                    serializedValue = "Quaternionf(" + ((Quaternionf) fieldValue).x() + "," + ((Quaternionf) fieldValue).y() + "," + ((Quaternionf) fieldValue).z() + "," + ((Quaternionf) fieldValue).w() + ")";
                }
                else if(clazz == Color.class)
                {
                    serializedValue = "Color(" + ((Color) fieldValue).r() + "," + ((Color) fieldValue).g() + "," + ((Color) fieldValue).b() + "," + ((Color) fieldValue).a() + ")";
                }
                else if(clazz == String.class)
                {
                    serializedValue = "String(" + fieldValue + ")";
                }
            }
            return serializedValue;
        }
        public Toml.builder asset(AssetInfo info, Asset asset)
        {
            if(asset instanceof Scene scene) return scene(scene, info);
            assetHeader(info);
            
            List<String> path = new ArrayList<>(2);
            path.add(asset.getClass().getSimpleName());
            type(path, asset);
            path.remove(path.size() - 1);
            
            serializeObject(path, asset);
            return this;
        }
        public Toml.builder type(List<String> path, Object node)
        {
            path.add("type");
            String name = node.getClass().getName();
            if(name.startsWith("com.anthonycosenza"))
            {
                config.set(path, name);
            }
            else //It's a user defined class
            {
                config.set(path, "Script(" + name + ")");
            }
            return this;
        }
        public Toml.builder camera(Camera camera)
        {
            List<String> path = new ArrayList<>();
            path.add(CAMERA);
            type(path, camera);
            for(Field field : ClassUtils.getAllFields(Camera.class))
            {
                path.set(1, field.getName());
                try
                {
                    config.set(path, serializeValue(field.getType(), field.get(camera)));
                } catch(IllegalAccessException e)
                {
                    throw new RuntimeException(e);
                }
            }
            return this;
        }
        
        public Toml.builder assetHeader(AssetInfo info)
        {
            return assetHeader(info.assetID(), info.assetType(), info.filePath());
        }
        public Toml.builder assetHeader(long assetHandle, AssetType assetType, String filePath)
        {
            config.add(ASSET + ".handle", assetHandle);
            config.add(ASSET + ".type", assetType.name());
            config.add(ASSET + ".path", filePath);
            return this;
        }
    
        private Toml.builder assetRef(long refID, Asset asset)
        {
            serializeObject(new ArrayList<>(List.of("ref_" + refID)), asset);
            
            return this;
        }
    
        public Toml.builder scene(Scene scene, AssetInfo info)
        {
            return scene(scene, info.filePath());
        }
    
        public Toml.builder scene(Scene scene, String filePath)
        {
            assetHeader(scene.getResourceID(), AssetType.SCENE, filePath);
            List<String> path = new ArrayList<>();
            path.add(scene.getName());
            path.add("executions");
            list(path, scene.getExecutions());
            path.clear();
            return node(path, scene);
        }
        
        public Toml.builder list(List<String> path, List list)
        {
            if(list.isEmpty()) return this;
            StringBuilder listString = new StringBuilder();
            listString.append("List(");
            for(int i = 0; i < list.size(); i++)
            {
                if(i != 0)
                {
                    listString.append(" ");
                }
                Object object = list.get(i);
                listString.append(serializeValue(object.getClass(), object));
            }
            listString.append(")");
            
            config.set(path, listString.toString());
            return this;
        }
    
        public Toml.builder array(List<String> path, Object[] array)
        {
            if(array.length == 0) return this;
            StringBuilder arrayString = new StringBuilder();
            arrayString.append("Array(");
            for(int i = 0; i < array.length; i++)
            {
                if(i != 0)
                {
                    arrayString.append(" ");
                }
                Object object = array[i];
                arrayString.append(serializeValue(object.getClass(), object));
            }
            arrayString.append(")");
            config.set(path, arrayString.toString());
            return this;
        }
        
        public Toml.builder settings(ProjectSettings settings)
        {
            List<String> path = new ArrayList<>(2);
            path.add(PROJECT_SETTINGS);
            
            serializeObject(path, settings);
            
            return this;
        }
    
        public Toml.builder node(Node node)
        {
            saveNode(new ArrayList<>(), node);
            return this;
        }
        public Toml.builder node(List<String> path, Node node)
        {
            saveNode(path, node);
            return this;
        }
        
        private void saveNode(List<String> path, Node node)
        {
            path.add(node.getName());
    
            for(Node child : node.getChildren())
            {
                saveNode(new ArrayList<>(path), child);
            }
            
            List<Field> fields = ClassUtils.getAllFieldsInclSuper(node.getClass()).stream()
                    .filter(field -> !Modifier.isTransient(field.getModifiers()) &&
                            !field.getName().equals("parent") &&
                            !field.getName().equals("children") &&
                            !field.getName().equals("name") &&
                            !field.getName().equals("executions")
                    ).toList();
            
            serializeObject(fields, path, node);

            
            path.remove(path.size() - 1);
        }
    
        public Toml.builder load(File file)
        {
            TomlParser reader = new TomlParser();
            if(file.exists())
            {
                reader.parse(file, config, ParsingMode.REPLACE, FileNotFoundAction.THROW_ERROR);
            }
            return this;
        }
        
        public void build(String destination)
        {
            build(new File(destination));
        }
        public void build(File destination)
        {
            TomlWriter writer = new TomlWriter();
            try
            {
                writer.write(config, destination, WritingMode.REPLACE);
            }catch(WritingException e)
            {
                EditorLogger.error("Toml Build Failure: " + e);
                System.out.println(e);
            }
        }
    }
}
