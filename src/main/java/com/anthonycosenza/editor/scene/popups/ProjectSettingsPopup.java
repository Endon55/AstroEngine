package com.anthonycosenza.editor.scene.popups;

import com.anthonycosenza.engine.space.ProjectSettings;
import com.anthonycosenza.engine.util.ImGuiUtils;
import imgui.ImGui;
import org.joml.Vector2i;

public class ProjectSettingsPopup implements Popup
{
    private boolean finished = false;
    private ProjectSettings settings;
    private ProjectSettings tempSettings;
    private Vector2i windowSize = new Vector2i(350, 500);
    int[] screenDimensions;
    public ProjectSettingsPopup(ProjectSettings settings)
    {
        this.settings = settings;
        this.tempSettings = ProjectSettings.duplicate(settings);
        
        
        
        screenDimensions = new int[]{settings.width, settings.height};
    }
    @Override
    public void create()
    {
        if(ImGuiUtils.createPopupWindow(windowSize))
        {
            ImGui.text("Settings");
            ImGui.separator();
            ImGui.text("Window Dimensions");
            if(ImGui.inputInt2("##-screendim", screenDimensions))
            {
                tempSettings.width = screenDimensions[0];
                tempSettings.height = screenDimensions[1];
            }
            
            
            if(ImGui.button("Accept"))
            {
                copyOver();
                finished = true;
            }
            if(ImGui.button("Cancel"))
            {
                finished = true;
            }
            
            ImGui.end();
        }
    }
    
    private void copyOver()
    {
        settings = tempSettings;
    }
    
    
    @Override
    public boolean isFinished()
    {
        return finished;
    }
    
    @Override
    public ProjectSettings finish()
    {
        return settings;
    }
}
