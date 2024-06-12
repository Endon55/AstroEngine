package com.anthonycosenza.engine.space;

import java.lang.reflect.Field;

public class ProjectSettings
{
    public String name = "None";
    public long mainScene = -1;
    public int width = 1920;
    public int height = 1080;
    public boolean vsync = false;
    public boolean centered = true;
    public int monitor = 0;
    
    public boolean enableSystemDiagnostics = true;
    
    public int lwjglStackSize = 2056;
    public boolean guiMultiViewport = false;
    public boolean guiDocking = true;
    public boolean guiHideViewportTaskbar = true;
    
    public boolean lockMouse = false;
    
    
    public static ProjectSettings duplicate(ProjectSettings toDuplicate)
    {
        ProjectSettings duplicate = new ProjectSettings();
        for(Field field : ProjectSettings.class.getDeclaredFields())
        {
            try
            {
                field.set(duplicate, field.get(toDuplicate));
            } catch(IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
        }
        return duplicate;
    }
}
