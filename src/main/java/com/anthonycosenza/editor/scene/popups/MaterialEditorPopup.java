package com.anthonycosenza.editor.scene.popups;

import com.anthonycosenza.engine.space.rendering.materials.Material;
import com.anthonycosenza.engine.space.rendering.materials.StandardMaterial;
import com.anthonycosenza.engine.util.ImGuiUtils;
import com.anthonycosenza.engine.util.math.Color;
import imgui.ImGui;
import org.joml.Vector2i;


public class MaterialEditorPopup implements Popup
{
    private boolean finished = false;
    private StandardMaterial material;
    private Vector2i windowSize = new Vector2i(400, 300);
    int tableConfig = 0;
    int columns = 2;
    float[] diffuse;
    
    public MaterialEditorPopup(StandardMaterial material)
    {
        this.material = material;
        diffuse = material.diffuseColor.getAsArray();
    }
    @Override
    public void create()
    {
        
        if(ImGuiUtils.createPopupWindow(windowSize))
        {
            if(ImGuiUtils.activeAndEscHit())
            {
                finished = true;
                material = null;
                ImGui.end();
                return;
            }
            if(ImGui.beginTable("Material Editor", columns, tableConfig))
            {
                ImGui.tableNextColumn();
                ImGui.text("Texture");
                ImGui.tableNextColumn();
                ImGui.text("Implement texture picking");
                ImGui.tableNextColumn();
                ImGui.text("Diffuse");
                
                if(ImGui.colorPicker4("##Diffuse Color Picker", diffuse, 0))
                {
                
                }
    
                ImGui.tableNextColumn();
                if(ImGui.button("Accept"))
                {
                    copyOver();
                    finished = true;
                }
                ImGui.tableNextColumn();
                if(ImGui.button("Cancel"))
                {
                    finished = true;
                }
                ImGui.endTable();
            }
        }
        ImGui.end();
    }
    private void copyOver()
    {
        material.diffuseColor = new Color(diffuse);
    }
    
    @Override
    public boolean isFinished()
    {
        return finished;
    }
    
    @Override
    public Material finish()
    {
        return material;
    }
}
