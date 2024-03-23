package com.anthonycosenza.engine.window;

import com.anthonycosenza.engine.Engine;

public class WindowOptions
{
    public boolean compatibleProfile;
    public int fps;
    public int ups = Engine.TARGET_UPS;
    
    public int width;
    public int height;
    
    public WindowOptions()
    {
        this.fps = 0;
        this.width = 0;
        this.height = 0;
    }
    public WindowOptions(int targetFPS, int width, int height)
    {
        this.fps = targetFPS;
        this.width = width;
        this.height = height;
    }
}
