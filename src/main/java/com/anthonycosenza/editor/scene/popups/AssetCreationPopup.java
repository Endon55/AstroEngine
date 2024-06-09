package com.anthonycosenza.editor.scene.popups;

import com.anthonycosenza.engine.assets.Asset;
import com.anthonycosenza.engine.assets.AssetManager;
import com.anthonycosenza.engine.assets.AssetType;
import com.anthonycosenza.engine.util.ImGuiUtils;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.type.ImString;
import org.joml.Vector2i;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AssetCreationPopup implements Popup
{
    private static final List<AssetType> types = new ArrayList<>(List.of(AssetType.values())).stream().filter(AssetType::isImplemented).toList();
    private static final List<String> typesString = types.stream().map(Enum::name).toList();
    
    private boolean finished = false;
    private Vector2i windowSize = new Vector2i(350, 150);
    
    private int typeSelection = 0;
    private ImString filename = new ImString(20);
    private String error = "";
    private File directory;
    private Asset asset;
    private boolean shouldSelectText = true;
    
    public AssetCreationPopup(AssetType defaultType, File directory)
    {
        typeSelection = types.indexOf(defaultType);
        if(typeSelection == -1)
        {
            System.out.println("Default Type either doesn't exist or doesn't have a creation function. Defaulting to element 0");
            typeSelection = 0;
        }
        this.directory = directory;
    }
    public AssetCreationPopup(File directory)
    {
        this.directory = directory;
    }
    
    @Override
    public void create()
    {
        if(ImGuiUtils.createPopupWindow(windowSize))
        {
            ImGui.text("Save As...");
            ImGui.separator();
            
            ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);
            ImGui.setNextItemWidth(ImGui.getWindowSizeX());
            if(!error.isEmpty())
            {
                ImGuiUtils.error(error);
            }
            if(shouldSelectText)
            {
                ImGui.setKeyboardFocusHere();
                shouldSelectText = false;
            }
            if(ImGui.inputText("##2", filename, ImGuiInputTextFlags.EnterReturnsTrue))
            {
                AssetType assetType = types.get(typeSelection);
                String filename = directory.getPath() + "\\" + this.filename.get() + "." + assetType.getExtension();
                File file = new File(filename);
                if(file.exists())
                {
                    error = "File name already in use";
                }
                else
                {
                    finished = true;
                    asset = AssetManager.getInstance().createNewAsset(directory, this.filename.get(), assetType);
                }
            }
            ImGui.popStyleVar();
            ImGui.spacing();
            ImGui.separator();
            ImGui.spacing();
        
            for(int i = 0; i < typesString.size(); i++)
            {
                if(ImGui.selectable(typesString.get(i), typeSelection == i))
                {
                    typeSelection = i;
                    ImGui.setItemDefaultFocus();
                    shouldSelectText = true;
                }
                else if(typeSelection == i)
                {
                    ImGui.setItemDefaultFocus();
                }
            }
        }
        ImGui.end();
    }
    
    @Override
    public boolean isFinished()
    {
        return finished;
    }
    
    @Override
    public Asset finish()
    {
        return asset;
    }
}
