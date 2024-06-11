package com.anthonycosenza.editor.scene.popups;

import com.anthonycosenza.editor.EditorIO;
import com.anthonycosenza.engine.util.ImGuiUtils;
import com.anthonycosenza.engine.util.NativeFileDialogue;
import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import org.joml.Vector2i;

public class ImportPopup implements Popup
{
    private final Vector2i popupSize = new Vector2i(650, 350);
    ImBoolean makeCopy = new ImBoolean(false);
    ImString assetPath = new ImString(100);
    boolean finished = false;
    public ImportPopup()
    {
        assetPath.set(EditorIO.getProjectDirectory());
    }
    @Override
    public void create()
    {
    
        ImGui.beginTooltip();
        if(ImGuiUtils.createPopupWindow(popupSize))
        {
            if(ImGuiUtils.activeAndEscHit())
            {
                finished = true;
                assetPath = null;
                ImGui.end();
                return;
            }
            ImGui.text("Asset Importer");
            ImGui.separator();
            ImGui.setTooltip("Put a copy of this asset in the assets directory?");
            ImGui.checkbox("Make Copy", makeCopy);
            
            ImGui.setNextItemWidth(ImGui.getContentRegionAvailX() - 55);
            ImGui.inputText("##-Asset input", assetPath);
            ImGui.sameLine();
            ImGui.setNextItemWidth(55);
            if(ImGui.button("Browse"))
            {
                String path = NativeFileDialogue.openFile();
                assetPath.set(path);
            }
            if(ImGui.button("Import"))
            {
                finished = true;
            }
        }
        ImGui.end();
        ImGui.endTooltip();
    }
    
    @Override
    public boolean isFinished()
    {
        return finished;
    }
    
    @Override
    public String finish()
    {
        return assetPath.get();
    }
}
