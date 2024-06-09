package com.anthonycosenza.editor.scene.nodes;

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
import com.anthonycosenza.engine.util.ImGuiUtils;
import com.anthonycosenza.engine.util.math.Color;
import imgui.ImColor;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiDataType;
import imgui.flag.ImGuiStyleVar;
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
    private static int MAX_STRING_FIELD_LENGTH = 20;
    
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
                text = String.valueOf(asset.getResourceID());
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
    
    public static void propertyTable(Object object, ImBoolean modified)
    {
        propertyTable(object.getClass(), object, modified);
    }
    
    public static void propertyTable(Class<?> clazz, Object object, ImBoolean modified)
    {
        ImGui.pushStyleVar(ImGuiStyleVar.CellPadding, 2, 1);
        if(ImGui.beginTable("##Property Table - " + clazz.getSimpleName(), 2,
                ImGuiTableFlags.SizingFixedFit | ImGuiTableFlags.NoHostExtendX))
        {
            List<Field> fields = Arrays.stream(clazz.getDeclaredFields()).filter(
                    field -> !Modifier.isTransient(field.getModifiers()) &&
                            !Modifier.isStatic(field.getModifiers()) &&
                            !field.getName().equals("resourceID")).toList();
            float longestName = 0;
            float[] fieldNameLengths = new float[fields.size()];
            for(int i = 0; i < fields.size(); i++)
            {
                float length = ImGui.calcTextSize(fields.get(i).getName()).x;
                fieldNameLengths[i] = length;
                if(length > longestName) longestName = length;
            }
            
            ImGui.tableSetupColumn("## property column", ImGuiTableColumnFlags.WidthFixed, longestName);
            ImGui.tableSetupColumn("## value column", ImGuiTableColumnFlags.WidthStretch);

            for(int i = 0; i < fields.size(); i++)
            {
                Field field = fields.get(i);
                field.setAccessible(true);
                
                ImGui.tableNextRow();
                
                ImGui.tableSetColumnIndex(0);
                ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
                ImGui.text(field.getName());
                
                ImGui.tableSetColumnIndex(1);
                try
                {
                    ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());
                    Object value = EditorProperty.createInputField(field.getType(), field.getName(), field.get(object), modified);
                    field.set(object, value);
            
                } catch(IllegalAccessException e)
                {
                    throw new RuntimeException(e);
                }
            }
    
            ImGui.endTable();
        }
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
            if(fieldValue != null)
            {
                quaternion = ((Quaternionf) fieldValue);
                imValue[0] = quaternion.x();
                imValue[1] = quaternion.y();
                imValue[2] = quaternion.z();
            }
            else
            {
                quaternion = new Quaternionf();
                fieldValue = quaternion;
            }
            if(ImGui.inputFloat3("##" + fieldName, imValue))
            {
                quaternion.set(imValue[0], imValue[1], imValue[2], 1);
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
            if(fieldValue == null)
            {
                Mesh mesh = EditorProperty.createNewMesh(type);
                if(mesh != null)
                {
                    fieldValue = mesh;
                    modified.set(true);
                    mesh.initialize();
                }
                Asset dragAndDrop = ImGuiUtils.assetDragAndDropTarget(type);
                if(dragAndDrop != null)
                {
                    modified.set(true);
                    fieldValue = dragAndDrop;
                }
            }
            else
            {
                propertyTable(fieldValue.getClass(), fieldValue, modified);
                if(modified.get())
                {
                    ((Mesh) fieldValue).initialize();
                }
                
            }
        }
        else if(Material.class.equals(fieldType))
        {
            AssetType type = AssetType.MATERIAL;
            ImGui.sameLine();
            if(fieldValue == null)
            {
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
            else
            {
                propertyTable(StandardMaterial.class, fieldValue, modified);
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
        else
        {
            ImGui.text("Implement - " + fieldType.getSimpleName());
        }
        return fieldValue;
    }
    public static Material materialDropdown(Material material, ImBoolean modified)
    {
        boolean exists = material != null;
    
        if(beginDropdownCombo(AssetType.MATERIAL, material))
        {
            if(ImGui.selectable("new Material"))
            {
            
            }
        
        
            ImGui.endCombo();
        }
        Material dragAndDrop = (Material) ImGuiUtils.assetDragAndDropTarget(AssetType.MATERIAL);
        if(dragAndDrop != null)
        {
            modified.set(true);
            return dragAndDrop;
        }
    
        if(exists)
        {
            int tableConfig = 0;
            ImGui.indent();
            ImGui.pushStyleColor(ImGuiCol.TableRowBg, ImColor.rgba(1f,1f,1f, 1f));
            if(ImGui.beginTable("Material Property Editor", COLUMN_COUNT, tableConfig))
            {
                ImGui.tableNextColumn();
                ImGui.text("Mesh");
                ImGui.tableNextColumn();
                ImGui.text("Implement");
            
                ImGui.tableNextColumn();
                ImGui.text("Whatever");
                ImGui.tableNextColumn();
                ImGui.text("Implement");
            
                ImGui.endTable();
            }
            ImGui.unindent();
            ImGui.popStyleColor();
        }
        return material;
    }

    
    private static boolean beginDropdownCombo(AssetType type, Asset asset)
    {
        String typeStr = type.name().toLowerCase();
        return ImGui.beginCombo("##-" + typeStr + "ModelDropdown", (asset != null ?
                new File(AssetManager.getInstance().getAssetInfo(asset.getResourceID()).filePath()).getName()
                : "Pick " + typeStr));
    }
    public static Model modelDropdown(Model model, ImBoolean modified)
    {
        boolean exists = model != null;
        
        if(beginDropdownCombo(AssetType.MODEL, model))
        {
            if(ImGui.selectable("new Model"))
            {
            
            }
        
        
            ImGui.endCombo();
        }
        Model dragAndDrop = (Model) ImGuiUtils.assetDragAndDropTarget(AssetType.MODEL);
        if(dragAndDrop != null)
        {
            modified.set(true);
            return dragAndDrop;
        }
        
        if(exists)
        {
            int tableConfig = 0;
            ImGui.indent();
            ImGui.pushStyleColor(ImGuiCol.TableRowBg, ImColor.rgba(1f, 1f, 1f, 1f));
            if(ImGui.beginTable("Model Property Editor", COLUMN_COUNT, tableConfig))
            {
                ImGui.tableNextColumn();
                ImGui.text("Mesh");
                ImGui.tableNextColumn();
                ImGui.text("Implement");
        
                ImGui.tableNextColumn();
                Material material = materialDropdown(model.getMaterials().get(0), modified);
                ImGui.tableNextColumn();
                ImGui.text("Implement");
        
                ImGui.endTable();
            }
            ImGui.unindent();
            ImGui.popStyleColor();
        }
        return model;
    }
}
