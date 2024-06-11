package com.anthonycosenza.editor.scene.nodes;

import com.anthonycosenza.editor.logger.EditorLogger;
import com.anthonycosenza.engine.assets.Asset;
import com.anthonycosenza.engine.assets.AssetInfo;
import com.anthonycosenza.engine.assets.AssetManager;
import com.anthonycosenza.engine.assets.AssetType;
import com.anthonycosenza.engine.space.entity.Mesh;
import com.anthonycosenza.engine.space.entity.Model;
import com.anthonycosenza.engine.space.entity.PlaneMesh;
import com.anthonycosenza.engine.space.rendering.materials.Material;
import com.anthonycosenza.engine.space.rendering.materials.ShaderMaterial;
import com.anthonycosenza.engine.space.rendering.materials.StandardMaterial;
import com.anthonycosenza.engine.space.rendering.shader.FragmentShader;
import com.anthonycosenza.engine.space.rendering.shader.Shader;
import com.anthonycosenza.engine.space.rendering.shader.VertexShader;
import com.anthonycosenza.engine.util.ImGuiUtils;
import com.anthonycosenza.engine.util.math.Color;
import imgui.ImGui;
import imgui.flag.ImGuiDataType;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTableBgTarget;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.type.ImBoolean;
import imgui.type.ImDouble;
import imgui.type.ImFloat;
import imgui.type.ImInt;
import imgui.type.ImLong;
import imgui.type.ImString;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class EditorProperty
{
    private static int COLUMN_COUNT = 2;
    private static int MAX_STRING_FIELD_LENGTH = 100;
    
    public static void createMeshDropdown()
    {
    
    }
    
    public static Mesh createNewMesh(AssetType type)
    {
        String typeStr = type.name().toLowerCase();
        String text = "Create New " + typeStr;
        Mesh mesh = null;
        
        if(ImGui.beginCombo("##-" + typeStr + " Dropdown", text))
        {
            if(ImGui.selectable("New " + "PlaneMesh"))
            {
                mesh = new PlaneMesh();
            }
            ImGui.endCombo();
        }
        return mesh;
    }
    
    public static String createNew(Asset asset, AssetType assetType, String...types)
    {
        String typeStr = assetType.name().toLowerCase();
        String text;
        String selectedType = null;
        if(asset == null) text = "Create New " + typeStr;
        else
        {
            AssetInfo info = AssetManager.getInstance().getAssetInfo(asset.getResourceID());
            if(info == null)
            {
                text = asset.getClass().getSimpleName();
            }
            else
            {
                text = new File(AssetManager.getInstance().getAssetInfo(asset.getResourceID()).filePath()).getName();
            }
            
        }
        if(ImGui.beginCombo("##-" + typeStr + " Dropdown", text))
        {
            for(String type: types)
            {
                if(ImGui.selectable("New " + type))
                {
                    selectedType = type;
                }
            }
            ImGui.endCombo();
        }
        return selectedType;
    }
    
    public static boolean createNew(AssetType type, Asset asset)
    {
        String typeStr = type.name().toLowerCase();
        String text;
        boolean create = false;
        if(asset == null) text = "Create New " + typeStr;
        else
        {
            AssetInfo info = AssetManager.getInstance().getAssetInfo(asset.getResourceID());
            if(info == null)
            {
                text = String.valueOf(asset.getResourceID());
            }
            else
            {
                text = new File(AssetManager.getInstance().getAssetInfo(asset.getResourceID()).filePath()).getName();
            }
            
        }
        if(ImGui.beginCombo("##-" + typeStr + " Dropdown", text))
        {
            if(ImGui.selectable("New " + typeStr))
            {
                create = true;
            }
            ImGui.endCombo();
        }
        return create;
    }
    
    public static void propertyTable(Object object, ImBoolean modified, Color tableBgColor)
    {
        propertyTable(object.getClass(), object, modified, tableBgColor);
    }
    
    public static void propertyTable(Class<?> clazz, Object object, ImBoolean modified, Color tableBgColor)
    {
        ImGui.pushStyleVar(ImGuiStyleVar.CellPadding, COLUMN_COUNT, 1);
        List<Field> fields = Arrays.stream(clazz.getDeclaredFields()).filter(
                field -> !Modifier.isTransient(field.getModifiers()) &&
                        !Modifier.isStatic(field.getModifiers()) &&
                        !field.getName().equals("resourceID") && !field.getName().equals("children") && !field.getName().equals("parent")).toList();
    
        float longestName = 0;
    
        for(Field item : fields)
        {
            float length = ImGui.calcTextSize(item.getName()).x;
            if(length > longestName) longestName = length;
        }
    
        boolean recreateTable = true;
        for(int i = 0; i < fields.size(); i++)
        {
            if(recreateTable)
            {
                if(ImGui.beginTable("##Property Table - " + clazz.getSimpleName(), COLUMN_COUNT,
                        ImGuiTableFlags.SizingFixedFit | ImGuiTableFlags.NoHostExtendX))
                {
                    ImGui.tableSetupColumn("## property column", ImGuiTableColumnFlags.WidthFixed, longestName);
                    ImGui.tableSetupColumn("## value column", ImGuiTableColumnFlags.WidthStretch);
                    recreateTable = false;
                }
                else return;
            }
    
            Field field = fields.get(i);
            field.setAccessible(true);
    
    
            ImGui.tableNextRow();
            ImGui.tableSetBgColor(ImGuiTableBgTarget.RowBg0, tableBgColor.getInt());
            
            ImGui.tableSetColumnIndex(0);
            ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
            ImGui.text(field.getName());
            
            ImGui.tableSetColumnIndex(1);
            ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
            try
            {
                Object fieldValue = (object != null ? field.get(object) : null);
                Class<?> fieldClass = (fieldValue == null ? field.getType() : fieldValue.getClass());
                //Dropdown combo box.
                Object value = EditorProperty.createInputField(field.getType(), field.getName(), fieldValue, modified);
                
                if(Asset.class.isAssignableFrom(fieldClass))
                {
                    ImGui.endTable();
                    //Asset fields
                    
                    propertyTable(fieldClass, fieldValue, modified, new Color(tableBgColor).mult(2));
                    
                    recreateTable = true;
                }
                if(object != null)
                {
                    field.set(object, value);
                }
        
            } catch(IllegalAccessException e)
            {
                EditorLogger.log(e);
            }
        }
        if(!recreateTable) ImGui.endTable();
        ImGui.popStyleVar();
    }
    
    
    public static Object createInputField(Class<?> fieldType, String fieldName, Object fieldValue, ImBoolean modified)
    {
        if(fieldName.equals("resourceID"))
        {
            ImGui.text(String.valueOf(((long) fieldValue)));
        }
        else if(short.class.equals(fieldType) || Short.class.equals(fieldType))
        {
            if(fieldValue == null)
            {
                fieldValue = 0;
            }
            ImInt imValue = new ImInt((int) fieldValue);
            if(ImGui.inputInt("##" + fieldName, imValue))
            {
                fieldValue = imValue.get();
                modified.set(true);
            }
        }
        else if(int.class.equals(fieldType) || Integer.class.equals(fieldType))
        {
            if(fieldValue == null)
            {
                fieldValue = 0;
            }
            ImInt imValue = new ImInt((Integer) fieldValue);
            if(ImGui.inputInt("##" + fieldName, imValue))
            {
                fieldValue = imValue.get();
                modified.set(true);
            }
        }
        else if(long.class.equals(fieldType) || Long.class.equals(fieldType))
        {
            //ImGui doesn't directly support longs, hopefully this doesn't cause problems lol.
            if(fieldValue == null)
            {
                fieldValue = 0L;
            }
            ImLong imValue = new ImLong((long) fieldValue);
            if(ImGui.inputScalar("##" + fieldName, ImGuiDataType.S64, imValue))
            {
                fieldValue = imValue.get();
                modified.set(true);
            }
        }
        else if(float.class.equals(fieldType) || Float.class.equals(fieldType))
        {
            if(fieldValue == null)
            {
                fieldValue = 0f;
            }
            ImFloat imValue = new ImFloat((Float) fieldValue);
            if(ImGui.inputFloat("##" + fieldName, imValue))
            {
                fieldValue = imValue.get();
                modified.set(true);
            }
        }
        else if(double.class.equals(fieldType) || Double.class.equals(fieldType))
        {
            if(fieldValue == null)
            {
                fieldValue = 0d;
            }
            ImDouble imValue = new ImDouble((Double) fieldValue);
            if(ImGui.inputDouble("##" + fieldName, imValue))
            {
                fieldValue = imValue.get();
                modified.set(true);
            }
        }
        else if(Vector3f.class.equals(fieldType))
        {
            float[] imValue = new float[3];
            Vector3f vector;
            if(fieldValue != null)
            {
                vector = ((Vector3f) fieldValue);
                imValue[0] = vector.x();
                imValue[1] = vector.y();
                imValue[2] = vector.z();
            }
            else
            {
                vector = new Vector3f();
                fieldValue = vector;
            }
            if(ImGui.inputFloat3("##" + fieldName, imValue))
            {
                vector.set(imValue[0], imValue[1], imValue[2]);
                modified.set(true);
            }
        }
        else if(Quaternionf.class.equals(fieldType))
        {
            float[] imValue = new float[3];
            Quaternionf quaternion;
            Vector3f euler;
            if(fieldValue != null)
            {
                quaternion = ((Quaternionf) fieldValue);
                euler = quaternion.getEulerAnglesXYZ(new Vector3f());
                euler.mul((float) (180f / Math.PI));
                imValue[0] = euler.x();
                imValue[1] = euler.y();
                imValue[2] = euler.z();
            }
            else
            {
                quaternion = new Quaternionf();
                euler = quaternion.getEulerAnglesXYZ(new Vector3f());
                fieldValue = quaternion;
            }
            if(ImGui.inputFloat3("##" + fieldName, imValue,"%.3f",  ImGuiInputTextFlags.EnterReturnsTrue))
            {
                if(euler.x() != imValue[0])
                {
                    quaternion.rotateX((float) Math.toRadians(imValue[0] - euler.x()));
                }
                if(euler.y() != imValue[1])
                {
                    quaternion.rotateY((float) Math.toRadians(imValue[1] - euler.y()));
                }
                if(euler.z() != imValue[2])
                {
                    quaternion.rotateZ((float) Math.toRadians(imValue[2] - euler.z()));
                }

                modified.set(true);
            }
        }
        else if(String.class.equals(fieldType))
        {
            if(fieldValue == null)
            {
                fieldValue = "";
            }
            //Pre-allocating the String buffer, otherwise the buffers size is limited to the length of whatever was first added to it.
            ImString imValue = new ImString(MAX_STRING_FIELD_LENGTH);
            imValue.set(fieldValue);
            if(ImGui.inputText("##" + fieldName, imValue))
            {
                if(ImGui.isItemDeactivatedAfterEdit())
                {
                    fieldValue = imValue.get();
                    modified.set(true);
                }
            }
        }
        else if(Color.class.equals(fieldType))
        {
            float[] imValue = new float[4];
            Color color;
            if(fieldValue != null)
            {
                color = ((Color) fieldValue);
                imValue[0] = color.r();
                imValue[1] = color.g();
                imValue[2] = color.b();
                imValue[3] = color.a();
            }
            else
            {
                color = new Color();
                fieldValue = color;
            }
            if(ImGui.colorPicker4("##" + fieldName, imValue))
            {
                color.set(imValue[0], imValue[1], imValue[2], imValue[3]);
                modified.set(true);
            }
        }
        else if(Mesh.class.equals(fieldType))
        {
            AssetType type = AssetType.MESH;
            ImGui.sameLine();
    
            String selected = EditorProperty.createNew((Asset) fieldValue, type, "Plane");
            if(selected != null)
            {
                Mesh mesh = null;
                if(selected.equals("Plane")) mesh = new PlaneMesh();
        
                fieldValue = mesh;
                modified.set(true);
            }
            Asset dragAndDrop = ImGuiUtils.assetDragAndDropTarget(type);
            if(dragAndDrop != null)
            {
                modified.set(true);
                fieldValue = dragAndDrop;
            }
        }
        else if(Material.class.equals(fieldType))
        {
            AssetType type = AssetType.MATERIAL;
            ImGui.sameLine();
            
            String selected = EditorProperty.createNew((Asset) fieldValue, type, "Standard", "Shader");
            if(selected != null)
            {
                Material material = null;
                if(selected.equals("Standard")) material = new StandardMaterial();
                else if(selected.equals("Shader")) material = new ShaderMaterial();
        
                fieldValue = material;
                modified.set(true);
            }
            Asset dragAndDrop = ImGuiUtils.assetDragAndDropTarget(type);
            if(dragAndDrop != null)
            {
                modified.set(true);
                fieldValue = dragAndDrop;
            }
        }
        else if(Model.class.equals(fieldType))
        {
            AssetType type = AssetType.MODEL;
            ImGui.sameLine();
            if(fieldValue == null)
            {
                String text = "Add Model";
                if(ImGui.beginCombo("##Model Viewer Combo", text))
                {
                    ImGui.endCombo();
                }
                Asset dragAndDrop = ImGuiUtils.assetDragAndDropTarget(type);
                if(dragAndDrop != null)
                {
                    modified.set(true);
                    fieldValue = dragAndDrop;
                }
            }
        }
        else if(Shader.class.isAssignableFrom(fieldType))
        {
            AssetType type;
            if(fieldType.equals(FragmentShader.class)) type = AssetType.FRAGMENT;
            else type = AssetType.VERTEX;
            
            ImGui.sameLine();
    
            String selected = EditorProperty.createNew((Asset) fieldValue, type, "Shader");
            if(selected != null)
            {
                Shader shader = null;
                if(selected.equals("Shader"))
                {
                    shader = (type == AssetType.VERTEX ? new VertexShader() : new FragmentShader());
                }
        
                fieldValue = shader;
                modified.set(true);
            }
            Asset dragAndDrop = ImGuiUtils.assetDragAndDropTarget(type);
            if(dragAndDrop != null)
            {
                modified.set(true);
                fieldValue = dragAndDrop;
            }
        }
        return fieldValue;
    }
    
    
    

}
